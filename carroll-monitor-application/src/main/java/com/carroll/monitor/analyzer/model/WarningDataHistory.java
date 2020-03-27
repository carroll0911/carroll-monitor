package com.carroll.monitor.analyzer.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @author: carroll
 * @date 2019/10/23
 */
@Getter
@Setter
public class WarningDataHistory extends BaseModel {
    //监控项ID
    private String itemId;
    //应用名称（告警源）
    private String applicationName;
    //第一次发生时间
    private Date firstTime;
    //最新时间
    private Date latestTime;
    //累计次数
    private Long times = 0L;
    //周期累计次数
    private Long cycleTimes = 0L;
    //上次发送时间
    private Date lastSendTime;
    //恢复时间
    private Date recoveryTime;
    //连续成功次数
    private Long successTimes = 0L;
    //状态
    private WarningData.Status status = WarningData.Status.NORMAL;
    //告警源IP
    private String host;
    // 告警对象
    private String target;
    /**
     * 项目ID
     */
    private String projectId;

    // 监控点调用日志数据
    private List<LogData> logs;
}
