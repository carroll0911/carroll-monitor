package com.carroll.monitor.analyzer.response;

import com.carroll.monitor.analyzer.dto.ProjectDetailDto;
import com.carroll.spring.rest.starter.BaseResponse;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: carroll
 * @date 2019/9/29
 *
 */
@ApiModel
@Getter
@Setter
public class ProjectDetailResponse extends BaseResponse {

    private ProjectDetailDto data;
}
