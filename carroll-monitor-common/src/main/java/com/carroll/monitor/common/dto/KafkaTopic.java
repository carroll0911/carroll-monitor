package com.carroll.monitor.common.dto;

/**
 * @author: carroll
 * @date 2019/9/9
 */
public class KafkaTopic {

    private KafkaTopic(){}

    /**
     * 监控数据Topic
     */
    public static final String MONITOR_DATA="MONITOR_DATA";
    /**
     * 通知发送数据Topic
     */
    public static final String NOTIFY_DATA="NOTIFY_DATA";

    /**
     * 时间监控 Group
     */
    public static final String MONITOR_TIME_GROUP="MONITOR_TIME_GROUP";

    /**
     * 监控数据分析 Group
     */
    public static final String MONITOR_DATA_GROUP="MONITOR_DATA_GROUP";

}
