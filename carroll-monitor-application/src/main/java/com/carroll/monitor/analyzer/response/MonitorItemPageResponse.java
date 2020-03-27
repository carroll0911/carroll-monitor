package com.carroll.monitor.analyzer.response;

import com.carroll.monitor.analyzer.dto.MonitorItemDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Getter
@Setter
public class MonitorItemPageResponse extends PageResponse {
    private List<MonitorItemDto> list;
}
