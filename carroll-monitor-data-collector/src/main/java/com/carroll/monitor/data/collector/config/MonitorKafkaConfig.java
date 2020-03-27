package com.carroll.monitor.data.collector.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author: carroll
 * @date 2019/9/9
 **/
@Component
@ConfigurationProperties(prefix = "monitor.kafka")
@Data
public class MonitorKafkaConfig {
    private String brokers;
    private String group;
    private int bachSize;
    private int lingerMs;
    private int bufferMemory;
    private String autoCommitIntervalMs;
    private String sessionTimeoutMs;
    private String heartbeatIntervalMs;
    private String autoOffsetReset;
    private int maxPollRecords;
    private int concurrencey;
    private int pollTimeout;
    private int maxBlockMs = 3000;
}
