package com.carroll.monitor.analyzer.request;

import com.carroll.spring.rest.starter.validator.ValueIn;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Getter
@Setter
public class MonitorStatisticRequest extends BmBaseRequest {
    @ApiModelProperty(value = "统计维度, LEVEL:告警级别, SOURCE:告警源, NAME:告警名称")
    @ValueIn(allowValues = {"LEVEL", "SOURCE", "NAME"})
    private String type;

    @ApiModelProperty(value = "告警数据状态, NORMAL: 未清除, CLEARED: 已清除")
    @ValueIn(allowValues = {"NORMAL", "CLEARED"})
    private String status;
}
