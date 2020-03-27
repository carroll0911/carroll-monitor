package com.carroll.monitor.analyzer.job;

import com.alibaba.fastjson.JSONObject;
import com.carroll.monitor.analyzer.model.BaseServiceMonitorItem;
import com.carroll.monitor.analyzer.model.Project;
import com.carroll.monitor.analyzer.service.IBSMonitorItemService;
import com.carroll.monitor.analyzer.service.IProjectService;
import com.carroll.monitor.analyzer.utils.OkHttpUtil;
import com.carroll.monitor.data.collector.component.DataSender;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author: carroll
 * @date 2019/9/9
 */
@Slf4j
@JobHandler(value = "kafkaMonitorJob")
@Component
public class KafkaMonitorJob extends IJobHandler {
    @Autowired
    private IBSMonitorItemService bsMonitorItemService;
    @Autowired
    private IProjectService projectService;
    @Autowired
    private DataSender dataSender;

    private static final String TAG = "KAFKA";

    /**
     * 获取 消费者列表
     *
     * @param item
     * @return
     * @throws IOException
     */
    private List<String> getConsumers(BaseServiceMonitorItem item) throws IOException {
        String rs = OkHttpUtil.doGet(item.getUri() + "/consumer");
        log.info("get kafka consumers:{}", rs);
        JSONObject ja = null;
        try {
            ja = JSONObject.parseObject(rs);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
        if (ja.containsKey("consumers")) {
            return ja.getObject("consumers", List.class);
        } else {
            return null;
        }

    }

    private void checkStatus(BaseServiceMonitorItem item, String consumer, Map<String, Object> resultMap) throws IOException {
        String rs = OkHttpUtil.doGet(String.format("%s/%s/%s/lag", item.getUri(), "consumer", consumer));
        JSONObject ja = null;
        JSONObject rso = null;
        String statusKey = "status";
        String maxLagKey = "maxlag";
        String totallagKey = "current_lag";
        String topicKey = "topic";
        log.debug("consumer result:{}", rs);
        try {
            rso = JSONObject.parseObject(rs);
            ja = rso;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return;
        }
        if (!ja.containsKey(statusKey)) {
            return;
        }
        ja = ja.getJSONObject(statusKey);
        if (!ja.containsKey(maxLagKey)) {
            return;
        }
        ja = ja.getJSONObject(maxLagKey);
        if (ja.containsKey(totallagKey)) {
            Integer totallag = ja.getInteger(totallagKey);
            if (totallag != null && totallag > item.getWarnLine()) {
                resultMap.put(String.format("%s[%s]", consumer, ja.getString(topicKey)), rso);
            }
        }
    }

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        XxlJobLogger.log("Kafka 状态监控任务.");
        List<Project> projects = projectService.findAll();
        List<BaseServiceMonitorItem> bsmis = null;
        for (Project p : projects) {
            bsmis = bsMonitorItemService.findAllByTagAndProjectId(TAG, p.getId());
            Map<String, Object> resultMap = new HashMap<>();
            List<String> consumers = null;
            for (BaseServiceMonitorItem bsmi : bsmis) {
                try {
                    consumers = getConsumers(bsmi);
                } catch (IOException e) {
                    log.warn(e.getMessage(), e);
                    continue;
                }
                if (consumers != null && !consumers.isEmpty()) {
                    consumers.forEach(consumer -> {
                        try {
                            checkStatus(bsmi, consumer, resultMap);
                        } catch (IOException e) {
                            log.warn(e.getMessage(), e);
                        }
                    });
                }
            }
            if (!resultMap.isEmpty()) {
                dataSender.send(TAG, false, null, resultMap);
            } else {
                dataSender.send(TAG, true, null, resultMap);
            }
        }
        return SUCCESS;
    }
}
