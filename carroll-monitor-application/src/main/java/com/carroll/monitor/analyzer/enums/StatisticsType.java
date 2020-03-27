package com.carroll.monitor.analyzer.enums;

/**
 * @author: carroll
 * @date 2019/9/9
 */
public enum StatisticsType {
    LEVEL("告警级别"),
    NAME("告警名称"),
    SOURCE("告警源");

    private String desc;

    StatisticsType(String desc) {
        this.desc = desc;
    }
}
