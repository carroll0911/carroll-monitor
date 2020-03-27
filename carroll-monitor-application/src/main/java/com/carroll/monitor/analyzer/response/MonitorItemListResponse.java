package com.carroll.monitor.analyzer.response;

import com.carroll.monitor.analyzer.dto.MonitorItemListDto;
import com.carroll.spring.rest.starter.BaseResponse;
import lombok.Data;

import java.util.List;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Data
public class MonitorItemListResponse extends BaseResponse {
    private List<MonitorItemListDto> list;
}
