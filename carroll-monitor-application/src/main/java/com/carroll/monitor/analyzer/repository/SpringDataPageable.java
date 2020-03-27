package com.carroll.monitor.analyzer.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

/**
 * 分页
 *
 * @author: carroll
 * @date 2019/10/16
 **/
public class SpringDataPageable implements Serializable, Pageable {
    private static final long serialVersionUID = 1;
    // 当前页
    private Integer pagenumber = 1;
    // 当前页面条数
    private Integer pagesize = 10000;
    // 排序条件
    private Sort sort;

    // 当前页面
    @Override
    public int getPageNumber() {
        return pagenumber;
    }

    // 每一页显示的条数
    @Override
    public int getPageSize() {
        return pagesize;
    }

    // 第二页所需要增加的数量
    @Override
    public int getOffset() {
        return (pagenumber - 1) * pagesize;
    }

    @Override
    public Sort getSort() {
        return sort;
    }


    public void setPagenumber(Integer pagenumber) {
        this.pagenumber = pagenumber;
    }


    public void setPagesize(Integer pagesize) {
        this.pagesize = pagesize;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    @Override
    public Pageable first() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasPrevious() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Pageable next() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Pageable previousOrFirst() {
        // TODO Auto-generated method stub
        return null;
    }
}
