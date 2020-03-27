package com.carroll.monitor.analyzer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

/**
 * Mongodb 操作基类
 *
 * @author: carroll
 * @date 2019/10/16
 **/
public abstract class MongodbBaseDao<T> {

    /**
     * 通过条件查询,查询分页结果
     *
     * @param query
     * @param pageable
     * @return
     */
    public Page<T> getPage(Query query, Pageable pageable) {
        long totalCount = this.getMongoTemplate().count(query, this.getEntityClass());
        query.skip(pageable.getOffset());// skip相当于从那条记录开始
        query.limit(pageable.getPageSize());// 从skip开始,取多少条记录
        List<T> datas = this.find(query);
        return new PageImpl<>(datas, pageable, totalCount);
    }

    /**
     * 通过条件查询实体(集合)
     *
     * @param query
     */
    public List<T> find(Query query) {
        return getMongoTemplate().find(query, this.getEntityClass());
    }

    /**
     * 通过一定的条件查询一个实体
     *
     * @param query
     * @return
     */
    public T findOne(Query query) {
        return getMongoTemplate().findOne(query, this.getEntityClass());
    }

    /**
     * 查询出所有数据
     *
     * @return
     */
    public List<T> findAll() {
        return this.getMongoTemplate().findAll(getEntityClass());
    }

    /**
     * 查询并且修改记录
     *
     * @param query
     * @param update
     * @return
     */
    public T findAndModify(Query query, Update update) {

        return this.getMongoTemplate().findAndModify(query, update, this.getEntityClass());
    }

    /**
     * 按条件查询,并且删除记录
     *
     * @param query
     * @return
     */
    public T findAndRemove(Query query) {
        return this.getMongoTemplate().findAndRemove(query, this.getEntityClass());
    }

    /**
     * 通过条件查询更新数据
     *
     * @param query
     * @param update
     * @return
     */
    public void updateFirst(Query query, Update update) {
        getMongoTemplate().updateFirst(query, update, this.getEntityClass());
    }

    /**
     * 保存一个对象到mongodb
     *
     * @param bean
     * @return
     */
    public T save(T bean) {
        getMongoTemplate().save(bean);
        return bean;
    }

    /**
     * 通过ID获取记录
     *
     * @param id
     * @return
     */
    public T findById(String id) {
        return getMongoTemplate().findById(id, this.getEntityClass());
    }

    /**
     * 通过ID获取记录,并且指定了集合名(表的意思)
     *
     * @param id
     * @param collectionName 集合名
     * @return
     */
    public T findById(String id, String collectionName) {
        return getMongoTemplate().findById(id, this.getEntityClass(), collectionName);
    }

    /**
     * 获取需要操作的实体类class
     *
     * @return
     */
    protected abstract Class<T> getEntityClass();

    /**
     * 注入mongodbTemplate
     */
    protected abstract MongoTemplate getMongoTemplate();

}
