package com.carroll.monitor.analyzer.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 业务上下文数据
 * @author: carroll
 * @date 2019/9/9
 */
public class BizContext {

    public static final String MONITOR_USER_NAME = "MONITOR_USER_NAME";
    public static final String MONITOR_USER_CACHE = "MONITOR_USER_CACHE";

    private static final ThreadLocal<Map<String, Object>> bizContext = new ThreadLocal<Map<String, Object>>() {
        @Override
        protected Map<String, Object> initialValue() {
            return new HashMap<>(16);
        }
    };

    private BizContext() {
    }

    public static Map<String, Object> getAll() {
        return bizContext.get();
    }

    public static void addAll(Map<String, Object> data) {
        Map<String, Object> map = bizContext.get();
        if (map == null) {
            map = new HashMap<>();
        }
        if (data != null) {
            map.putAll(data);
        }
        bizContext.set(map);
    }


    public static void setData(String key, Object value) {
        Map<String, Object> map = bizContext.get();
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(key, value);
        bizContext.set(map);
    }

    public static Object getData(String key) {
        Map<String, Object> map = bizContext.get();
        if (map == null) {
            return null;
        }
        return map.get(key);
    }

    public static void clear() {
        Map<String, Object> map = bizContext.get();
        if (map != null) {
            map.clear();
        }
    }
}
