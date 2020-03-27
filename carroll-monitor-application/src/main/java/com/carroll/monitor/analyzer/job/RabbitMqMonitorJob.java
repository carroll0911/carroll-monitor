package com.carroll.monitor.analyzer.job;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.carroll.monitor.analyzer.model.BaseServiceMonitorItem;
import com.carroll.monitor.analyzer.model.Project;
import com.carroll.monitor.analyzer.service.IBSMonitorItemService;
import com.carroll.monitor.analyzer.service.IProjectService;
import com.carroll.monitor.analyzer.utils.HttpUtils;
import com.carroll.monitor.data.collector.component.DataSender;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RabbitMQ监控时任务
 * @author: carroll
 * @date 2019/9/9
 */
@Slf4j
@Component
@JobHandler(value="rabbitMqMonitorJob")
public class RabbitMqMonitorJob extends IJobHandler {
    @Autowired
    private IBSMonitorItemService bsMonitorItemService;
    @Autowired
    private IProjectService projectService;
    @Autowired
    private DataSender dataSender;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        List<Project> projects = projectService.findAll();
        List<BaseServiceMonitorItem> bsmis = null;
        Map<String, Integer> rs = null;
        for (Project p : projects) {
            bsmis = bsMonitorItemService.findAllByTagAndProjectId("RABBITMQ", p.getId());
            for (BaseServiceMonitorItem bsmi : bsmis) {
                rs = mqSumInfo(bsmi);
                if (rs != null) {
                    rs.forEach((que, count) -> {
                        if (count != null && bsmi.getWarnLine() != null && count > bsmi.getWarnLine()) {
                            dataSender.send(bsmi.getTag(), false, null, count, que);
                        } else {
                            dataSender.send(bsmi.getTag(), true, null, count, que);
                        }
                    });
                }
            }
        }
        return SUCCESS;
    }

     private Map<String, Integer> mqSumInfo(BaseServiceMonitorItem item) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", item.getAuthorization());
        String rs = HttpUtils.doGet(item.getUri(), "", HttpUtils.JSON, headers);
        log.info("rabbitMq queues state:{}", rs);
        JSONArray ja = null;
        try {
            ja = JSONObject.parseArray(rs);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        Map<String, Integer> queues = new HashMap<>();
        JSONObject jo = null;
        Integer msgcount = 0;
        if (ja != null && ja.size() > 0) {
            for (int i = 0; i < ja.size(); i++) {
                jo = ja.getJSONObject(i);
                if (jo != null) {
                    msgcount = jo.getInteger("messages");
                    if (msgcount != null && msgcount > 0) {
                        queues.put(String.format("%s:%s", jo.getString("name"), jo.getString("node")), msgcount);
                    }
                }
            }
        }

        return queues;
    }


}
