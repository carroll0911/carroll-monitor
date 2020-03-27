package com.carroll.monitor.analyzer.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 告警数据
 * @author: carroll
 * @date 2019/10/16 
 */
@Getter
@Setter
public class WarningDataCallBack extends BaseModel {
    //应用tag
    private String projectTag;
    //回调地址
    private String callback;
    //启用状态
    private boolean enable;
}
