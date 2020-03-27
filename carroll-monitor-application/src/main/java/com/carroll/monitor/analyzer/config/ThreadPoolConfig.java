package com.carroll.monitor.analyzer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 线程池配置
 * @author: carroll
 * @date 2019/9/9
 */
@Component
@ConfigurationProperties(prefix = "theadpool")
public class ThreadPoolConfig {

    private int corePoolSize = 20;

    private int maxPoolSize = 50;

    private int blockQueueSize = 500;

    private long keepAliveTimeMs = 1000L;

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getBlockQueueSize() {
        return blockQueueSize;
    }

    public void setBlockQueueSize(int blockQueueSize) {
        this.blockQueueSize = blockQueueSize;
    }

    public long getKeepAliveTimeMs() {
        return keepAliveTimeMs;
    }

    public void setKeepAliveTimeMs(long keepAliveTimeMs) {
        this.keepAliveTimeMs = keepAliveTimeMs;
    }
}
