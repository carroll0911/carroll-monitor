package com.carroll.monitor.analyzer.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.carroll.monitor.analyzer.dto.BaseDto;
import com.carroll.monitor.analyzer.dto.MonitorDataEx;
import com.carroll.monitor.analyzer.dto.MonitorItemDto;
import com.carroll.monitor.analyzer.dto.MonitorItemListDto;
import com.carroll.monitor.analyzer.enums.ErrEnum;
import com.carroll.monitor.analyzer.exception.MonitorBaseException;
import com.carroll.monitor.analyzer.model.MonitorItem;
import com.carroll.monitor.analyzer.model.Operator;
import com.carroll.monitor.analyzer.model.Project;
import com.carroll.monitor.analyzer.model.UserProject;
import com.carroll.monitor.analyzer.repository.*;
import com.carroll.monitor.analyzer.request.*;
import com.carroll.monitor.analyzer.response.MonitorItemListResponse;
import com.carroll.monitor.analyzer.response.MonitorItemPageResponse;
import com.carroll.monitor.analyzer.response.MonitorItemResponse;
import com.carroll.monitor.analyzer.service.IMonitorItemService;
import com.carroll.monitor.analyzer.service.IProjectService;
import com.carroll.monitor.analyzer.utils.PageUtil;
import com.carroll.monitor.analyzer.utils.ScriptUtil;
import com.carroll.spring.rest.starter.BaseException;
import com.carroll.spring.rest.starter.BaseResponse;
import com.carroll.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.carroll.monitor.analyzer.enums.ErrEnum.DATA_EXIST;
import static com.carroll.monitor.analyzer.enums.ErrEnum.ITEM_TAG_ALREADY_EXIST;


/**
 * @author: carroll
 * @date 2019/9/9
 */
@Service
public class MonitorItemServiceImpl extends BaseServiceImpl implements IMonitorItemService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MonitorItemRepository monitorItemRepository;

    @Autowired
    private OperatorRepository operatorRepository;
    @Autowired
    private UserProjectRepository userProjectRepository;
    @Autowired
    private IProjectService projectService;
    @Autowired
    private WarningDataRepository warningDataRepository;
    @Autowired
    private WarningDataHistoryRepository warningDataHistoryRepository;

    private static final String TIME_TAG = "TIME";

    @Override
    @Cacheable(value = "monitorItem#5*60")
    public MonitorItem getByTag(String tag, String projectId) {
        MonitorItem item = monitorItemRepository.findTopByTagAndProjectId(tag, projectId);
        //数据库中不存在则缓存一个新的空对象，防止缓存的穿透
        if (item == null) {
            item = new MonitorItem();
        }
        return item;
    }

    @Override
    public MonitorItem getById(String id) {
        return monitorItemRepository.findOne(id);
    }

    @Override
    public MonitorItem save(MonitorItem item) {
        return monitorItemRepository.save(item);
    }

    @Override
    public MonitorItemListResponse list(BmBaseRequest request) {
        MonitorItemListResponse response = new MonitorItemListResponse();
        List<String> projects = getCurrentUserProjects(request.getProjectId());
        if (projects == null) {
            return response;
        }
//        verifyProject(request.getProjectId(), request.getProjectTag());
        List<MonitorItem> list = null;
        if (projects.isEmpty()) {
            list = monitorItemRepository.findAll();
        } else {
            list = monitorItemRepository.findByProjectIdIsIn(projects);
        }

        response.setList(convertModelToDto(list));
        return response;
    }

    @Override
    public MonitorItemResponse detail(IdRequest request) {
        MonitorItemResponse response = new MonitorItemResponse();
        MonitorItem item = monitorItemRepository.findOne(request.getId());
        if (null == item) {
            throw new BaseException(ErrEnum.DATA_NOT_EXIST.getCode(), ErrEnum.DATA_NOT_EXIST.getMsg());
        }
        // 校验是否有权限
        if (!checkPermission(item.getProjectId())) {
            throw new BaseException(ErrEnum.DATA_NOT_EXIST.getCode(), ErrEnum.DATA_NOT_EXIST.getMsg());
        }
        response.setData(convertModelToDto(item));
        return response;
    }

    @Override
    public MonitorItemPageResponse page(MonitorItemPageRequest request) {
        MonitorItemPageResponse response = new MonitorItemPageResponse();
        List<MonitorItemDto> list = new ArrayList<>();
        response.setList(list);
        List<String> projects = getCurrentUserProjects(request.getProjectId());
        if (projects == null) {
            return response;
        }
        Page<MonitorItem> page = monitorItemRepository.advanceQuery(projects, request.getKeyword(), PageUtil.convertPageRequestToPageable(request));
        if (page != null) {
            response = PageUtil.convertPageToPageResponse(page, response);
            page.getContent().forEach(operator -> {
                list.add(convertModelToDto(operator));
            });
        }
        response.setList(list);
        return response;
    }

    @Override
    @CacheEvict(value = {"monitorItem", "monitorItem#2*60*60", "monitorItem#list#byProject#2*60*60",
            "monitorItem#list#byLevel#2*60*60", "monitorItem#list#ByReceiver#2*60*60"}, allEntries = true)
    public MonitorItemResponse update(MonitorItemUpdateRequest request) {
        MonitorItemResponse response = new MonitorItemResponse();
        MonitorItem item = verify(request.getId());
        if (!checkPermission(item.getProjectId(), true)) {
            throw new MonitorBaseException(com.carroll.monitor.analyzer.enums.ErrEnum.NO_PERMISSION_ERROR);
        }
        BeanUtils.copyPropertiesIgnorException(request, item);
        item.setCategory(StringUtils.isEmpty(request.getCategory()) ? null : MonitorItem.Category.valueOf(request.getCategory()));
        item.setLevel(StringUtils.isEmpty(request.getLevel()) ? null : MonitorItem.Level.valueOf(request.getLevel()));
        item.setDescription(StringUtils.isEmpty(request.getDescription()) ? null : request.getDescription());
        item.setSuggest(StringUtils.isEmpty(request.getSuggest()) ? null : request.getSuggest());
        monitorItemRepository.save(item);
        response.setData(convertModelToDto(item));
        return response;
    }

    @Override
    @CacheEvict(value = {"monitorItem", "monitorItem#2*60*60", "monitorItem#list#byProject#2*60*60",
            "monitorItem#list#byLevel#2*60*60", "monitorItem#list#ByReceiver#2*60*60"}, allEntries = true)
    public BaseResponse notify(MonitorItemNotifyRequest request) {
        BaseResponse response = new BaseResponse();
        MonitorItem item = verify(request.getId());
//        verifyProject(request.getProjectId(), request.getProjectTag());
//        if (!item.getProjectId().equals(request.getProjectId())) {
//            throw new BaseException(ErrEnum.MONITORITEM_PROJECT_NOT_MATCH.getCode(), ErrEnum.MONITORITEM_PROJECT_NOT_MATCH.getMsg());
//        }
        verify(item.getProjectId(), request.getCycle(), request.getReceivers());
        item = convertRequestToModel(item, request);
        List<Operator> receivers = new ArrayList<>();
        request.getReceivers().forEach(dto -> {
            Operator operator = operatorRepository.findOne(dto.getId());
            receivers.add(operator);
        });
        item.setReceivers(receivers);
        monitorItemRepository.save(item);
        return response;
    }

    @Override
    public MonitorItemResponse save(MonitorItemRequest request) {
        MonitorItemResponse response = new MonitorItemResponse();
        if (!checkPermission(request.getProjectId(), true)) {
            throw new MonitorBaseException(com.carroll.monitor.analyzer.enums.ErrEnum.NO_PERMISSION_ERROR);
        }
        Project project = projectRepository.findOne(request.getProjectId());
        if (null == project) {
            throw new BaseException(ErrEnum.PROJECT_NOT_EXIST.getCode(), ErrEnum.PROJECT_NOT_EXIST.getMsg());
        }
        MonitorItem item = monitorItemRepository.findTopByTagAndProjectId(request.getTag(), request.getProjectId());
        if (item != null) {
            throw new MonitorBaseException(ITEM_TAG_ALREADY_EXIST);
        }
        item = convertRequestToModel(request);
        monitorItemRepository.save(item);
        response.setData(convertModelToDto(item));
        return response;
    }

    @Override
    public void initSystemMonitorItem() {
        List<Project> projects = projectRepository.findAll();
        MonitorItem monitorItem = null;
        for (Project p : projects) {
            monitorItem = monitorItemRepository.findTopByTagAndProjectId(TIME_TAG, p.getId());
            // 不存在则新增时间同步监控项
            if (monitorItem == null) {
                monitorItem = new MonitorItem();
                monitorItem.setTag(TIME_TAG);
                monitorItem.setCategory(MonitorItem.Category.SYSTEM);
                monitorItem.setCycle(10L);
                monitorItem.setCycleTimes(100L);
                monitorItem.setLevel(MonitorItem.Level.MAJOR);
                monitorItem.setProjectId(p.getId());
                monitorItem.setName("时间同步监控");
                monitorItem.setTimes(100L);
                monitorItem.setRecoveryTimes(100L);
                monitorItem.setSendFlag(false);
                monitorItem.setMsgTypes(new ArrayList<>());
                monitorItem.getMsgTypes().add(MonitorItem.MessageType.EMAIL);
                monitorItem.getMsgTypes().add(MonitorItem.MessageType.SMS);
                monitorItemRepository.save(monitorItem);
            }
        }
    }

    @Override
    public boolean isSuccess(MonitorItem monitorItem, MonitorDataEx data) {
        if (StringUtils.isEmpty(monitorItem.getResultScript())) {
            return data.isResult();
        }
        String preScript = "var src_data=eval('('+data+')');";
        Object rs = ScriptUtil.execute(preScript + monitorItem.getResultScript(), JSONObject.toJSONString(data));
        if (null != rs && rs instanceof Boolean) {
            return (Boolean) rs;
        }
        return data.isResult();
    }

    @Override
    public void changeStatus(String id, MonitorItem.Status status) {
        MonitorItem item = verify(id);
        if (!checkPermission(item.getProjectId(), true)) {
            throw new MonitorBaseException(com.carroll.monitor.analyzer.enums.ErrEnum.NO_PERMISSION_ERROR);
        }
        if (status.equals(item.getStatus())) {
            return;
        }
        item.setStatus(status);
        monitorItemRepository.save(item);
    }

    @Override
    public void delete(String id) {
        MonitorItem item = verify(id);
        //判断是否已有告警数据
        if (warningDataRepository.countAllByItemId(id) > 0 || warningDataHistoryRepository.countAllByItemId(id) > 0) {
            throw new MonitorBaseException(DATA_EXIST.getCode(), "存在监控数据，不能删除该监控项");
        }
        monitorItemRepository.delete(id);
    }

    private void verify(String projectId, Long cycle, List<BaseDto> receivers) {
        if (null != cycle && cycle % 5 != 0) {
            throw new BaseException(ErrEnum.MONITORITEM_CYCLE_ERROR.getCode(), ErrEnum.MONITORITEM_CYCLE_ERROR.getMsg());
        }
        List<UserProject> ups = userProjectRepository.findAllByProjectId(projectId);
        List<String> users = new ArrayList<>();
        ups.forEach(up -> {
            users.add(up.getUserId());
        });
        receivers.forEach(receiver -> {
            Operator operator = operatorRepository.findOne(receiver.getId());
            if (null == operator) {
                throw new BaseException(ErrEnum.OPERATOR_NOT_EXIST.getCode(), ErrEnum.OPERATOR_NOT_EXIST.getMsg());
            }

            if (!users.contains(receiver.getId())) {
                throw new BaseException(ErrEnum.OPERATOR_PROJECT_NOT_MATCH.getCode(), ErrEnum.OPERATOR_PROJECT_NOT_MATCH.getMsg());
            }
        });
    }

    private MonitorItem verify(String id) {
        MonitorItem item = monitorItemRepository.findOne(id);
        if (null == item) {
            throw new BaseException(ErrEnum.DATA_NOT_EXIST.getCode(), ErrEnum.DATA_NOT_EXIST.getMsg());
        }
        return item;
    }

    private MonitorItemDto convertModelToDto(MonitorItem item) {
        MonitorItemDto dto = new MonitorItemDto();
        BeanUtils.copyPropertiesIgnorException(item, dto);
        Project project = projectService.getByID(item.getProjectId());
        if (project != null) {
            dto.setProjectName(project.getName());
        }
        dto.setLevel(null == item.getLevel() ? null : item.getLevel().getDesc());
        dto.setLevelCode(null == item.getLevel() ? null : item.getLevel().name());
        dto.setCategory(null == item.getCategory() ? null : item.getCategory().getDesc());
        dto.setCategoryCode(null == item.getCategory() ? null : item.getCategory().name());
        List<String> msgTypes = new ArrayList<>();
        if (item.getMsgTypes() != null) {
            item.getMsgTypes().forEach(msgType -> {
                msgTypes.add(msgType.name());
            });
        }
        dto.setMsgTypes(msgTypes);
        if (item.getReceivers() == null) {
            dto.setReceivers(new ArrayList<>());
        }
        dto.setStatus(item.getStatus().name());
        return dto;
    }

    private List<MonitorItemListDto> convertModelToDto(List<MonitorItem> items) {
        List<MonitorItemListDto> list = new ArrayList<>();
        items.forEach(item -> {
            MonitorItemListDto dto = new MonitorItemListDto();
            dto.setName(item.getName());
            dto.setId(item.getId());
            list.add(dto);
        });
        return list;
    }

    private MonitorItem convertRequestToModel(MonitorItem item, MonitorItemNotifyRequest request) {
        BeanUtils.copyPropertiesIgnorException(request, item);
        item.setCycle(null == request.getCycle() ? null : request.getCycle());
        item.setCycleTimes(null == request.getCycleTimes() ? null : request.getCycleTimes());
        List<MonitorItem.MessageType> msgTypes = new ArrayList<>();
        request.getMsgTypes().forEach(msgType -> {
            msgTypes.add(MonitorItem.MessageType.valueOf(msgType));
        });
        item.setMsgTypes(msgTypes);
        return item;
    }

    private MonitorItem convertRequestToModel(MonitorItemRequest request) {
        MonitorItem item = new MonitorItem();
        BeanUtils.copyPropertiesIgnorException(request, item);
        List<MonitorItem.MessageType> msgTypes = new ArrayList<>();
        if (request.getMsgTypes() != null) {
            request.getMsgTypes().forEach(msgType -> {
                msgTypes.add(MonitorItem.MessageType.valueOf(msgType));
            });
        }
        item.setMsgTypes(msgTypes);
        item.setLevel(MonitorItem.Level.valueOf(request.getLevel()));
        item.setCategory(MonitorItem.Category.valueOf(request.getCategory()));
        return item;
    }
}
