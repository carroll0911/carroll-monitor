package com.carroll.monitor.analyzer.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Getter
@Setter
public class LogDataDto {
    @ApiModelProperty(value = "监控点调用参数")
    private String params;
    @ApiModelProperty(value = "监控点调用结果")
    private String response;
    /**
     * 调用链ID
     */
    @ApiModelProperty(value = "调用链ID")
    private String traceId;
}
