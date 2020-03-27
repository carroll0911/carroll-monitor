package com.carroll.monitor.analyzer.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.carroll.cache.RedisUtil;
import com.carroll.monitor.analyzer.dto.*;
import com.carroll.monitor.analyzer.enums.ErrEnum;
import com.carroll.monitor.analyzer.enums.StatisticsType;
import com.carroll.monitor.analyzer.model.MonitorItem;
import com.carroll.monitor.analyzer.model.Project;
import com.carroll.monitor.analyzer.model.WarningData;
import com.carroll.monitor.analyzer.model.WarningDataHistory;
import com.carroll.monitor.analyzer.repository.*;
import com.carroll.monitor.analyzer.request.CurrentMonitorRequest;
import com.carroll.monitor.analyzer.request.HistoryMonitorPageRequest;
import com.carroll.monitor.analyzer.request.IdRequest;
import com.carroll.monitor.analyzer.request.MonitorStatisticRequest;
import com.carroll.monitor.analyzer.response.*;
import com.carroll.monitor.analyzer.service.IProjectService;
import com.carroll.monitor.analyzer.service.IWarningDataService;
import com.carroll.monitor.analyzer.utils.OkHttpUtil;
import com.carroll.monitor.analyzer.utils.PageUtil;
import com.carroll.monitor.common.dto.KafkaTopic;
import com.carroll.spring.rest.starter.BaseException;
import com.carroll.utils.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Slf4j
@Service
public class WarningDataServiceImpl extends BaseServiceImpl implements IWarningDataService {

    @Autowired
    private MonitorItemRepository monitorItemRepository;

    @Autowired
    private WarningDataRepository warningDataRepository;
    @Autowired
    private WarningDataHistoryRepository warningDataHistoryRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private WarningDataMongoDao warningDataMongoDao;
    @Autowired
    private IProjectService projectService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Value("${dateNum}")
    private Integer num;

    @Value("${days}")
    private Integer days = 5;

    @Value("${host}")
    private String hostSuffix;
    @Value("${traceUrl:}")
    private String traceUrl;

    @Autowired
    private RedisUtil redisUtil;

    private static final String WARNING_DATA_KEY_PREFIX = "WARNING_DATA";
    private static final String STATUS_KEY = "status";
    private static final String PROJECT_ID_KEY = "projectId";
    private static final String ITEM_ID_KEY = "itemId";
    private static final String WARNING_DATA_KEY = "warningData";
    private static final String WARNING_DATA_HISTORY_KEY = "warningDataHistory";
    private static final String TIMES_KEY = "times";

    @Override
    public WarningData getCurrentData(String itemId, String applicationName, String host, String target) {
        String key = getWarningDataRedisKey(itemId, applicationName, host, target);
        WarningData data = (WarningData) redisUtil.get(key);
        boolean inRedis = true;
        if (data == null) {
            inRedis = false;
            List<WarningData> datas = warningDataRepository.findWarnData(itemId, applicationName, WarningData.Status.NORMAL, host, target);
            data = datas.isEmpty() ? null : datas.get(0);
            datas = null;
        }
        if (data == null) {
            data = new WarningData();
        }
        if (!inRedis) {
            redisUtil.set(key, data, days * 24 * 3600L);
        }
        if (StringUtils.isEmpty(data.getItemId()) || WarningData.Status.CLEARED.equals(data.getStatus())) {
            return null;
        }
        return data;

    }

    @Override
    public WarningData save(WarningData warningData) {
        String key = getWarningDataRedisKey(warningData.getItemId(), warningData.getApplicationName(), warningData.getHost(), warningData.getTarget());
        warningData = warningDataRepository.save(warningData);
        if (WarningData.Status.CLEARED.equals(warningData.getStatus())) {
            redisUtil.remove(key);
            WarningDataHistory history = new WarningDataHistory();
            history.setApplicationName(warningData.getApplicationName());
            history.setItemId(warningData.getItemId());
            history.setFirstTime(warningData.getFirstTime());
            history.setLatestTime(warningData.getLatestTime());
            history.setTimes(warningData.getTimes());
            history.setCycleTimes(warningData.getCycleTimes());
            history.setLastSendTime(warningData.getLastSendTime());
            history.setRecoveryTime(warningData.getRecoveryTime());
            history.setSuccessTimes(warningData.getSuccessTimes());
            history.setStatus(warningData.getStatus());
            history.setHost(warningData.getHost());
            history.setTarget(warningData.getTarget());
            history.setProjectId(warningData.getProjectId());
            history.setLogs(warningData.getLogs());
            history.setId(warningData.getId());
            history.setCreateTime(warningData.getCreateTime());
            history.setUpdateTime(warningData.getUpdateTime());
            warningDataHistoryRepository.save(history);
            warningDataRepository.delete(warningData.getId());
        } else {
            redisUtil.set(key, warningData, days * 24 * 3600L);
        }

        return warningData;
    }

    private String getWarningDataRedisKey(String itemId, String applicationName, String host, String target) {
        return String.format("%s_%s_%s_%s_%s", WARNING_DATA_KEY_PREFIX, itemId, applicationName, host, target);
    }

    @Override
    public void clearUselessData() {
        Date now = new Date();
        Date endDay = DateUtils.addDays(now, days * -1);
        Query query = new Query();
        Criteria criteria = Criteria.where("updateTime").lte(endDay).and(STATUS_KEY).is(WarningData.Status.NORMAL);
        query.addCriteria(criteria);
        long count = warningDataMongoDao.getPageCount(query);
        log.debug("共查到 " + count + " 条僵尸数据");
        if (count > 0) {
            int dataNumber = num;
            int batch;
            if (count % dataNumber == 0) {
                batch = (int) (count / dataNumber);
            } else {
                batch = (int) (count / dataNumber + 1);
            }
            SpringDataPageable pageable = new SpringDataPageable();
            List<WarningData> list = null;
            for (int i = 1; i <= batch; i++) {
                pageable.setPagenumber(i);
                pageable.setPagesize(dataNumber);
                list = warningDataMongoDao.getPage(query, pageable);
                list.forEach(data -> {
                    data.setStatus(WarningData.Status.CLEARED);
                    data.setLastSendTime(new Date());

                    MonitorItem monitorItem = monitorItemRepository.findOne(data.getItemId());
                    if (null != monitorItem) {
                        NotifyDataDto notifyDataDto = new NotifyDataDto(false, monitorItem, data);
                        kafkaTemplate.send(KafkaTopic.NOTIFY_DATA, JSON.toJSONString(notifyDataDto));
                    }
                    this.save(data);
                });
            }
        }
    }

    @Override
    public void resendData() {
        Query query = new Query();
        Criteria criteria = Criteria.where(STATUS_KEY).is(WarningData.Status.NORMAL).and("lastSendTime").ne(null);
        query.addCriteria(criteria);
        long count = warningDataMongoDao.getPageCount(query);
        log.debug("status is normal、lastsendtime is not null的总条数：" + count);
        if (count > 0) {
            int dataNumber = num;
            int batch;
            if (count % dataNumber == 0) {
                batch = (int) (count / dataNumber);
            } else {
                batch = (int) (count / dataNumber + 1);
            }
            SpringDataPageable pageable = new SpringDataPageable();
            List<WarningData> list = null;
            log.debug("处理批次数：" + batch);
            for (int i = 1; i <= batch; i++) {
                pageable.setPagenumber(i);
                pageable.setPagesize(dataNumber);
                list = warningDataMongoDao.getPage(query, pageable);
                list.forEach(data -> {
                    log.debug("告警id：" + data.getId());
                    MonitorItem monitorItem = monitorItemRepository.findOne(data.getItemId());
                    if (null != monitorItem && monitorItem.getSendFlag()) {
                        log.debug("告警项id：" + monitorItem.getId());
                        Date now = new Date();
                        if (null != monitorItem.getCycle() && null != monitorItem.getCycleTimes() && null != data.getCycleTimes()) {
                            Date nextSendTime = DateUtils.addMinutes(data.getLastSendTime(), monitorItem.getCycle().intValue());
                            if (now.after(nextSendTime) && data.getCycleTimes() >= monitorItem.getCycleTimes()) {
                                NotifyDataDto notifyDataDto = new NotifyDataDto(false, monitorItem, data);
                                log.debug("发送告警数据： " + JSONObject.toJSONString(notifyDataDto));
                                kafkaTemplate.send(KafkaTopic.NOTIFY_DATA, JSON.toJSONString(notifyDataDto));
                                data.setLastSendTime(now);
                                data.setCycleTimes(0L);
                                this.save(data);
                            }
                        }
                    }
                });
            }
        }
    }

    /**
     * 查询历史告警
     *
     * @param request
     * @return
     */
    @Override
    public HistoryMonitorPageResponse historyMonitor(HistoryMonitorPageRequest request) {
        List<WarningDataPageDto> list = new ArrayList<>();
        HistoryMonitorPageResponse response = new HistoryMonitorPageResponse();
        List<String> projects = getCurrentUserProjects(request.getProjectId());
        if (projects == null) {
            // 当前用户不是超级管理员且未关联任何应用直接返回空列表
            return response;
        }
        List<String> itemIds = getItemsIds(projects, request.getItemId(), request.getLevel());

        Page<WarningDataHistory> page = null;
        page = warningDataHistoryRepository.advanceQuery(request.getFirstStartTime(),
                request.getFirstEndTime(),
                request.getRecoveryStartTime(),
                request.getRecoveryEndTime(),
                itemIds, request.getApplicationName(), request.getTarget(), projects, PageUtil.convertPageRequestToPageable(request));
        if (page != null) {
            response = PageUtil.convertPageToPageResponse(page, response);
            page.getContent().forEach(data -> {
                MonitorItem item = monitorItemRepository.findOne(data.getItemId());
                WarningDataPageDto dto = this.convertModelToDto(data, item);
                list.add(dto);
            });
            response.setList(list);
        }

        return response;
    }

    public List<String> verifyPorjectAndGetMonitorItemIds(String projectId, String projectTag, String itemId, String level) {
        Project project = projectRepository.findOne(projectId);
        if (null == project) {
            throw new BaseException(ErrEnum.DATA_NOT_EXIST.getCode(), ErrEnum.DATA_NOT_EXIST.getMsg());
        }
        if (!project.getTag().equals(projectTag)) {
            throw new BaseException(ErrEnum.DATA_NOT_EXIST.getCode(), ErrEnum.DATA_NOT_EXIST.getMsg());
        }

        List<MonitorItem> items = monitorItemRepository.advanceQuery(Arrays.asList(project.getId()), itemId, level);
        List<String> itemIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(items)) {
            items.forEach(item -> {
                itemIds.add(item.getId());
            });
        }
        return itemIds;
    }

    /**
     * 查询实时告警
     *
     * @param request
     * @return
     */
    @Override
    public CurrentMonitorResponse currentMonitor(CurrentMonitorRequest request) {
        CurrentMonitorResponse response = new CurrentMonitorResponse();
        List<CurrentMonitorDto> list = new ArrayList<>();
        List<WarningDataStatisticDto> statisticList = new ArrayList<>();
        List<String> projects = getCurrentUserProjects(request.getProjectId());
        if (projects == null) {
            // 当前用户不是超级管理员且未关联任何应用直接返回空列表
            return response;
        }
        List<String> itemIds = getItemsIds(projects, request.getItemId(), request.getLevel());

        Page<WarningData> page = null;
        page = warningDataRepository.advanceQueryByPage(request.getFirstStartTime(),
                request.getFirstEndTime(),
                request.getUpdateStartTime(),
                request.getUpdateEndTime(),
                itemIds, request.getApplicationName(), request.getTarget(), projects, PageUtil.convertPageRequestToPageable(request));
        if (page != null) {
            response = PageUtil.convertPageToPageResponse(page, response);
            page.getContent().forEach(data -> {
                MonitorItem item = monitorItemRepository.findOne(data.getItemId());
                list.add(convertModelToCurrentMonitorDto(data, item));
            });
        }
        response.setList(list);
        //实时面板统计告警
        for (MonitorItem.Level level : MonitorItem.Level.values()) {
            WarningDataStatisticDto dto = new WarningDataStatisticDto();
            dto.setDesc(level.getDesc());
            dto.setStatus(WarningData.Status.NORMAL.getDesc());
            if (!StringUtils.isEmpty(request.getLevel())) {
                if (level.name().equals(request.getLevel())) {
                    dto.setTimes(response.getTotalElements());
                } else {
                    dto.setTimes(0L);
                }
            } else {
                List<String> ids = new ArrayList<>();
                List<MonitorItem> items = monitorItemRepository.advanceQuery(projects, request.getItemId(), level.name());
                if (!CollectionUtils.isEmpty(items)) {
                    items.forEach(item -> {
                        ids.add(item.getId());
                    });
                }
                if (!CollectionUtils.isEmpty(ids)) {
                    int count = warningDataRepository.currentStatistic(request.getFirstStartTime(),
                            request.getFirstEndTime(), request.getUpdateStartTime(), request.getUpdateEndTime(),
                            ids, request.getApplicationName(), request.getTarget(), WarningData.Status.NORMAL.name(), projects);
                    dto.setTimes((long) count);
                } else {
                    dto.setTimes(0L);
                }

            }
            statisticList.add(dto);
        }
        response.setStatisticList(statisticList);
        return response;
    }

    /**
     * 查询告警详情
     *
     * @param request
     * @return
     */
    @Override
    public MonitorDetailResponse monitorDetail(IdRequest request) {
        MonitorDetailResponse response = new MonitorDetailResponse();
        WarningData data = warningDataRepository.findOne(request.getId());
        WarningDataHistory history = warningDataHistoryRepository.findOne(request.getId());
        if (null == data && history == null) {
            throw new BaseException(ErrEnum.WARNINGDATA_NOT_EXIST.getCode(), ErrEnum.WARNINGDATA_NOT_EXIST.getMsg());
        }
        List<String> projectIds = getCurrentUserProjects(null);
        String projectId = null == data ? history.getProjectId() : data.getProjectId();
        if (projectIds == null || (projectIds.size() > 0 && !projectIds.contains(projectId))) {
            throw new BaseException(ErrEnum.WARNINGDATA_NOT_EXIST.getCode(), ErrEnum.WARNINGDATA_NOT_EXIST.getMsg());
        }
        MonitorItem item = monitorItemRepository.findOne(null == data ? history.getItemId() : data.getItemId());
        if (null == item) {
            throw new BaseException(ErrEnum.WARNINGDATA_PROJECT_NOT_MATCH.getCode(), ErrEnum.WARNINGDATA_PROJECT_NOT_MATCH.getMsg());
        }
        WarningDataDetailDto dto = new WarningDataDetailDto();
        dto.setLevel(item.getLevel().getDesc());
        dto.setCategory(item.getCategory().getDesc());
        List<LogDataDto> logDtos = new ArrayList<>();
        if (data == null) {
            BeanUtils.copyPropertiesIgnorException(item, dto);
            BeanUtils.copyPropertiesIgnorException(history, dto);

            history.getLogs().forEach(log -> {
                LogDataDto logDto = new LogDataDto();
                BeanUtils.copyPropertiesIgnorException(log, logDto);
                logDtos.add(logDto);
            });
            dto.setLogs(logDtos);
            dto.setStatus(history.getStatus().getDesc());
            dto.setTarget(history.getTarget());
            dto.setHost(null != history.getHost() ? history.getHost() : hostSuffix.substring(1, hostSuffix.length()));
        } else {
            BeanUtils.copyPropertiesIgnorException(item, dto);
            BeanUtils.copyPropertiesIgnorException(data, dto);
            data.getLogs().forEach(log -> {
                LogDataDto logDto = new LogDataDto();
                BeanUtils.copyPropertiesIgnorException(log, logDto);
                logDtos.add(logDto);
            });
            dto.setLogs(logDtos);
            dto.setStatus(data.getStatus().getDesc());
            dto.setTarget(data.getTarget());
            dto.setHost(null != data.getHost() ? data.getHost() : hostSuffix.substring(1, hostSuffix.length()));
        }
        response.setData(dto);
        return response;
    }

    @Override
    @Cacheable(value = "statistic#2*60")
    public MonitorStatisticResponse statistic(MonitorStatisticRequest request) {
        MonitorStatisticResponse response = new MonitorStatisticResponse();
        List<List<WarningDataStatisticDto>> responseList = new ArrayList<>();
        List<WarningDataStatisticDto> dtoList = new ArrayList<>();
        Project project = projectRepository.findOne(request.getProjectId());
        if (null == project) {
            throw new BaseException(ErrEnum.DATA_NOT_EXIST.getCode(), ErrEnum.DATA_NOT_EXIST.getMsg());
        }

        if (!checkPermission(request.getProjectId())) {
            throw new BaseException(ErrEnum.DATA_NOT_EXIST.getCode(), ErrEnum.DATA_NOT_EXIST.getMsg());
        }
        //type不传时，查询normal状态的数据总条数
        if (StringUtils.isEmpty(request.getType())) {
            if (!StringUtils.isEmpty(request.getStatus())) {
                WarningDataStatisticDto dto = new WarningDataStatisticDto();
                int count = warningDataRepository.currentStatistic(null, null, null,
                        null, null, null, null, request.getStatus(), Arrays.asList(request.getProjectId()));
                dto.setTimes((long) count);
                dto.setDesc(WarningData.Status.ALL.getDesc());
                dto.setStatus(WarningData.Status.valueOf(request.getStatus()).getDesc());
                dtoList.add(dto);
                responseList.add(dtoList);
                response.setList(responseList);
            } else {
                throw new BaseException(ErrEnum.WARNINGDATA_STATUS_NOT_NULL.getCode(), ErrEnum.WARNINGDATA_STATUS_NOT_NULL.getMsg());
            }
        } else {
            //根据告警源统计
            if (request.getType().equals(StatisticsType.SOURCE.name())) {
                dtoList = statisticBySource(project.getId(), request.getStatus());
                response.setList(responseList);
            }

            //根据告警名称统计
            if (request.getType().equals(StatisticsType.NAME.name())) {
                dtoList = statisticByName(project.getId(), request.getStatus());
                response.setList(responseList);
            }

            //根据告警级别统计
            if (request.getType().equals(StatisticsType.LEVEL.name())) {
                dtoList = statisticByLevel(request.getProjectId(), request.getStatus());
                response.setList(responseList);
            }
            dtoList.forEach(dto -> {
                //根据告警源统计时，applicationName为空的，过滤掉
                if (null != dto.getDesc()) {
                    List<WarningDataStatisticDto> subList = new ArrayList<>();
                    if (responseList.size() > 0) {
                        for (List<WarningDataStatisticDto> existSubList : responseList) {
                            if (existSubList.get(0).getDesc().equals(dto.getDesc())) {
                                subList = existSubList;
                                break;
                            }
                        }
                        responseList.remove(subList);
                    }
                    subList.add(dto);
                    responseList.add(subList);
                }
            });
        }
        return response;
    }

    @Override
    public TraceDetailResponse getTraceDetail(String traceId) {
        TraceDetailResponse response = new TraceDetailResponse();
        try {
            String responseStr = OkHttpUtil.doGet(String.format("%s?id=%s", traceUrl, traceId));
            response.setData(JSONObject.parseObject(responseStr));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return response;
    }

    private List<WarningDataStatisticDto> statisticBySource(String projectId, String status) {
        List<WarningDataStatisticDto> dtoList = new ArrayList<>();
        List<WarningDataStatisticDto> dtoListHis = new ArrayList<>();
        Criteria criteria = null;
        if (!StringUtils.isEmpty(projectId)) {
            criteria = Criteria.where(PROJECT_ID_KEY).is(projectId);
        }
        if (!StringUtils.isEmpty(status)) {
            if (criteria == null) {
                criteria = Criteria.where(STATUS_KEY).is(status);
            } else {
                criteria.and(STATUS_KEY).is(status);
            }
        }
        TypedAggregation<WarningData> agg = newAggregation(WarningData.class,
                project(PROJECT_ID_KEY, "applicationName", TIMES_KEY, STATUS_KEY),
                criteria != null ? match(criteria) : null,
                group("applicationName", STATUS_KEY).sum(TIMES_KEY).as(TIMES_KEY)
        );
        AggregationResults<WarningData> results = mongoTemplate.aggregate(agg, WARNING_DATA_KEY, WarningData.class);
        List<WarningData> list = results.getMappedResults();
        list.forEach(data -> {
            WarningDataStatisticDto dto = new WarningDataStatisticDto();
            dto.setDesc(data.getApplicationName());
            dto.setTimes(data.getTimes());
            dto.setStatus(data.getStatus().getDesc());
            dtoList.add(dto);
        });
        AggregationResults<WarningDataHistory> resultsHis = mongoTemplate.aggregate(agg, WARNING_DATA_HISTORY_KEY, WarningDataHistory.class);
        List<WarningDataHistory> listHis = resultsHis.getMappedResults();
        listHis.forEach(data -> {
            WarningDataStatisticDto dto = new WarningDataStatisticDto();
            dto.setDesc(data.getApplicationName());
            dto.setTimes(data.getTimes());
            dto.setStatus(data.getStatus().getDesc());
            dtoListHis.add(dto);
        });
        return mergeStatisticList(dtoList, dtoListHis);
    }

    /**
     * 合并统计结果
     *
     * @param allList
     * @return
     */
    private List<WarningDataStatisticDto> mergeStatisticList(List<WarningDataStatisticDto>... allList) {
        Map<String, WarningDataStatisticDto> dtoMap = new HashMap<>();
        String key = null;
        WarningDataStatisticDto dto = null;
        for (List<WarningDataStatisticDto> l : allList) {
            for (WarningDataStatisticDto data : l) {
                key = String.format("%s_%s", data.getDesc(), data.getStatus());
                dto = dtoMap.get(key);
                if (dto == null) {
                    dto = new WarningDataStatisticDto();
                    dto.setDesc(data.getDesc());
                    dto.setTimes(0L);
                    dto.setStatus(data.getStatus());
                    dtoMap.put(key, dto);
                }
                dto.setTimes(dto.getTimes() + data.getTimes());
            }
        }
        List<WarningDataStatisticDto> result = new ArrayList<>();
        result.addAll(dtoMap.values());
        return result;
    }

    private List<WarningDataStatisticDto> statisticByName(String projectId, String status) {
        List<WarningDataStatisticDto> responseList = new ArrayList<>();
        List<WarningDataStatisticDto> responseListHis = new ArrayList<>();
        Criteria criteria = null;
        if (!StringUtils.isEmpty(projectId)) {
            criteria = Criteria.where(PROJECT_ID_KEY).is(projectId);
        }
        if (!StringUtils.isEmpty(status)) {
            if (criteria == null) {
                criteria = Criteria.where(STATUS_KEY).is(status);
            } else {
                criteria.and(STATUS_KEY).is(status);
            }
        }
        TypedAggregation<WarningData> agg = newAggregation(WarningData.class,
                project(PROJECT_ID_KEY, ITEM_ID_KEY, TIMES_KEY, STATUS_KEY),
                criteria == null ? null : match(criteria),
                group(PROJECT_ID_KEY, ITEM_ID_KEY, STATUS_KEY).sum(TIMES_KEY).as(TIMES_KEY)
        );
        AggregationResults<WarningData> results = mongoTemplate.aggregate(agg, WARNING_DATA_KEY, WarningData.class);
        List<WarningData> list = results.getMappedResults();
        list.forEach(data -> {
            WarningDataStatisticDto dto = new WarningDataStatisticDto();
            dto.setDesc(monitorItemRepository.findOne(data.getItemId()).getName());
            dto.setTimes(data.getTimes());
            dto.setStatus(data.getStatus().getDesc());
            responseList.add(dto);
        });
        AggregationResults<WarningDataHistory> resultsHis = mongoTemplate.aggregate(agg, WARNING_DATA_HISTORY_KEY, WarningDataHistory.class);
        List<WarningDataHistory> listHis = resultsHis.getMappedResults();
        listHis.forEach(data -> {
            WarningDataStatisticDto dto = new WarningDataStatisticDto();
            dto.setDesc(monitorItemRepository.findOne(data.getItemId()).getName());
            dto.setTimes(data.getTimes());
            dto.setStatus(data.getStatus().getDesc());
            responseListHis.add(dto);
        });
        return mergeStatisticList(responseList, responseListHis);
    }

    /**
     * 根据level统计告警数据
     *
     * @param projectId
     * @param status
     * @return
     */
    private List<WarningDataStatisticDto> statisticByLevel(String projectId, String status) {
        List<WarningDataStatisticDto> list = new ArrayList<>();
        for (MonitorItem.Level level : MonitorItem.Level.values()) {
            List<String> monitorItemIds = getItemsIds(Arrays.asList(projectId), null, level.name());
            if (StringUtils.isEmpty(status)) {
                for (WarningData.Status status1 : WarningData.Status.values()) {
                    if (!status1.equals(WarningData.Status.ALL)) {
                        searchStatisticByLevel(monitorItemIds, level, list, status1.getCode());
                    }
                }
            } else {
                searchStatisticByLevel(monitorItemIds, level, list, status);
            }
        }
        return list;
    }

    private List<String> getItemsIds(List<String> projectIds, String itemId, String level) {
        List<MonitorItem> monitorItems = monitorItemRepository.advanceQuery(projectIds, itemId, level);
        List<String> monitorItemIds = new ArrayList<>();
        monitorItems.forEach(item -> {
            monitorItemIds.add(item.getId());
        });
        return monitorItemIds;
    }

    private void searchStatisticByLevel(List<String> monitorItemIds, MonitorItem.Level level,
                                        List<WarningDataStatisticDto> list, String status) {
        WarningDataStatisticDto dto = new WarningDataStatisticDto();
        dto.setDesc(level.getDesc());
        dto.setStatus(StringUtils.isEmpty(status) ? WarningData.Status.ALL.getDesc() : WarningData.Status.valueOf(status).getDesc());
        if (CollectionUtils.isEmpty(monitorItemIds)) {
            dto.setTimes(0L);
        } else {
            Criteria criteria = null;
            criteria = Criteria.where(STATUS_KEY).is(status).and(ITEM_ID_KEY).in(monitorItemIds);
            TypedAggregation<WarningData> agg = newAggregation(WarningData.class,
                    match(criteria),
                    group(STATUS_KEY).sum(TIMES_KEY).as(TIMES_KEY)
            );
            AggregationResults<WarningData> results = mongoTemplate.aggregate(agg, WARNING_DATA_KEY, WarningData.class);
            List<WarningData> result = results.getMappedResults();
            dto.setTimes(CollectionUtils.isEmpty(result) ? Long.valueOf(0L) : result.get(0).getTimes());
            AggregationResults<WarningDataHistory> resultsHis = mongoTemplate.aggregate(agg, WARNING_DATA_HISTORY_KEY, WarningDataHistory.class);
            List<WarningDataHistory> resultHis = resultsHis.getMappedResults();
            if (!CollectionUtils.isEmpty(resultHis)) {
                dto.setTimes(dto.getTimes() + resultHis.get(0).getTimes());
            }
        }
        list.add(dto);
    }

    private WarningDataPageDto convertModelToDto(WarningDataHistory data, MonitorItem item) {
        WarningDataPageDto dto = new WarningDataPageDto();
        BeanUtils.copyPropertiesIgnorException(item, dto);
        BeanUtils.copyPropertiesIgnorException(data, dto);
        dto.setLevel(item.getLevel().getDesc());
        dto.setTarget(data.getTarget());
        if (!StringUtils.isEmpty(data.getProjectId())) {
            Project project = projectService.getByID(data.getProjectId());
            if (project != null) {
                dto.setProjectName(project.getName());
            }
        }
        return dto;
    }

    private CurrentMonitorDto convertModelToCurrentMonitorDto(WarningData data, MonitorItem item) {
        CurrentMonitorDto dto = new CurrentMonitorDto();
        BeanUtils.copyPropertiesIgnorException(item, dto);
        BeanUtils.copyPropertiesIgnorException(data, dto);
        dto.setLevel(item.getLevel().getDesc());
        dto.setTarget(data.getTarget());
        if (!StringUtils.isEmpty(data.getProjectId())) {
            Project project = projectService.getByID(data.getProjectId());
            if (project != null) {
                dto.setProjectName(project.getName());
            }
        }

        return dto;
    }

}
