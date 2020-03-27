package com.carroll.monitor.common.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 监控数据模型
 * @author: carroll
 * @date 2019/9/9
 */
@Getter
@Setter
public class MonitorData {

    private String tag;
    private boolean result;
    private String projectTag;
    private String password;
    private Long time;
    private String applicationName;
    private String host = "UNKNOWN";
    /**
     * 监控点调用参数
     */
    private String params;
    /**
     * 监控点调用结果
     */
    private String response;
    /**
     * 告警对象
     */
    private String target;
    /**
     * 方法执行所用时间
     */
    private Long useTimeMs;
    /**
     * 监控超时时间
     */
    private Long timeoutMs;

    /**
     * 调用链ID
     */
    private String traceId;
}
