package com.carroll.monitor.analyzer.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 告警数据
 * @author: carroll
 * @date 2019/9/9
 */
@Getter
@Setter
public class WarningDataCallBackDto extends BaseDto {
    //应用tag
    @ApiModelProperty(value = "应用tag")
    private String projectTag;
    //回调地址
    @ApiModelProperty(value = "回调地址")
    private String callback;
    //启用状态
    @ApiModelProperty(value = "启用状态")
    private boolean enable;
}
