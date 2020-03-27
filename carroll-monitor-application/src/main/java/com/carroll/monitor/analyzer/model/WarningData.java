package com.carroll.monitor.analyzer.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 告警数据
 * @author: carroll
 * @date 2019/10/16
 */
@Getter
@Setter
public class WarningData extends BaseModel {

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
    private Status status = Status.NORMAL;
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

    public enum Status {
        ALL("ALL", "全部"),
        CLEARED("CLEARED", "已清除"),
        NORMAL("NORMAL", "未清除");
        @Getter
        private String code;
        @Getter
        private String desc;

        Status(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    public void copyTo(WarningData data){
        data.setProjectId(projectId);
        data.setItemId(itemId);
        data.setApplicationName(applicationName);
        data.setFirstTime(firstTime);
        data.setLatestTime(latestTime);
        data.setTimes(times);
        data.setCycleTimes(cycleTimes);
        data.setLastSendTime(lastSendTime);
        data.setRecoveryTime(recoveryTime);
        data.setSuccessTimes(successTimes);
        data.setStatus(status);
        data.setHost(host);
        data.setTarget(target);
        data.setLogs(logs);
    }
}
