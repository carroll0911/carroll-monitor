package com.carroll.monitor.analyzer.service.impl;

import com.carroll.auth.entity.TokenMessage;
import com.carroll.cache.RedisUtil;
import com.carroll.monitor.analyzer.config.PassportConf;
import com.carroll.monitor.analyzer.dto.OperatorDto;
import com.carroll.monitor.analyzer.dto.UserCacheDto;
import com.carroll.monitor.analyzer.enums.ErrEnum;
import com.carroll.monitor.analyzer.exception.MonitorBaseException;
import com.carroll.monitor.analyzer.model.MonitorItem;
import com.carroll.monitor.analyzer.model.Operator;
import com.carroll.monitor.analyzer.model.UserProject;
import com.carroll.monitor.analyzer.repository.MonitorItemRepository;
import com.carroll.monitor.analyzer.repository.OperatorRepository;
import com.carroll.monitor.analyzer.repository.UserProjectRepository;
import com.carroll.monitor.analyzer.request.*;
import com.carroll.monitor.analyzer.response.LoginResponse;
import com.carroll.monitor.analyzer.response.OperatorListResponse;
import com.carroll.monitor.analyzer.response.OperatorPageResponse;
import com.carroll.monitor.analyzer.response.OperatorResponse;
import com.carroll.monitor.analyzer.service.IOperatorService;
import com.carroll.monitor.analyzer.utils.BizContext;
import com.carroll.monitor.analyzer.utils.EmailUtils;
import com.carroll.monitor.analyzer.utils.PageUtil;
import com.carroll.monitor.analyzer.utils.TokenUtils;
import com.carroll.spring.rest.starter.BaseException;
import com.carroll.spring.rest.starter.BaseResponse;
import com.carroll.utils.BeanUtils;
import com.carroll.utils.Md5Util;
import com.carroll.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.carroll.monitor.analyzer.enums.ErrEnum.LOGIN_ERROR;


/**
 * @author: carroll
 * @date 2019/9/9
 */
@Service
@Slf4j
public class OperatorServiceImpl extends BaseServiceImpl implements IOperatorService {
    private static final String REDIS_TOKEN_KEY = "_portal_token";

    private static final String USER_CACHE_KEY = "user_info_";

    @Autowired
    private OperatorRepository operatorRepository;

    @Autowired
    private MonitorItemRepository monitorItemRepository;
    @Autowired
    private UserProjectRepository userProjectRepository;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private EmailUtils emailUtils;
    @Autowired
    private PassportConf passportConf;

    @Override
    public OperatorResponse save(OperatorRequest request) {
        checkPermission();
        OperatorResponse response = new OperatorResponse();
        verifyEmailAndMobile(null, request.getEmail(), request.getMobile());
        Operator operator = convertRequestToModel(request);
        operator.setPassword(Md5Util.md5Encode(StringUtil.generatePwd()));
        operatorRepository.save(operator);
        response.setData(convertModelToDto(operator));
        emailUtils.sendEmail(new String[]{operator.getEmail()}, "【监控平台】账号变更通知",
                String.format("监控平台管理员已为您创建账户：%s，登录初始密码为：%s，请尽快登录修改密码", operator.getEmail(), operator.getPassword()));
        return response;
    }

    @Override
    @CacheEvict(value = {"monitorItem", "monitorItem#2*60*60", "monitorItem#list#byProject#2*60*60",
            "monitorItem#list#byLevel#2*60*60", "monitorItem#list#ByReceiver#2*60*60"}, allEntries = true)
    public OperatorResponse update(OperatorUpdateRequest request) {
        checkPermission();
        OperatorResponse response = new OperatorResponse();
        Operator operator = operatorRepository.findOne(request.getId());
        if (operator == null) {
            throw new BaseException(ErrEnum.DATA_NOT_EXIST.getCode(), ErrEnum.DATA_NOT_EXIST.getMsg());
        }
        verifyEmailAndMobile(request.getId(), request.getEmail(), request.getMobile());
        List<MonitorItem> items = monitorItemRepository.findByReceivers(operator);
        Date now = new Date();
        if (!CollectionUtils.isEmpty(items)) {
            items.forEach(item -> {
                item.getReceivers().forEach(receiver -> {
                    if (receiver.getId().equals(request.getId())) {
                        BeanUtils.copyPropertiesIgnorException(request, receiver);
                    }
                    receiver.setUpdateTime(now);
                    monitorItemRepository.save(item);
                });
            });
        }
        BeanUtils.copyPropertiesIgnorException(request, operator);
        operator.setUpdateTime(now);
        operatorRepository.save(operator);
        response.setData(convertModelToDto(operator));
        return response;
    }

    @Override
    @CacheEvict(value = {"monitorItem", "monitorItem#2*60*60", "monitorItem#list#byProject#2*60*60",
            "monitorItem#list#byLevel#2*60*60", "monitorItem#list#ByReceiver#2*60*60"}, allEntries = true)
    public BaseResponse delete(IdRequest request) {
        if (StringUtils.isEmpty(request.getId())) {
            throw new BaseException(ErrEnum.OPERATOR_ID_NOT_BLANK.getCode(), ErrEnum.OPERATOR_ID_NOT_BLANK.getMsg());
        }
        Operator operator = operatorRepository.findOne(request.getId());
        if (null == operator) {
            throw new BaseException(ErrEnum.DATA_NOT_EXIST.getCode(), ErrEnum.DATA_NOT_EXIST.getMsg());
        }
        List<MonitorItem> items = monitorItemRepository.findByReceivers(operator);
        if (!CollectionUtils.isEmpty(items)) {
            throw new BaseException(ErrEnum.MONITORITEM_OPERATOR_ALREADY_BIND.getCode(), ErrEnum.MONITORITEM_OPERATOR_ALREADY_BIND.getMsg());
        }
        operatorRepository.delete(request.getId());
        return new BaseResponse();
    }

    @Override
    public OperatorPageResponse page(OperatorPageRequest request) {
        OperatorPageResponse response = new OperatorPageResponse();
        List<OperatorDto> list = new ArrayList<>();
        response.setList(list);
        List<String> projects = getCurrentUserProjects(request.getProjectId());
        if (projects == null) {
            return response;
        }
        Page<Operator> page = operatorRepository.advanceQuery(projects, PageUtil.convertPageRequestToPageable(request));
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
    public OperatorListResponse list(BmBaseRequest request) {
        OperatorListResponse response = new OperatorListResponse();
        List<String> projects = getCurrentUserProjects(request.getProjectId());
        if (projects == null) {
            return response;
        }
        List<Operator> list;
        if (CollectionUtils.isEmpty(projects)) {
            list = operatorRepository.findAll();
        } else {
            List<UserProject> ups = userProjectRepository.findAllByProjectIdIsIn(projects);
            if (CollectionUtils.isEmpty(ups)) {
                response.setList(new ArrayList<>());
                return response;
            }
            List<String> uids = new ArrayList<>();
            ups.forEach(up -> {
                uids.add(up.getUserId());
            });
            list = operatorRepository.findAllByIdIsIn(uids);
        }

        List<OperatorDto> responseList = new ArrayList();
        list.forEach(operator -> {
            responseList.add(convertModelToDto(operator));
        });
        response.setList(responseList);
        return response;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Operator operator = operatorRepository.findTopByEmailOrMobile(request.getPhoneOrEmail(), request.getPhoneOrEmail());
        if (operator == null || !request.getPassword().equalsIgnoreCase(operator.getPassword())) {
            throw new MonitorBaseException(LOGIN_ERROR);
        }
        List<UserProject> ups = userProjectRepository.findAllByUserId(operator.getId());
        TokenMessage tokenMessage = null;
        try {
            tokenMessage = TokenUtils.createToken((long) operator.getId().hashCode(), operator.getMobile());
            if (tokenMessage != null) {
                String token = tokenMessage.getToken();
                redisUtil.set(operator.getMobile() + REDIS_TOKEN_KEY, token, passportConf.getTokenExpireTime());
                UserCacheDto cacheDto = new UserCacheDto(operator.getId(), operator.getName(), operator.getMobile(), operator.getEmail(),
                        new HashMap<>(), operator.getRole());
                for (UserProject up : ups) {
                    cacheDto.getProjects().put(up.getProjectId(), up.getRole());
                }
                redisUtil.set(USER_CACHE_KEY + operator.getMobile(), cacheDto);
                return new LoginResponse(token, operator.getRole(), operator.getName(), operator.getEmail(), operator.getMobile());
            }
        } catch (Exception e) {
            log.error("登录失败：{}", e.getMessage(), e);
        }
        return new LoginResponse(LOGIN_ERROR.getCode(), LOGIN_ERROR.getMsg());
    }

    @Override
    public BaseResponse logout() {
        BaseResponse response = new BaseResponse();
        try {
            UserCacheDto cacheDto = (UserCacheDto) BizContext.getData(BizContext.MONITOR_USER_CACHE);
            if (cacheDto != null) {
                String key = cacheDto.getMobile() + REDIS_TOKEN_KEY;

                String token = (String) redisUtil.get(key);
                if (!StringUtils.isEmpty(token)) {
                    redisUtil.remove(key);
                }
                response.setReturnSuccess(true);
            } else {
                throw new BaseException(com.carroll.monitor.analyzer.enums.ErrEnum.NOT_LOGIN_ERROR.getCode(),
                        com.carroll.monitor.analyzer.enums.ErrEnum.NOT_LOGIN_ERROR.getMsg());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            response.setReturnSuccess(false);
            response.setReturnErrCode(com.carroll.monitor.analyzer.enums.ErrEnum.LOG_OUT_ERROR.getCode());
            response.setReturnErrMsg(com.carroll.monitor.analyzer.enums.ErrEnum.LOG_OUT_ERROR.getMsg());
        }
        return response;
    }

    @Override
    public UserCacheDto getUserCache(String userId) {
        return (UserCacheDto) redisUtil.get(USER_CACHE_KEY + userId);
    }

    @Override
    public void modifyPwd(ModifyPasswordReq req) {
        UserCacheDto cacheDto = (UserCacheDto) BizContext.getData(BizContext.MONITOR_USER_CACHE);
        if (cacheDto == null) {
            throw new MonitorBaseException(com.carroll.monitor.analyzer.enums.ErrEnum.NOT_LOGIN_ERROR);
        }
        Operator operator = operatorRepository.findOne(cacheDto.getId());
        if (operator == null) {
            throw new MonitorBaseException(com.carroll.monitor.analyzer.enums.ErrEnum.USER_NOT_EXISTS);
        }
        if (!req.getOldPwd().equalsIgnoreCase(operator.getPassword())) {
            throw new MonitorBaseException(com.carroll.monitor.analyzer.enums.ErrEnum.PWD_ERROR.getCode(), "原始密码错误");
        }
        operator.setPassword(req.getNewPwd());
        operatorRepository.save(operator);
        logout();
    }

    @Override
    public void resetPwd(String userId) {
        checkPermission();
        Operator operator = operatorRepository.findOne(userId);
        if (operator == null) {
            throw new MonitorBaseException(com.carroll.monitor.analyzer.enums.ErrEnum.USER_NOT_EXISTS);
        }
        String password = StringUtil.generatePwd();
        operator.setPassword(Md5Util.md5Encode(password));
        emailUtils.sendEmail(new String[]{operator.getEmail()}, "【监控平台】账号变更通知",
                String.format("监控平台管理员已将您的账户：%s 密码重置为：%s，请尽快登录修改密码", operator.getEmail(), password));
        operatorRepository.save(operator);
    }

    @Override
    public void updateMyInfo(String email, String mobile) {
        UserCacheDto cacheDto = (UserCacheDto) BizContext.getData(BizContext.MONITOR_USER_CACHE);
        if (cacheDto == null) {
            throw new MonitorBaseException(com.carroll.monitor.analyzer.enums.ErrEnum.NOT_LOGIN_ERROR);
        }
        Operator operator = operatorRepository.findOne(cacheDto.getId());
        if (operator == null) {
            throw new MonitorBaseException(com.carroll.monitor.analyzer.enums.ErrEnum.USER_NOT_EXISTS);
        }
        verifyEmailAndMobile(cacheDto.getId(), email, mobile);
        operator.setEmail(email);
        operator.setMobile(mobile);
        operatorRepository.save(operator);
    }

    private void verifyEmailAndMobile(String id, String email, String mobile) {
        Operator operatorEmail;
        Operator operatorMobile;
        if (StringUtils.isEmpty(id)) {
            operatorEmail = operatorRepository.findByEmail(email);
            operatorMobile = operatorRepository.findByMobile(mobile);
        } else {
            operatorEmail = operatorRepository.findByIdIsNotAndEmail(id, email);
            operatorMobile = operatorRepository.findByIdIsNotAndMobile(id, mobile);
        }
        if (operatorEmail != null) {
            throw new BaseException(ErrEnum.OPERATOR_EMAIL_ALREADY_EXIST.getCode(), ErrEnum.OPERATOR_EMAIL_ALREADY_EXIST.getMsg());
        }
        if (operatorMobile != null) {
            throw new BaseException(ErrEnum.OPERATOR_SMS_ALREADY_EXIST.getCode(), ErrEnum.OPERATOR_SMS_ALREADY_EXIST.getMsg());
        }
    }

    public Operator convertRequestToModel(OperatorRequest request) {
        Operator operator = new Operator();
        BeanUtils.copyPropertiesIgnorException(request, operator);
        operator.setUpdateTime(new Date());
        return operator;
    }

    public OperatorDto convertModelToDto(Operator operator) {
        OperatorDto dto = new OperatorDto();
        if (null != operator) {
            BeanUtils.copyPropertiesIgnorException(operator, dto);
            return dto;
        }
        return null;
    }
}
