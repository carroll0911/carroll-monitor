package com.carroll.monitor.analyzer.repository;

import com.carroll.monitor.analyzer.model.Operator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author: carroll
 * @date 2019/10/16 
 */
public class OperatorRepositoryImpl extends MongodbBaseDao<Operator> {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Page<Operator> advanceQuery(List<String> projectIds, Pageable pageable) {
        Criteria criteria = new Criteria();
        if(!CollectionUtils.isEmpty(projectIds)) {
            if(projectIds.size()==1){
                criteria.and("projectId").is(projectIds.get(0));
            } else {
                criteria.and("projectId").in(projectIds);
            }
        }
        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.DESC, "updateTime"));
        return getPage(query,pageable);
    }

    @Override
    protected Class<Operator> getEntityClass() {
        return Operator.class;
    }

    @Override
    protected MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
