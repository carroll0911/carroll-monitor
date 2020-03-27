package com.carroll.monitor.analyzer.request;

import com.carroll.monitor.analyzer.dto.BaseDto;
import com.carroll.spring.rest.starter.BaseRequest;
import com.carroll.spring.rest.starter.validator.ValueIn;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Getter
@Setter
public class MonitorItemRequest extends BaseRequest {

    @ApiModelProperty(value = "名称", required = true, position = 1)
    private String name;

    @ApiModelProperty(value = "tag", required = true, position = 2)
    private String tag;

    @ApiModelProperty(value = "级别 BUSINESS: 业务, SYSTEM: 系统", required = true, position = 3)
    @ValueIn(allowValues = {"BUSINESS", "SYSTEM"})
    private String category;

    @ApiModelProperty(value = "告警级别, MAJOR: 严重, CRITICAL: 紧急 , WARNING: 警告 , EVENT: 事件",required = true,position = 4)
    @ValueIn(allowValues = {"MAJOR", "CRITICAL", "WARNING","EVENT"})
    private String level;

    @ApiModelProperty(value = "所属项目ID", required = true, position = 5)
    private String projectId;

    @ApiModelProperty(value = "描述", position = 6)
    private String description;

    @ApiModelProperty(value = "告警阈值", required = true, position = 7)
    @Min(1)
    private Long times;

    @ApiModelProperty(value = "重复告警周期(分)", required = true, position = 8)
    @Min(5)
    @Max(120)
    private Long cycle;

    @ApiModelProperty(value = "重复告警触发次数", required = true, position = 9)
    @Min(1)
    private Long cycleTimes;

    @ApiModelProperty(value = "告警接收人", required = true, position = 10)
    private List<BaseDto> receivers;

    @ApiModelProperty(value = "是否发送告警", required = true, position = 11)
    private Boolean sendFlag;

    @ApiModelProperty(value = "告警通知类型，SMS: 短信， EMAIL: 邮件", required = true, position = 12)
    @ValueIn(allowValues = {"SMS", "EMAIL"})
    private List<String> msgTypes;

    @ApiModelProperty(value = "告警恢复阈值", required = true, position = 13)
    @Min(1)
    private Long recoveryTimes;

    @ApiModelProperty(value = "处理意见", position = 14)
    private String suggest;

    @ApiModelProperty(value = "判断执行结果的脚本", position = 14)
    private String resultScript;
}
