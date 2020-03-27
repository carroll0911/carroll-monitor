package com.carroll.monitor.analyzer.dto;

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
public class CurrentMonitorDto extends BaseDto {
    @ApiModelProperty(value = "ip")
    private String host;
    @ApiModelProperty(value = "告警名称")
    private String name;

    @ApiModelProperty(value = "告警级别")
    private String level;

    @ApiModelProperty(value = "告警源")
    private String applicationName;

    @ApiModelProperty(value = "产生时间")
    private Date firstTime;

    @ApiModelProperty(value = "更新时间")
    private Date latestTime;

    @ApiModelProperty(value = "出现次数")
    private Long times;

    @ApiModelProperty(value = "连续成功次数")
    private Long successTimes;

    @ApiModelProperty(value = "修改数据时间")
    private Date updateTime;

    @ApiModelProperty(value = "告警对象")
    private String target;
    @ApiModelProperty(value = "项目名称")
    private String projectName;

    public Date getFirstTime() {
        if(firstTime != null){
            return (Date) firstTime.clone();
        }else {
            return null;
        }
    }

    public void setFirstTime(Date firstTime) {
        if(firstTime != null){
            this.firstTime = (Date) firstTime.clone();
        }else {
            this.firstTime = null;
        }
    }

    public Date getLatestTime() {
        if(latestTime != null){
            return (Date) latestTime.clone();
        }else {
            return null;
        }
    }

    public void setLatestTime(Date latestTime) {
        if(latestTime != null){
            this.latestTime = (Date) latestTime.clone();
        }else {
            this.latestTime = null;
        }
    }

    public Date getUpdateTime() {
        if(updateTime != null){
            return (Date) updateTime.clone();
        }else {
            return null;
        }
    }

    public void setUpdateTime(Date updateTime) {
        if(updateTime != null){
            this.updateTime = (Date) updateTime.clone();
        }else {
            this.updateTime = null;
        }
    }
}
