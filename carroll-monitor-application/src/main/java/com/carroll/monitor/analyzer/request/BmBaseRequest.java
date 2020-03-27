package com.carroll.monitor.analyzer.request;

import com.carroll.spring.rest.starter.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Getter
@Setter
public class BmBaseRequest extends BaseRequest {

    @ApiModelProperty(value = "所属项目id")
    private String projectId;

    @ApiModelProperty(value = "所属项目tag")
    private String projectTag;
}
