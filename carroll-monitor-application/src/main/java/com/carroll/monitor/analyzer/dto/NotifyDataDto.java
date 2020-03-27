package com.carroll.monitor.analyzer.dto;

import com.carroll.monitor.analyzer.model.MonitorItem;
import com.carroll.monitor.analyzer.model.WarningData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 告警通知数据模型
 * @author: carroll
 * @date 2019/9/9 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotifyDataDto {

    private boolean recovery;
    private MonitorItem monitorItem;
    private WarningData warningData;
}
