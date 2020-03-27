package com.carroll.monitor.analyzer.dto;

import com.carroll.monitor.analyzer.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserCacheDto {
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

    /**
     * 所属项目id
     */
    private Map<String, Role> projects;

    /**
     * 角色
     */
    private Role role;
}
