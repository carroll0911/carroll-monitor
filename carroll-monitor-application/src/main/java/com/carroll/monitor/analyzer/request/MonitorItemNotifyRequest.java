package com.carroll.monitor.analyzer.request;

import com.carroll.monitor.analyzer.dto.BaseDto;
import com.carroll.spring.rest.starter.validator.ValueIn;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Getter
@Setter
public class MonitorItemNotifyRequest extends BmBaseRequest {

    @ApiModelProperty(value = "告警内容id", required = true, position = 0)
    @NotBlank
    private String id;

    @ApiModelProperty(value = "是否发送通知", required = true, position = 1)
    private Boolean sendFlag;

    @ApiModelProperty(value = "告警通知类型，SMS: 短信， EMAIL: 邮件", required = true, position = 2)
    @ValueIn(allowValues = {"SMS", "EMAIL"})
    private List<String> msgTypes;

    @ApiModelProperty(value = "告警人员", required = true, position = 3)
    @NotNull
    private List<BaseDto> receivers;

    @ApiModelProperty(value = "告警通知阀值", required = true, position = 4)
    @Min(1)
    private Long times;

    @ApiModelProperty(value = "告警恢复阀值", required = true, position = 5)
    @Min(1)
    private Long recoveryTimes;

    @ApiModelProperty(value = "重复告警周期（分）", position = 6)
    @Min(5)
    @Max(120)
    private Long cycle;

    @ApiModelProperty(value = "重复告警触发次数", position = 7)
    @Min(1)
    private Long cycleTimes;

    @ApiModelProperty(value = "忽略 host", required = true, position = 8)
    private Boolean ignoreHost;

    @ApiModelProperty(value = "忽略 告警源", required = true, position = 9)
    private Boolean ignoreApp;

    @ApiModelProperty(value = "超时时间（毫秒）", required = true, position = 10)
    private Long timeoutMs;
}
