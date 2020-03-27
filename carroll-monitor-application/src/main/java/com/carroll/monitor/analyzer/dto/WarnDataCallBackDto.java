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
public class WarnDataCallBackDto {

    @ApiModelProperty(value = "应用名称")
    private String projectName;
    @ApiModelProperty(value = "应用ID")
    private String projectId;
    @ApiModelProperty(value = "应用Tag")
    private String projectTag;

    @ApiModelProperty(value = "推送地址")
    private String callback;

    @ApiModelProperty(value = "启用状态")
    private boolean enable;
}
