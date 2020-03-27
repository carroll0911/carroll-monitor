package com.carroll.monitor.analyzer.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

/**
 * @author: carroll
 * @date 2017/11/22
 */
@Getter
@Setter
public class BaseModel {

    @Id
    private String id;

    @CreatedDate
    private Date createTime;
    @LastModifiedDate
    private Date updateTime;
}
