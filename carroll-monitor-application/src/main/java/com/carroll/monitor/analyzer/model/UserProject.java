package com.carroll.monitor.analyzer.model;

import com.carroll.monitor.analyzer.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用户信息
 * @author: carroll
 * @date 2019/10/16
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProject extends BaseModel {

    /**
     * 项目ID
     */
    private String projectId;
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 角色
     */
    private Role role;

}
