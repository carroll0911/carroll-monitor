package com.carroll.monitor.analyzer.utils;

import com.carroll.cache.RedisUtil;
import com.carroll.monitor.analyzer.dto.ProjectDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Component
@Slf4j
public class ShareUtils {

    @Autowired
    private RedisUtil redisUtil;

    private static final String PROJECT_KEY_PREFIX = "PROJECT_INFO";
    private static final String LOCK_KEY_PREFIX = "LOCK";

    private static final String KEY_SEPERATOR = "#";


    private static final int LOCK_TIMES = 200;
    private static final ThreadLocal<Integer> lockTimes = new ThreadLocal<>();

    public ProjectDto loadProjectByTag(String tag) {
        ProjectDto dto = (ProjectDto) redisUtil.get(PROJECT_KEY_PREFIX + KEY_SEPERATOR + tag);
        return dto;
    }

    public ProjectDto loadProjectById(String id) {
        ProjectDto dto = (ProjectDto) redisUtil.get(PROJECT_KEY_PREFIX + KEY_SEPERATOR + id);
        return dto;
    }

    public void shareProject(ProjectDto projectDto) {
        if (null != projectDto) {
            redisUtil.set(PROJECT_KEY_PREFIX + KEY_SEPERATOR + projectDto.getTag(), projectDto);
            redisUtil.set(PROJECT_KEY_PREFIX + KEY_SEPERATOR + projectDto.getId(), projectDto);
        }
    }

    public synchronized boolean getLock(String key, long expire) {
        String k = String.format("%s%s%s", LOCK_KEY_PREFIX, KEY_SEPERATOR, key);
        Integer count = 0;
        Integer times = lockTimes.get();
        times = times == null ? 1 : times + 1;
        try {
            count = (Integer) redisUtil.get(k);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        if (null == count || count < 1) {
            redisUtil.set(k, 1, expire);
            return true;
        }
        lockTimes.set(times);
        if (times > LOCK_TIMES) {
            releaseLock(key);
        }
        return false;
    }

    public synchronized void releaseLock(String key) {
        try {
            redisUtil.remove(String.format("%s%s%s", LOCK_KEY_PREFIX, KEY_SEPERATOR, key));
            lockTimes.set(null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }
}
