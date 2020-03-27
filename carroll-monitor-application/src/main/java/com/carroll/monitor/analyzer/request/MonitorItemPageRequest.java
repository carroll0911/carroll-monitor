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
public class MonitorItemPageRequest extends BmPageRequest {
    @ApiModelProperty("搜索关键字")
    private String keyword;
}
