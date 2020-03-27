package com.carroll.monitor.analyzer.response;

import com.carroll.monitor.analyzer.dto.WarningDataPageDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Getter
@Setter
public class HistoryMonitorPageResponse extends PageResponse {

    private List<WarningDataPageDto> list;
}
