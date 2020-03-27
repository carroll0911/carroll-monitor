package com.carroll.monitor.analyzer.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.carroll.cache.LockUtils;
import com.carroll.monitor.analyzer.config.ThreadPoolConfig;
import com.carroll.monitor.analyzer.dto.MonitorDataEx;
import com.carroll.monitor.analyzer.dto.NotifyDataDto;
import com.carroll.monitor.analyzer.model.*;
import com.carroll.monitor.analyzer.service.IItemSummaryRecordService;
import com.carroll.monitor.analyzer.service.IMonitorItemService;
import com.carroll.monitor.analyzer.service.IProjectService;
import com.carroll.monitor.analyzer.service.IWarningDataService;
import com.carroll.monitor.common.dto.KafkaTopic;
import com.carroll.monitor.common.utils.PasswordUtils;
import com.carroll.utils.DateUtils;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.*;

/**
 * 监控数据处理
 * @author: carroll
 * @date 2019/9/9
 */
@Component
@Slf4j
public class MonitorDataConsumer {

    @Autowired
    private IProjectService projectService;
    @Autowired
    private IMonitorItemService monitorItemService;
    @Autowired
    private IWarningDataService warningDataService;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private SimpMessagingTemplate template;
    @Value("${logSize}")
    private int logSize = 50;
    @Autowired
    private LockUtils lockUtils;
    @Autowired
    private IItemSummaryRecordService itemSummaryRecordService;

    private static final long LOCK_TIME = 2000L;

    private static final String TIME_TAG = "TIME";

    @Value("${syncTimeValue}")
    private Long syncTimeValue = 60000L;

    // 时钟同步监控采样时间间隔
    @Value("${syncTimeValue:30000}")
    private Long clockMonitorRate = 30000L;

    @Autowired
    private ThreadPoolConfig threadPoolConfig;
    private ExecutorService fixedThreadPool;
    private ExecutorService timeFixedThreadPool;

    /* 上报URL本地缓存 时间 */
    private Map<String, Long> clockMonitorTimeMap = new HashMap<>();

    @PostConstruct
    @SuppressWarnings("unused")
    private void init() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("monitor-data-runner-%d").build();
        fixedThreadPool = new ThreadPoolExecutor(threadPoolConfig.getCorePoolSize(), threadPoolConfig.getMaxPoolSize(),
                threadPoolConfig.getKeepAliveTimeMs(), TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(threadPoolConfig.getBlockQueueSize()), namedThreadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy());
        timeFixedThreadPool = new ThreadPoolExecutor(threadPoolConfig.getCorePoolSize(), threadPoolConfig.getMaxPoolSize(),
                threadPoolConfig.getKeepAliveTimeMs(), TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(threadPoolConfig.getBlockQueueSize()), namedThreadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * 监控数据分析监听
     *
     * @param cr
     */
    @KafkaListener(topics = {KafkaTopic.MONITOR_DATA}, group = KafkaTopic.MONITOR_DATA_GROUP)
    @SuppressWarnings("unused")
    public void monitorData(ConsumerRecord<?, ?> cr) {
        Optional<?> messages = Optional.ofNullable(cr.value());
        messages.ifPresent(m -> fixedThreadPool.execute(() -> {
            analize(m.toString(), true);
        }));
        cr = null;
    }

    /**
     * 时间同步分析 监听
     *
     * @param cr
     */
    @KafkaListener(topics = {KafkaTopic.MONITOR_DATA}, group = KafkaTopic.MONITOR_TIME_GROUP, containerFactory = "timeContainerFactory")
    @SuppressWarnings("unused")
    public void monitorTime(ConsumerRecord<?, ?> cr) {
        Optional<?> messages = Optional.ofNullable(cr.value());
        messages.ifPresent(m -> timeFixedThreadPool.execute(() -> {
            analize(m.toString(), false);
        }));
        cr = null;
    }

    /**
     * 监控数据分析
     *
     * @param content 监控数据
     * @param isData  是否是监控数据（fals=时间同步的监控, true=业务监控）
     * @return
     */
    public WarningData analize(String content, boolean isData) {
        log.debug("MonitorDataConsumer message:" + content);
        JSONObject jsonObject = null;
        try {
            jsonObject = JSON.parseObject(content);
        } catch (Exception e) {
            log.debug("monitor data content illegal:" + content, e);
            return null;
        }
        MonitorDataEx data = checkData(jsonObject);
        if (data == null) {
            log.info("数据校验未通过:{}", content);
            return null;
        } else {
            if (isData) {
                return dealwith(data, true);
            } else {
                //防止时间同步监控数据 重复处理
                if (TIME_TAG.equals(data.getTag())) {
                    return null;
                }
                return dealwithTime(data);
            }

        }
    }

    /**
     * 检查时间是否同步或者监控数据KAFKA阻塞
     *
     * @param data
     * @return
     */
    public WarningData dealwithTime(MonitorDataEx data) {
        if (!checkClockMonitorRate(data)) {
            return null;
        }
        long sysTime = System.currentTimeMillis();
        log.debug("dealwith time");
        MonitorItem monitorItem = monitorItemService.getByTag(TIME_TAG, data.getProjectId());
        if (MonitorItem.isNull(monitorItem)) {
            return null;
        }
        data.setResult(Math.abs(sysTime - data.getTime()) < syncTimeValue);
        data.setTag(TIME_TAG);
        data.setParams(null);
        data.setResponse(null);
        data.setTarget(null);
        return dealwith(data, false);
    }

    /**
     * 校验 数据是否满足采样条件
     *
     * @param data
     * @return
     */
    private synchronized boolean checkClockMonitorRate(MonitorDataEx data) {
        if (StringUtils.isEmpty(data.getHost())) {
            return false;
        }
        Long time = clockMonitorTimeMap.get(data.getHost());
        time = time == null ? Long.valueOf(0L) : time;
        Long now = System.currentTimeMillis();
        if (now - time > clockMonitorRate) {
            clockMonitorTimeMap.put(data.getHost(), now);
            return true;
        }
        return false;
    }

    /**
     * 检查数据是否合法
     *
     * @param jsonObject 监控数据
     * @return
     */
    private MonitorDataEx checkData(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        MonitorDataEx data = new MonitorDataEx();
        data.setProjectTag(jsonObject.getString("projectTag"));
        if (StringUtils.isEmpty(data.getProjectTag())) {
            return null;
        }
        data.setPassword(jsonObject.getString("password"));
        if (StringUtils.isEmpty(data.getPassword())) {
            return null;
        }
        Boolean result = jsonObject.getBoolean("result");
        if (result == null) {
            return null;
        }
        data.setResult(result);
        data.setTag(jsonObject.getString("tag"));
        if (StringUtils.isEmpty(data.getTag())) {
            return null;
        }
        Project project = projectService.getByTag(data.getProjectTag());
        if (project == null) {
            log.info("项目({})不存在", data.getTag());
            return null;
        }
        if (!data.getPassword().equalsIgnoreCase(PasswordUtils.encodePassword(project.getTag(), project.getPassword()))) {
            log.info("项目({})密码错误", data.getTag());
            return null;
        }
        data.setProjectId(project.getId());
        data.setTime(jsonObject.getLong("time"));
        data.setUseTimeMs(jsonObject.getLong("useTimeMs"));
        data.setTraceId(jsonObject.getString("traceId"));
        data.setApplicationName(jsonObject.getString("applicationName"));
        data.setHost(jsonObject.getString("host"));
        data.setTarget(jsonObject.getString("target"));
        data.setParams(jsonObject.getString("params"));
        data.setResponse(jsonObject.getString("response"));
        if (StringUtils.isEmpty(data.getHost())) {
            data.setHost("UNKNOWN");
        }
        return data;
    }

    /**
     * 分析监控数据
     *
     * @param data    监控数据
     * @param useLock 是否使用分布式锁
     * @return
     */
    private WarningData dealwith(MonitorDataEx data, boolean useLock) {
        log.debug("dealwith");
        MonitorItem monitorItem = monitorItemService.getByTag(data.getTag(), data.getProjectId());
        if (MonitorItem.isNull(monitorItem)) {
            log.warn("系统中不存在监控项:{}-{}", data.getProjectTag(), data.getTag());
            return null;
        }
        if (!MonitorItem.Status.ENABLED.equals(monitorItem.getStatus())) {
            return null;
        }
        String lockKey = String.format("dealwith_%s_%s_%s_%s", monitorItem.getId(), data.getApplicationName(), data.getHost(), data.getTarget());
        if (useLock) {
            lockUtils.lock(lockKey, LOCK_TIME);
        }

        if (monitorItem.getIgnoreHost() != null && monitorItem.getIgnoreHost()) {
            data.setHost(null);
        }
        if (monitorItem.getIgnoreApp() != null && monitorItem.getIgnoreApp()) {
            data.setApplicationName(null);
        }
        WarningData warningData = warningDataService.getCurrentData(monitorItem.getId(), data.getApplicationName(), data.getHost(), data.getTarget());
        boolean resultFlag = monitorItemService.isSuccess(monitorItem, data);
        boolean timeout = isTimeout(data, monitorItem);
        if (warningData == null) {
            if (!resultFlag || timeout) {
                warningData = createNewWarningData(monitorItem, data);
            }
        } else {
            updateWarningData(data, warningData, monitorItem, resultFlag);
        }
        //监控项统计
        summary(monitorItem, data, resultFlag, timeout);
        if (useLock) {
            lockUtils.releaseLock(lockKey);
        }
        if (warningData != null) {
            sendToEndpoint(WarningData.Status.CLEARED.equals(warningData.getStatus()), warningData, data, monitorItem);
        }
        return warningData;
    }

    /**
     * 统计监控项
     *
     * @param monitorItem
     */
    private void summary(MonitorItem monitorItem, MonitorDataEx dataEx, boolean resultFlag, boolean timeout) {
        if (monitorItem == null || dataEx == null) {
            return;
        }
        Date date = DateUtils.dayBegin(new Date(dataEx.getTime()));
        ItemSummaryRecord record = itemSummaryRecordService.find(monitorItem.getId(), date);
        if (record == null) {
            record = new ItemSummaryRecord();
            record.setDate(date);
            record.setItemId(monitorItem.getId());
        }
        if (resultFlag) {
            record.succeed(dataEx.getUseTimeMs());
        } else {
            record.failed(dataEx.getUseTimeMs());
        }
        if (timeout) {
            record.timeouted();
        }
        itemSummaryRecordService.save(record);
    }

    /**
     * 根据告警数据判断是否超时
     *
     * @param data        告警数据
     * @param monitorItem 告警项
     * @return
     */
    private boolean isTimeout(MonitorDataEx data, MonitorItem monitorItem) {
        if (data.getUseTimeMs() != null) {
            Long timeoutMs = data.getTimeoutMs() != null ? data.getTimeoutMs() : monitorItem.getTimeoutMs();
            if (timeoutMs != null && timeoutMs > 0) {
                return data.getUseTimeMs() > timeoutMs;
            }
        }
        return false;
    }

    /**
     * 新建告警数据
     *
     * @param monitorItem 告警项
     * @param data        告警数据
     * @return
     */
    private WarningData createNewWarningData(MonitorItem monitorItem, MonitorDataEx data) {
        WarningData warningData = new WarningData();
        warningData.setItemId(monitorItem.getId());
        Date now = new Date();
        warningData.setFirstTime(now);
        warningData.setLatestTime(now);
        warningData.setTimes(1L);
        warningData.setCycleTimes(1L);
        warningData.setApplicationName(data.getApplicationName());
        warningData.setHost(data.getHost());
        warningData.setTarget(data.getTarget());
        warningData.setProjectId(monitorItem.getProjectId());

        if (warningData.getTimes() >= monitorItem.getTimes()) {
            sendNotify(false, monitorItem, warningData);
        }

        addLog(data, warningData, monitorItem);
        log.debug("saveWarningData");
        warningData = warningDataService.save(warningData);
        return warningData;
    }

    /**
     * 更新告警数据
     *
     * @param data        告警数据
     * @param warningData 告警记录
     * @param monitorItem 告警项
     * @return
     */
    private WarningData updateWarningData(MonitorDataEx data, WarningData warningData, MonitorItem monitorItem, boolean resultFlag) {
        Date now = new Date();
        if (resultFlag && !isTimeout(data, monitorItem)) {
            warningData.setSuccessTimes((warningData.getSuccessTimes() == null ? 0 : warningData.getSuccessTimes()) + 1);
            if (warningData.getSuccessTimes() >= monitorItem.getRecoveryTimes()) {
                warningData.setStatus(WarningData.Status.CLEARED);
                warningData.setRecoveryTime(new Date());
                if (warningData.getLastSendTime() != null) {
                    sendNotify(true, monitorItem, warningData);
                }
            }
        } else {
            warningData.setLatestTime(now);
            warningData.setTimes(warningData.getTimes() + 1);
            warningData.setCycleTimes(warningData.getCycleTimes() + 1);
            warningData.setSuccessTimes(0L);
            if (warningData.getTimes() >= monitorItem.getTimes()) {
                sendNotify(false, monitorItem, warningData);
            }
            addLog(data, warningData, monitorItem);
        }
        warningData.setProjectId(monitorItem.getProjectId());
        log.debug("saveWarningData:{}", warningData.getTimes());
        return warningDataService.save(warningData);
    }

    /**
     * 发送通知
     *
     * @param recovery 是否恢复
     * @param item     告警项
     * @param data     告警记录
     */
    private void sendNotify(boolean recovery, MonitorItem item, WarningData data) {
        if (!item.getSendFlag() || item.getReceivers() == null || item.getReceivers().isEmpty()) {
            return;
        }
        if (!recovery && data.getLastSendTime() != null) {
            return;
        }
        log.debug("send {} notify", recovery);

        //发送邮件时忽略WarningData的Log
        WarningData sendData = new WarningData();
//        BeanUtils.copyPropertiesIgnorException(data, sendData);
        if (data != null) {
            data.copyTo(sendData);
        }
        sendData.setLogs(null);
        NotifyDataDto notifyDataDto = new NotifyDataDto(recovery, item, sendData);
        kafkaTemplate.send(KafkaTopic.NOTIFY_DATA, JSON.toJSONString(notifyDataDto));
        if (!recovery) {
            data.setCycleTimes(0L);
        }
        data.setLastSendTime(data.getLatestTime());
    }

    /**
     * 推送数据到终端
     *
     * @param recovery      是否恢复
     * @param warningData   告警记录
     * @param monitorDataEx 告警数据
     * @param item
     * @return
     */
    private WarningData sendToEndpoint(boolean recovery, WarningData warningData, MonitorDataEx monitorDataEx, MonitorItem item) {
        log.debug("send data to endpoint");
        template.convertAndSend("/topic/" + monitorDataEx.getProjectTag(), new NotifyDataDto(recovery, item, warningData));
        return warningData;
    }

    /**
     * 记录监控点失败时调用参数及返回结果,执行时间,超时时间
     *
     * @param data        告警数据
     * @param warningData 告警记录
     * @param monitorItem 告警项
     */
    private void addLog(MonitorDataEx data, WarningData warningData, MonitorItem monitorItem) {
        List<LogData> logs = warningData.getLogs();
        if (logs == null) {
            logs = new ArrayList<>();
        }
        if (!StringUtils.isEmpty(data.getParams()) || !StringUtils.isEmpty(data.getResponse()) || data.getUseTimeMs() != null) {
            logs.add(new LogData(data.getParams(), data.getResponse(), data.getUseTimeMs(), data.getTimeoutMs() == null ? monitorItem.getTimeoutMs() : data.getTimeoutMs(), data.getTraceId(), data.getTime()));
        }
        if (logs.size() > logSize) {
            logs.remove(0);
        }
        warningData.setLogs(logs);
    }
}
