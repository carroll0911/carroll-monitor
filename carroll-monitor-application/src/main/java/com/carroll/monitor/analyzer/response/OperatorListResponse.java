package com.carroll.monitor.analyzer.response;

import com.carroll.monitor.analyzer.dto.OperatorDto;
import com.carroll.spring.rest.starter.BaseResponse;
import lombok.Data;

import java.util.List;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Data
public class OperatorListResponse extends BaseResponse {

    private List<OperatorDto> list;
}
