package com.carroll.monitor.analyzer.repository;

import com.carroll.monitor.analyzer.model.WarningDataHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

/**
 * @author: carroll
 * @date 2019/10/23
 */
public interface WarningDataHistoryRepository extends MongoRepository<WarningDataHistory, String> {

    Page<WarningDataHistory> advanceQuery(Date firstStartTime, Date firstEndTime, Date recoveryStartTime, Date recoveryEndTime,
                                          List<String> itemIds, String applicationName, String target, List<String> projectIds, Pageable pageable);

    int countAllByItemId(String itemId);
}
