package com.carroll.monitor.analyzer.repository;


import com.carroll.monitor.analyzer.model.BaseServiceMonitorItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author: carroll
 * @date 2019/10/16
 */
public interface BaseServiceMonitorItemRepository extends MongoRepository<BaseServiceMonitorItem, String> {

    List<BaseServiceMonitorItem> findAllByTagAndProjectId(String tag, String projectId);
}
