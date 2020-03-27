package com.carroll.monitor.analyzer.repository;

import com.carroll.monitor.analyzer.model.WarningData;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
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
import java.util.regex.Pattern;

/**
 * @author: carroll
 * @date 2019/10/16
 */
public class WarningDataRepositoryImpl extends MongodbBaseDao<WarningData> {

    private static final String STATUS_KEY = "status";
    private static final String PROJECT_ID_KEY = "projectId";
    private static final String ITEM_ID_KEY = "itemId";
    private static final String FIRST_TIME_KEY = "firstTime";
    private static final String APP_NAME_KEY = "applicationName";
    private static final String TARGET_KEY = "target";

    @Autowired
    private MongoTemplate mongoTemplate;
    public Page<WarningData> advanceQueryByPage(Date firstStartTime, Date firstEndTime, Date updateStartTime, Date updateEndTime,
                                                List<String> itemIds, String applicationName, String target, List<String> projectIds, Pageable pageable) {
        Criteria criteria = new Criteria();
        criteria.and(STATUS_KEY).is(WarningData.Status.NORMAL);
        //产生时间
        if (firstStartTime != null || firstEndTime != null) {
            criteria = criteria.and(FIRST_TIME_KEY);
            if (firstStartTime != null) {
                criteria = criteria.gte(firstStartTime);
            }
            if (firstEndTime != null) {
                criteria = criteria.lte(firstEndTime);
            }
        }
        //更新时间
        if (updateStartTime != null || updateEndTime != null) {
            criteria = criteria.and("updateTime");
            if (updateStartTime != null) {
                criteria = criteria.gte(updateStartTime);
            }
            if (updateEndTime != null) {
                criteria = criteria.lte(updateEndTime);
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
        //告警源
        if (!StringUtils.isEmpty(applicationName)) {
            Pattern pattern = Pattern.compile(String.format("^.*%s.*$", applicationName));
            criteria = criteria.and(APP_NAME_KEY).regex(pattern);
        }
        //告警对象
        if (!StringUtils.isEmpty(target)) {
            criteria = criteria.and(TARGET_KEY).is(target);
        }
        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.DESC, "latestTime"));
        return getPage(query, pageable);
    }

    public int currentStatistic(Date firstStartTime, Date firstEndTime, Date updateStartTime, Date updateEndTime,
                                List<String> itemIds, String applicationName, String target, String status, List<String> projectIds) {
        BasicDBObject query = new BasicDBObject();
        query.put(STATUS_KEY, status);
        if (!CollectionUtils.isEmpty(projectIds)) {
            if (projectIds.size() == 1) {
                query.put(PROJECT_ID_KEY, projectIds.get(0));
            } else {
                query.put(PROJECT_ID_KEY, new BasicDBObject("$in", projectIds));
            }
        }
        //产生时间
        if (firstStartTime != null || firstEndTime != null) {
            BasicDBObject queryCondition = new BasicDBObject();
            if (firstStartTime != null) {
                queryCondition.append("$gte", firstStartTime);
            }
            if (firstEndTime != null) {
                queryCondition.append("$lte", firstEndTime);
            }
            query.put(FIRST_TIME_KEY, queryCondition);
        }
        //更新时间
        if (updateStartTime != null || updateEndTime != null) {
            BasicDBObject queryCondition = new BasicDBObject();
            if (updateStartTime != null) {
                queryCondition.append("$gte", updateStartTime);
            }
            if (updateEndTime != null) {
                queryCondition.append("$lte", updateEndTime);
            }
            query.put("updateTime", queryCondition);
        }
        //告警名称
        if (!CollectionUtils.isEmpty(itemIds)) {
            if (itemIds.size() == 1) {
                query.put(ITEM_ID_KEY, itemIds.get(0));
            } else {
                query.put(ITEM_ID_KEY, new BasicDBObject("$in", itemIds));
            }
        }
        //告警源
        if (!StringUtils.isEmpty(applicationName)) {
            query.put(APP_NAME_KEY, applicationName);
        }
        //告警对象
        if (!StringUtils.isEmpty(target)) {
            query.put(TARGET_KEY, target);
        }
        DBCursor dbCursor = mongoTemplate.getCollection("warningData").find(query);
        return dbCursor.count();
    }

    @Override
    protected Class<WarningData> getEntityClass() {
        return WarningData.class;
    }

    @Override
    protected MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    public List<WarningData> findWarnData(String itemId, String applicationName, WarningData.Status status, String host, String target) {
        Criteria criteria = new Criteria();
        criteria.and(STATUS_KEY).is(status);
        criteria.and(ITEM_ID_KEY).is(itemId);

        Criteria criteria1;
        if (!StringUtils.isEmpty(applicationName)) {
            criteria1 = Criteria.where(APP_NAME_KEY).is(applicationName);
        } else {
            criteria1 = new Criteria();
            criteria1 = criteria1.orOperator(Criteria.where(APP_NAME_KEY).is(target), Criteria.where(APP_NAME_KEY).exists(false));
        }
        Criteria criteria2;
        if (!StringUtils.isEmpty(host)) {
            criteria2 = Criteria.where("host").is(host);
        } else {
            criteria2 = new Criteria();
            criteria2 = criteria2.orOperator(Criteria.where("host").is(target), Criteria.where("host").exists(false));
        }

        Criteria criteria3;
        if (!StringUtils.isEmpty(target)) {
            criteria3 = Criteria.where(TARGET_KEY).is(target);
        } else {
            criteria3 = new Criteria();
            criteria3 = criteria3.orOperator(Criteria.where(TARGET_KEY).is(target), Criteria.where(TARGET_KEY).exists(false));
        }

        criteria = criteria.andOperator(criteria1, criteria2, criteria3);
        Query query = new Query(criteria);
        return find(query);
    }
}
