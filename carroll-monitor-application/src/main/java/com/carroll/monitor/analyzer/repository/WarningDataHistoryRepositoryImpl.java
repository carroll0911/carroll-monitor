package com.carroll.monitor.analyzer.repository;

import com.carroll.monitor.analyzer.model.WarningData;
import com.carroll.monitor.analyzer.model.WarningDataHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * @author: carroll
 * @date 2019/10/16
 */
public class WarningDataHistoryRepositoryImpl extends MongodbBaseDao<WarningDataHistory> {

    private static final String STATUS_KEY = "status";
    private static final String PROJECT_ID_KEY = "projectId";
    private static final String ITEM_ID_KEY = "itemId";
    private static final String FIRST_TIME_KEY = "firstTime";
    private static final String APP_NAME_KEY = "applicationName";
    private static final String TARGET_KEY = "target";

    @Autowired
    private MongoTemplate mongoTemplate;

    public Page<WarningDataHistory> advanceQuery(Date firstStartTime, Date firstEndTime, Date recoveryStartTime, Date recoveryEndTime,
                                                 List<String> itemIds, String applicationName, String target, List<String> projectIds, Pageable pageable) {
        Criteria criteria = new Criteria();
        criteria.and(STATUS_KEY).is(WarningData.Status.CLEARED);
        if (firstStartTime != null || firstEndTime != null) {
            criteria = criteria.and(FIRST_TIME_KEY);
            if (firstStartTime != null) {
                criteria = criteria.gte(firstStartTime);
            }
            if (firstEndTime != null) {
                criteria = criteria.lte(firstEndTime);
            }
        }

        if (recoveryStartTime != null || recoveryEndTime != null) {
            criteria = criteria.and("recoveryTime");
            if (recoveryStartTime != null) {
                criteria = criteria.gte(recoveryStartTime);
            }
            if (recoveryEndTime != null) {
                criteria = criteria.lte(recoveryEndTime);
            }
        }
        if (!CollectionUtils.isEmpty(itemIds)) {
            if (itemIds.size() == 1) {
                criteria = criteria.and(ITEM_ID_KEY).is(itemIds.get(0));
            } else {
                criteria = criteria.and(ITEM_ID_KEY).in(itemIds);
            }
        }
        if (!CollectionUtils.isEmpty(projectIds)) {
            if (projectIds.size() == 1) {
                criteria = criteria.and(PROJECT_ID_KEY).is(projectIds.get(0));
            } else {
                criteria = criteria.and(PROJECT_ID_KEY).in(projectIds);
            }
        }
        if (!StringUtils.isEmpty(applicationName)) {
            criteria = criteria.and(APP_NAME_KEY).is(applicationName);
        }
        if (!StringUtils.isEmpty(target)) {
            criteria = criteria.and(TARGET_KEY).is(target);
        }
        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.DESC, "recoveryTime"));
        return getPage(query, pageable);
    }

    @Override
    protected Class<WarningDataHistory> getEntityClass() {
        return WarningDataHistory.class;
    }

    @Override
    protected MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

}
