package com.carroll.monitor.analyzer.response;

import com.carroll.spring.rest.starter.BaseResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: carroll
 * @date 2019/12/3
 *
 */
@Getter
@Setter
public class TraceDetailResponse extends BaseResponse {

    private Object data;
}
