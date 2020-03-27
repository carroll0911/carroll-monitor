package com.carroll.monitor.analyzer.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@ApiModel
@Getter
@Setter
public class ProjectDetailDto {

    @ApiModelProperty("id")
    private String id;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("应用密码")
    private String password;
    @ApiModelProperty("应用标签")
    private String tag;
    @ApiModelProperty("应用授权用户列表")
    private List<OperatorDtoV2> users;
}
