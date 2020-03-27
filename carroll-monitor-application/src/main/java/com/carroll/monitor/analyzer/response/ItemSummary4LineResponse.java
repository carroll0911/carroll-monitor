package com.carroll.monitor.analyzer.response;

import com.carroll.monitor.analyzer.dto.ChartSeriesItem;
import com.carroll.spring.rest.starter.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 告警统计曲线图 请求结果
 *
 * @author: carroll
 * @date 2019/12/11
 *
 */
@Getter
@Setter
public class ItemSummary4LineResponse extends BaseResponse {

    private List<Date> dates;

    private List<String> itemNames;

    private List<ChartSeriesItem> datas;
}
