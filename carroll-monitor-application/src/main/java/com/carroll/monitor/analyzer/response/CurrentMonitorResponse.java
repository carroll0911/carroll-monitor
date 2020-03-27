package com.carroll.monitor.analyzer.response;

import com.carroll.monitor.analyzer.dto.CurrentMonitorDto;
import com.carroll.monitor.analyzer.dto.WarningDataStatisticDto;
import lombok.Data;

import java.util.List;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Data
public class CurrentMonitorResponse extends PageResponse {
    private List<WarningDataStatisticDto> statisticList;

    private List<CurrentMonitorDto> list;
}
