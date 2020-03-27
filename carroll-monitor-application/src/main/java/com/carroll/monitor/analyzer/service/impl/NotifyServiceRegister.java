package com.carroll.monitor.analyzer.service.impl;


import com.carroll.monitor.analyzer.model.MonitorItem;
import com.carroll.monitor.analyzer.service.INotifyService;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 通知服务注册中心
 *
 * @author: carroll
 * @date 2019/11/8
 *
 */
public class NotifyServiceRegister {
    private static Map<MonitorItem.MessageType, INotifyService> listeners = new HashMap<>();

    /**
     * 注册 处理监听器
     *
     * @param listener
     */
    @SuppressWarnings("unused")
    public static void regist(INotifyService listener) {
        if (listener == null || listener.getMessageType() == null) {
            return;
        }
        if (listeners.get(listener.getMessageType()) != null
                && !listeners.get(listener.getMessageType()).getClass().isAssignableFrom(listener.getClass())) {
            throw new RuntimeException("接口tag冲突:" + listener.getMessageType().name());
        }
        listeners.put(listener.getMessageType(), listener);
    }

    /**
     * 获取已注册的监听器
     *
     * @return
     */
    public static INotifyService listener(MonitorItem.MessageType type) {
        return listeners.get(type);
    }

    public static Set<MonitorItem.MessageType> messageTypes() {
        return listeners.keySet();
    }
}
