package com.carroll.monitor.analyzer.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Getter
@Setter
public class WarningDataStatisticDto implements Serializable{
    @ApiModelProperty(value = "统计维度描述")
    private String desc;

    @ApiModelProperty(value = "次数")
    private Long times;

    @ApiModelProperty(value = "状态")
    private String status;
}
