package com.carroll.monitor.analyzer.enums;

/**
 * 用户角色
 *
 * @author: carroll
 * @date 2019/9/9
 */
public enum Role {
    SUPPER("超级管理员"),
    ADMIN("管理员"),
    NORMAL("普通用户");

    private String desc;

    Role(String desc) {
        this.desc = desc;
    }
}
