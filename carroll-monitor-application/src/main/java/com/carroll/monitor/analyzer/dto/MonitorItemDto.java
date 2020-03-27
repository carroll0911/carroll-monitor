package com.carroll.monitor.analyzer.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Getter
@Setter
public class MonitorItemDto extends BaseDto{
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "tag")
    private String tag;
    @ApiModelProperty(value = "类别")
    private String category;
    @ApiModelProperty(value = "类别code")
    private String categoryCode;
    @ApiModelProperty(value = "级别")
    private String level;
    @ApiModelProperty(value = "级别code")
    private String levelCode;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "处理意见")
    private String suggest;
    @ApiModelProperty(value = "所属项目ID")
    private String projectId;
    @ApiModelProperty(value = "所属项目名称")
    private String projectName;
    @ApiModelProperty(value = "告警阈值")
    private long times;
    @ApiModelProperty(value = "重复告警周期(分)")
    private long cycle;
    @ApiModelProperty(value = "重复告警触发次数")
    private long cycleTimes;
    @ApiModelProperty(value = "告警接收人")
    private List<OperatorDto> receivers;
    @ApiModelProperty(value = "是否发送告警")
    private boolean sendFlag;
    @ApiModelProperty(value = "告警信息类型")
    private List<String> msgTypes;
    @ApiModelProperty(value = "告警恢复阈值")
    private long recoveryTimes;
    @ApiModelProperty(value = "忽略 host")
    private Boolean ignoreHost;
    @ApiModelProperty(value = "忽略 告警源")
    private Boolean ignoreApp;
    @ApiModelProperty(value = "判断执行结果的脚本")
    private String resultScript;
    @ApiModelProperty(value = "状态")
    private String status;
}
