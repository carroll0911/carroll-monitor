package com.carroll.monitor.analyzer.repository;

import com.carroll.monitor.analyzer.model.WarningDataCallBack;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author: carroll
 * @date 2019/10/16 
 */
public interface WarnDataCallBackRepository extends MongoRepository<WarningDataCallBack, String> {

    WarningDataCallBack findByProjectTagAndEnable(String tag, boolean enable);

    WarningDataCallBack findByProjectTag(String tag);
}
