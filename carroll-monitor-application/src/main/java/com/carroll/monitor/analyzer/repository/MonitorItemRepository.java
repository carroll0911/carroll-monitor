package com.carroll.monitor.analyzer.repository;

import com.carroll.monitor.analyzer.model.MonitorItem;
import com.carroll.monitor.analyzer.model.Operator;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author: carroll
 * @date 2019/10/16
 */
public interface MonitorItemRepository extends MongoRepository<MonitorItem, String> {

    MonitorItem findByTag(String tag);

    MonitorItem findTopByTagAndProjectId(String tag, String projectId);

    @Cacheable(value = "monitorItem#2*60*60")
    MonitorItem findByProjectIdAndId(String projectId, String id);

    @Cacheable(value = "monitorItem#list#byProject#2*60*60")
    List<MonitorItem> findByProjectId(String projectId);

    List<MonitorItem> findByProjectIdIsIn(List<String> projectIds);

    @Cacheable(value = "monitorItem#list#byLevel#2*60*60")
    List<MonitorItem> findByLevelAndProjectId(MonitorItem.Level level, String projectId);

    List<MonitorItem> advanceQuery(List<String> projectIds, String id, String level);

    Page<MonitorItem> advanceQuery(String projectId, Pageable pageable);

    Page<MonitorItem> advanceQuery(String projectId, String keyword, Pageable pageable);
    Page<MonitorItem> advanceQuery(List<String> projectIds, String keyword, Pageable pageable);

    @Cacheable(value = "monitorItem#list#ByReceiver#2*60*60")
    List<MonitorItem> findByReceivers(Operator operator);
}
