package com.carroll.monitor.analyzer.repository;

import com.carroll.monitor.analyzer.model.Sequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * mongo操作基类
 * @author: carroll
 * @date 2019/10/16
 **/
@Repository
public abstract class MongoGenDao<T> {

	@Autowired
	protected MongoTemplate mongoTemplate;

	/**
	 * 保存对象
	 * @param t t
	 */
	public void save(T t) {
        this.mongoTemplate.insert(t);
    }

    /**
     * 批量添加
     * @param list list
     */
    public void insertList(List<T> list){
        this.mongoTemplate.insert(list,this.getEntityClass());
    }

	/**
	 * 根据gid查询对象
	 * @param id id
	 * @return t
	 */
    public T queryById(Long id) {
        Query query = new Query();
        Criteria criteria = Criteria.where("gid").is(id);
        query.addCriteria(criteria);
        return this.mongoTemplate.findOne(query, this.getEntityClass());
    }

    /**
     * 查询列表
     * @param query query
     * @return list
     */
    public List<T> queryList(Query query){
        return this.mongoTemplate.find(query, this.getEntityClass());
    }

    /**
     * 查询单个对象
     * @param query query
     * @return t
     */
    public T queryOne(Query query){
        return this.mongoTemplate.findOne(query, this.getEntityClass());
    }

    /**
     * 分页查询对象
     * @param query query
     * @param start start
     * @param size size
     * @return list
     */
    public List<T> getPage(Query query, int start, int size){
        query.skip(start);
        query.limit(size);
        return this.mongoTemplate.find(query, this.getEntityClass());
    }

    /**
     * 分页查询对象
     * @param query query
     * @return list
     */
    public List<T> getPage(Query query, SpringDataPageable pageable){
        return this.mongoTemplate.find(query.with(pageable), this.getEntityClass());
    }


    /**
     * 根据条件查询库中符合记录的总数,为分页查询服务
     *
     *
     * @param query
     *                     查询条件
     * @return
     *                     满足条件的记录总数
     */
    public Long getPageCount(Query query){
        return this.mongoTemplate.count(query, this.getEntityClass());
    }



    /**
     * 根据Id删除
     *
     *
     * @param id id
     */
    public void deleteById(Long id) {
        Criteria criteria = Criteria.where("gid").in(id);
        if(null!=criteria){
            Query query = new Query(criteria);
            this.mongoTemplate.remove(query, getEntityClass());
        }
    }
    public void deleteByQuery(Query query) {
        this.mongoTemplate.remove(query, getEntityClass());
    }



    /**
     * 删除对象
     *
     *
     * @param t t
     */
    public void delete(T t){
        this.mongoTemplate.remove(t);
    }

    /**
     * 更新满足条件的第一个记录
     *
     * @param query query
     * @param update update
     */
    public void updateFirst(Query query, Update update){
        this.mongoTemplate.updateFirst(query, update, this.getEntityClass());
    }


    /**
     * 更新满足条件的所有记录
     *
     *
     * @param query query
     * @param update update
     */
    public void updateMulti(Query query, Update update){
        this.mongoTemplate.updateMulti(query, update, this.getEntityClass());
    }


    /**
     * 查找更新,如果没有找到符合的记录,则将更新的记录插入库中
     *
     * @param query query
     * @param update update
     */
    public void updateInser(Query query, Update update){
        this.mongoTemplate.upsert(query, update, this.getEntityClass());
    }


	/**
	 * 获取某个表的自动递增id
	 *
	 * @param domain 代表那个实体类或者mongodb表名
	 * @return gid
	 */
	public Long getAutoIncrementingId(String domain){
		Query query = Query.query(Criteria.where("objName").is(domain));
		FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
		Update update = new Update().inc("gid", 1);
        Sequence sequence = mongoTemplate.findAndModify(query, update, options, Sequence.class);
		if (sequence == null) {
            sequence = new Sequence();
            sequence.setGid(1L);
            sequence.setObjName(domain);
            mongoTemplate.save(sequence);
            return 1L;
		}
		return sequence.getGid();
	}

	protected abstract Class<T> getEntityClass();

}
