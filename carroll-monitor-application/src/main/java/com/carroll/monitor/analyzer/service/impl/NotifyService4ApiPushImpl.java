package com.carroll.monitor.analyzer.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.carroll.monitor.analyzer.dto.NotifyDataDto;
import com.carroll.monitor.analyzer.model.*;
import com.carroll.monitor.analyzer.service.INotifyService;
import com.carroll.monitor.analyzer.service.IProjectService;
import com.carroll.monitor.analyzer.service.IWarningDataCallBackService;
import com.carroll.monitor.analyzer.utils.HttpUtils;
import com.carroll.spring.rest.starter.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 告警通知--接口推送实现
 *
 * @author: carroll
 * @date 2019/11/8
 *
 */
@Service
@Slf4j
public class NotifyService4ApiPushImpl extends NotifyServiceAbstract implements INotifyService {

    @Autowired
    private IWarningDataCallBackService warningDataCallBackService;

    @Autowired
    private IProjectService projectService;
    @Override
    public MonitorItem.MessageType getMessageType() {
        return MonitorItem.MessageType.API_PUSH;
    }

    @Override
    public void notify(NotifyDataDto data) {
        boolean success = false;
        try {
            MonitorItem item = null;
            if (data != null && data.getMonitorItem() != null) {
                item = data.getMonitorItem();
            }
            if (item == null) {
                log.warn("monitor item not found");
                return;
            }
            Project project = projectService.getByID(item.getProjectId());
            if (project == null) {
                log.debug("project not found");
                return;
            }
            WarningDataCallBack wdcb = warningDataCallBackService.findByProjectTagAndEnable(project.getTag(), true);
            if (wdcb == null || wdcb.getCallback() == null) {
                log.debug("callback not found");
                return;
            }
            String res = HttpUtils.doPost(wdcb.getCallback(), JSONObject.toJSONString(data), HttpUtils.JSON, 3000, 5000);
            if (res != null) {
                BaseResponse response = JSONObject.parseObject(res, BaseResponse.class);
                if (!response.getReturnSuccess()) {
                    log.error("监控数据回调失败：{}", wdcb.getCallback());
                } else {
                    log.debug("监控数据回调响应：{}", res);
                    success = true;
                }
            } else {
                log.error("监控数据回调失败：result is null");
            }
        } catch (Exception e) {
            log.error("监控数据回调失败：{}", e.getMessage());
        }
        NotifyRecord record = new NotifyRecord();
        record.setNotifyType(WarningData.Status.CLEARED.equals(data.getWarningData().getStatus()) ? NotifyRecord.NotifyType.RECOVERY : NotifyRecord.NotifyType.WARN);
        saveRecord(data, success, record, MonitorItem.MessageType.SMS);
    }

}
