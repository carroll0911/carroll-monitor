package com.carroll.monitor.analyzer.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Getter
@Setter
public class BmPageRequest extends PageRequest {

    @ApiModelProperty(value = "所属项目id", required = true)
//    @NotBlank(message = "项目id不能为空")
    private String projectId;

    @ApiModelProperty(value = "所属项目tag", required = true)
//    @NotBlank(message = "项目tag不能为空")
    private String projectTag;
}
