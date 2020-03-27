package com.carroll.monitor.analyzer.service;


import com.carroll.monitor.analyzer.model.BaseServiceMonitorItem;

import java.util.List;

/**
 * @author: carroll
 * @date 2019/9/9
 */
public interface IBSMonitorItemService {

    List<BaseServiceMonitorItem> findAllByTagAndProjectId(String tag, String projectId);
}
