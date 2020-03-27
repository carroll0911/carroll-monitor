package com.carroll.monitor.data.collector.component;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 发送标识持有者
 * @author: carroll
 * @date 2019/9/9
 */
@Slf4j
public class SendFlagHolder {

    private static final ThreadLocal<Map<String, Boolean>> holder = new ThreadLocal<Map<String, Boolean>>() {
        @Override
        protected Map<String, Boolean> initialValue() {
            return new HashMap<>(16);
        }
    };
    private static final ThreadLocal<Map<String, Integer>> counter = new ThreadLocal<Map<String, Integer>>() {
        @Override
        protected Map<String, Integer> initialValue() {
            return new HashMap<>(16);
        }
    };

    private static final ThreadLocal<Map<String, Long>> starttime = new ThreadLocal<Map<String, Long>>() {
        @Override
        protected Map<String, Long> initialValue() {
            return new HashMap<>(16);
        }
    };

    private SendFlagHolder() {
    }

    public synchronized static void setSended(String tag) {
        Map<String, Boolean> flagMap = holder.get();
        if (flagMap == null) {
            flagMap = new HashMap<>();
        }
        flagMap.put(tag, true);
        holder.set(flagMap);
        log.debug("setSended:{}", tag);
    }

    public synchronized static void reduceSended(String tag) {
        Map<String, Boolean> flagMap = holder.get();
        if (flagMap == null) {
            return;
        }
        flagMap.put(tag, false);
        holder.set(flagMap);
        log.debug("reduceSended:{}", tag);
    }

    public synchronized static boolean sended(String tag) {
        Map<String, Boolean> flagMap = holder.get();
        if (flagMap == null) {
            return false;
        }
        Boolean flag = flagMap.get(tag);
        return flag != null && flag;
    }

    public synchronized static void addCount(String tag) {
        Map<String, Integer> countMap = counter.get();
        if (countMap == null) {
            countMap = new HashMap<>();
        }
        int count = countMap.get(tag) == null ? 0 : countMap.get(tag);
        countMap.put(tag, count + 1);
        counter.set(countMap);
    }

    public synchronized static void reduceCount(String tag) {
        log.debug("before reduceCount:{}-{}", tag, count(tag));
        Map<String, Integer> countMap = counter.get();
        if (countMap == null) {
            return;
        }
        int count = countMap.get(tag) == null ? 0 : countMap.get(tag);
        countMap.put(tag, count < 1 ? 0 : count - 1);
        counter.set(countMap);
        if (count < 2) {
            reduceSended(tag);
        }
        log.debug("after reduceCount:{}-{}", tag, countMap.get(tag));
    }

    public synchronized static int count(String tag) {
        Map<String, Integer> countMap = counter.get();
        if (countMap == null) {
            return 0;
        }
        Integer count = countMap.get(tag);
        return count == null ? 0 : count;
    }

    public synchronized static void setStartTime(String joinpoint, long time) {
        Map<String, Long> timeMap = starttime.get();
        timeMap.put(joinpoint, time);
    }

    public synchronized static Long getUsedTime(String joinpoint) {
        Long time = starttime.get().get(joinpoint);
        if (time == null) {
            return null;
        }
        return System.currentTimeMillis() - time;
    }
}
