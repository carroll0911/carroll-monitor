package com.carroll.monitor.analyzer.repository;

import com.carroll.monitor.analyzer.model.WarningData;
import org.springframework.stereotype.Repository;

/**
 * @author: carroll
 * @date 2019/10/16
 */
@Repository
public class WarningDataMongoDao extends MongoGenDao<WarningData> {
    @Override
    protected Class<WarningData> getEntityClass() {
        return WarningData.class;
    }
}
