package com.carroll.monitor.analyzer.response;

import com.carroll.monitor.analyzer.dto.ConstantDto;
import com.carroll.spring.rest.starter.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Getter
@Setter
public class MonitorItemConstantResponse extends BaseResponse {
    List<ConstantDto> list;
}
