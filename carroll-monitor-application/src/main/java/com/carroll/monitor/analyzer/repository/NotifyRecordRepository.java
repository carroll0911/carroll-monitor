package com.carroll.monitor.analyzer.repository;

import com.carroll.monitor.analyzer.model.NotifyRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author: carroll
 * @date 2019/10/16
 */
public interface NotifyRecordRepository extends MongoRepository<NotifyRecord, String> {

}
