package com.carroll.monitor.analyzer.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Getter
@Setter
public class WarningDataDetailDto extends BaseDto{

    @ApiModelProperty(value = "告警名称")
    private String name;

    @ApiModelProperty(value = "告警源")
    private String applicationName;

    @ApiModelProperty(value = "告警级别")
    private String level;

    @ApiModelProperty(value = "告警类型")
    private String category;

    @ApiModelProperty(value = "产生时间")
    private Date firstTime;

    @ApiModelProperty(value = "更新时间")
    private Date latestTime;

    @ApiModelProperty(value = "恢复时间")
    private Date recoveryTime;

    @ApiModelProperty(value = "告警描述")
    private String description;

    @ApiModelProperty(value = "处理建议")
    private String suggest;

    @ApiModelProperty(value = "出现次数")
    private Long times;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "告警源IP")
    private String host;

    @ApiModelProperty(value = "连续成功次数")
    private Long successTimes;

    @ApiModelProperty(value = "监控点调用日志数据")
    private List<LogDataDto> logs;

    @ApiModelProperty(value = "告警对象")
    private String target;

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

    public Date getRecoveryTime() {
        if(recoveryTime != null){
            return (Date) recoveryTime.clone();
        }else {
            return null;
        }
    }

    public void setRecoveryTime(Date recoveryTime) {
        if(recoveryTime != null){
            this.recoveryTime = (Date) recoveryTime.clone();
        }else {
            this.recoveryTime = null;
        }
    }
}
