package com.carroll.monitor.analyzer.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Setter
@Getter
public class ChartSeriesItem {

    private String name;
    private String type;
    private List<Object> data;
}
