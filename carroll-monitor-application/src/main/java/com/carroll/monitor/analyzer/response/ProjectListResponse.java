package com.carroll.monitor.analyzer.response;

import com.carroll.monitor.analyzer.dto.ProjectListDto;
import com.carroll.spring.rest.starter.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author: carroll
 * @date 2019/9/4
 *
 */
@Getter
@Setter
public class ProjectListResponse extends BaseResponse {

    private List<ProjectListDto> list;
}
