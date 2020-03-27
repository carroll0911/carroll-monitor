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
public class ProjectDto extends BaseDto {
    @ApiModelProperty(value = "名称", position = 1)
    private String name;
    @ApiModelProperty(value = "tag", position = 2)
    private String tag;
    @ApiModelProperty(value = "密码", position = 3)
    private String password;
}
