package com.carroll.monitor.analyzer.repository;

import com.carroll.monitor.analyzer.model.MonitorItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author: carroll
 * @date 2019/10/16
 */
public class MonitorItemRepositoryImpl extends MongodbBaseDao<MonitorItem> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<MonitorItem> advanceQuery(List<String> projectIds, String id, String level) {
        Criteria criteria = new Criteria();
        if (!CollectionUtils.isEmpty(projectIds)) {
            if (projectIds.size() == 1) {
                criteria.and("projectId").is(projectIds.get(0));
            } else {
                criteria.and("projectId").in(projectIds);
            }
        }

        if (!StringUtils.isEmpty(level)) {
            criteria = criteria.and("level").is(level);
        }
        if (!StringUtils.isEmpty(id)) {
            criteria = criteria.and("id").is(id);
        }
        Query query = new Query(criteria);
        return find(query);
    }

    public Page<MonitorItem> advanceQuery(String projectId, Pageable pageable) {
        Criteria criteria = new Criteria();
        criteria.and("projectId").is(projectId);
        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.DESC, "updateTime"));
        return getPage(query, pageable);
    }

    public Page<MonitorItem> advanceQuery(String projectId, String keyword, Pageable pageable) {
        Criteria criteria = new Criteria();
        criteria.and("projectId").is(projectId);
        if (!StringUtils.isEmpty(keyword)) {
            Criteria criteria1;
            criteria1 = new Criteria();
            Pattern pattern = Pattern.compile("^.*" + keyword + ".*$", Pattern.CASE_INSENSITIVE);
            criteria1 = criteria1.orOperator(Criteria.where("name").regex(pattern), Criteria.where("tag").regex(pattern));
            criteria.andOperator(criteria1);
        }
        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.DESC, "updateTime"));
        return getPage(query, pageable);
    }

    public Page<MonitorItem> advanceQuery(List<String> projectIds, String keyword, Pageable pageable) {
        Criteria criteria = new Criteria();
        if (!CollectionUtils.isEmpty(projectIds)) {
            if (projectIds.size() == 1) {
                criteria.and("projectId").is(projectIds.get(0));
            } else {
                criteria.and("projectId").in(projectIds);
            }
        }

        if (!StringUtils.isEmpty(keyword)) {
            Criteria criteria1;
            criteria1 = new Criteria();
            Pattern pattern = Pattern.compile("^.*" + keyword + ".*$", Pattern.CASE_INSENSITIVE);
            criteria1 = criteria1.orOperator(Criteria.where("name").regex(pattern), Criteria.where("tag").regex(pattern));
            criteria.andOperator(criteria1);
        }
        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.DESC, "updateTime"));
        return getPage(query, pageable);
    }

    @Override
    protected Class<MonitorItem> getEntityClass() {
        return MonitorItem.class;
    }

    @Override
    protected MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
