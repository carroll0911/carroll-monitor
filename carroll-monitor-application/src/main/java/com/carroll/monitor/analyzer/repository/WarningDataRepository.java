package com.carroll.monitor.analyzer.repository;

import com.carroll.monitor.analyzer.model.WarningData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

/**
 * @author: carroll
 * @date 2019/10/16
 */
public interface WarningDataRepository extends MongoRepository<WarningData, String> {

    List<WarningData> findByItemIdAndApplicationNameAndStatusAndHost(String tag, String applicationName, WarningData.Status status, String host);

    Page<WarningData> advanceQueryByPage(Date firstStartTime, Date firstEndTime, Date updateStartTime, Date updateEndTime,
                                         List<String> itemIds, String applicationName, String target, List<String> projectIds, Pageable pageable);

    int currentStatistic(Date firstStartTime, Date firstEndTime, Date updateStartTime, Date updateEndTime,
                         List<String> itemIds, String applicationName, String target, String status, List<String> projectIds);

    List<WarningData> findWarnData(String itemId, String applicationName, WarningData.Status status, String host, String target);

    int countAllByItemId(String itemId);
}
