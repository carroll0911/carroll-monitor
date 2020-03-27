package com.carroll.monitor.analyzer.service;


import com.carroll.monitor.analyzer.dto.NotifyDataDto;
import com.carroll.monitor.analyzer.model.MonitorItem;

/**
 * 告警通知接口
 *
 * @author: carroll
 * @date 2019/11/8
 *
 */
public interface INotifyService {

    MonitorItem.MessageType getMessageType();

    void notify(NotifyDataDto data);
}
