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
public class CurrentMonitorRequest extends BmPageRequest {
    @ApiModelProperty(value = "告警数据产生时间 起始时间")
    private Date firstStartTime;

    @ApiModelProperty(value = "告警数据产生时间 结束时间")
    private Date firstEndTime;

    @ApiModelProperty(value = "告警更新时间 起始时间")
    private Date updateStartTime;

    @ApiModelProperty(value = "告警更新时间 结束时间")
    private Date updateEndTime;

    @ApiModelProperty(value = "告警级别, MAJOR: 严重, CRITICAL: 紧急 , WARNING: 警告 , EVENT: 事件")
    @ValueIn(allowValues = {"MAJOR", "CRITICAL", "WARNING", "EVENT"})
    private String level;

    @ApiModelProperty(value = "告警源")
    private String applicationName;

    @ApiModelProperty(value = "告警内容id")
    private String itemId;

    @ApiModelProperty(value = "告警对象")
    private String target;

    @ApiModelProperty(value = "所属项目id")
    private String projectId;

    public Date getFirstStartTime() {
        if (firstStartTime != null) {
            return (Date) firstStartTime.clone();
        } else {
            return null;
        }
    }

    public void setFirstStartTime(Date firstStartTime) {
        if (firstStartTime != null) {
            this.firstStartTime = (Date) firstStartTime.clone();
        } else {
            this.firstStartTime = null;
        }
    }

    public Date getFirstEndTime() {
        if (firstEndTime != null) {
            return (Date) firstEndTime.clone();
        } else {
            return null;
        }
    }

    public void setFirstEndTime(Date firstEndTime) {
        if (firstEndTime != null) {
            this.firstEndTime = (Date) firstEndTime.clone();
        } else {
            this.firstEndTime = null;
        }
    }

    public Date getUpdateStartTime() {
        if (updateStartTime != null) {
            return (Date) updateStartTime.clone();
        } else {
            return null;
        }
    }

    public void setUpdateStartTime(Date updateStartTime) {
        if (updateStartTime != null) {
            this.updateStartTime = (Date) updateStartTime.clone();
        } else {
            this.updateStartTime = null;
        }
    }

    public Date getUpdateEndTime() {
        if (updateEndTime != null) {
            return (Date) updateEndTime.clone();
        } else {
            return null;
        }
    }

    public void setUpdateEndTime(Date updateEndTime) {
        if (updateEndTime != null) {
            this.updateEndTime = (Date) updateEndTime.clone();
        } else {
            this.updateEndTime = null;
        }
    }

}
