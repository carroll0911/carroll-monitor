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
public class WarningDataPageDto extends BaseDto {

    @ApiModelProperty(value = "告警名称")
    private String name;

    @ApiModelProperty(value = "告警级别")
    private String level;

    @ApiModelProperty(value = "告警源")
    private String applicationName;

    @ApiModelProperty(value = "产生时间")
    private Date firstTime;

    @ApiModelProperty(value = "恢复时间")
    private Date recoveryTime;

    @ApiModelProperty(value = "出现次数")
    private Long times;

    @ApiModelProperty(value = "ip")
    private String host;

    @ApiModelProperty(value = "告警对象")
    private String target;
    @ApiModelProperty(value = "应用名称")
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
