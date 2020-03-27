package com.carroll.monitor.analyzer.utils;/**
 * Created by core_ on 2018/7/2.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * 调用第三方接口监听接口
 *
 * @author: carroll
 * @date 2018/7/2
 */

public class HttpClientListenerRegister {

    private static List<HttpClientListener> listeners = new ArrayList<>();

    /**
     * 注册 处理监听器
     *
     * @param listener
     */
    @SuppressWarnings("unused")
    public static void regist(HttpClientListener listener) {
        if (listener == null) {
            return;
        }
        for (HttpClientListener l : listeners) {
            if (l.getClass().equals(listener.getClass())) {
                return;
            }
        }
        listeners.add(listener);
    }

    /**
     * 获取已注册的监听器
     *
     * @return
     */
    public static List<HttpClientListener> listeners() {
        return listeners;
    }
}
