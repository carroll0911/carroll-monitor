package com.carroll.monitor.analyzer.model;

import com.carroll.monitor.analyzer.enums.Role;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * 运维人员
 * @author: carroll
 * @date 2019/10/16
 */
@Getter
@Setter
public class Operator {
    @Id
    private String id;

    /**
     * 姓名
     */
    private String name;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 邮箱
     */
    private String email;


    @CreatedDate
    private Date createTime;

    private Date updateTime;
    /**
     * 角色
     */
    private Role role;
    /**
     * 密码
     */
    private String password;
}
