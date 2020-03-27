package com.carroll.monitor.analyzer.request;

import com.carroll.spring.rest.starter.validator.ValueIn;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Getter
@Setter
public class HistoryMonitorPageRequest extends BmPageRequest {

    @ApiModelProperty(value = "告警数据产生时间 起始时间")
    private Date firstStartTime;

    @ApiModelProperty(value = "告警数据产生时间 结束时间")
    private Date firstEndTime;

    @ApiModelProperty(value = "告警恢复时间 起始时间")
    private Date recoveryStartTime;

    @ApiModelProperty(value = "告警恢复时间 结束时间")
    private Date recoveryEndTime;

    @ApiModelProperty(value = "告警级别, MAJOR: 严重, CRITICAL: 紧急 , WARNING: 警告 , EVENT: 事件")
    @ValueIn(allowValues = {"MAJOR", "CRITICAL", "WARNING","EVENT"})
    private String level;

    @ApiModelProperty(value = "告警源")
    private String applicationName;

    @ApiModelProperty(value = "告警内容id")
    private String itemId;

    @ApiModelProperty(value = "告警对象")
    private String target;

    @ApiModelProperty(value = "应用id")
    private String projectId;

    public Date getFirstStartTime() {
        if(firstStartTime != null){
            return (Date) firstStartTime.clone();
        }else {
            return null;
        }
    }

    public void setFirstStartTime(Date firstStartTime) {
        if(firstStartTime != null){
            this.firstStartTime = (Date) firstStartTime.clone();
        }else {
            this.firstStartTime = null;
        }
    }

    public Date getFirstEndTime() {
        if(firstEndTime != null){
            return (Date) firstEndTime.clone();
        }else {
            return null;
        }
    }

    public void setFirstEndTime(Date firstEndTime) {
        if(firstEndTime != null){
            this.firstEndTime = (Date) firstEndTime.clone();
        }else {
            this.firstEndTime = null;
        }
    }

    public Date getRecoveryStartTime() {
        if(recoveryStartTime != null){
            return (Date) recoveryStartTime.clone();
        }else {
            return null;
        }
    }

    public void setRecoveryStartTime(Date recoveryStartTime) {
        if(recoveryStartTime != null){
            this.recoveryStartTime = (Date) recoveryStartTime.clone();
        }else {
            this.recoveryStartTime = null;
        }
    }

    public Date getRecoveryEndTime() {
        if(recoveryEndTime != null){
            return (Date) recoveryEndTime.clone();
        }else {
            return null;
        }
    }

    public void setRecoveryEndTime(Date recoveryEndTime) {
        if(recoveryEndTime != null){
            this.recoveryEndTime = (Date) recoveryEndTime.clone();
        }else {
            this.recoveryEndTime = null;
        }
    }
}
