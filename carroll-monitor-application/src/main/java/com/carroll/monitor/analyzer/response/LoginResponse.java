package com.carroll.monitor.analyzer.response;

import com.carroll.monitor.analyzer.enums.Role;
import com.carroll.spring.rest.starter.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 登录返回对象
 *
 * @author: carroll
 * @date 2019/9/9
 *
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse extends BaseResponse {

    /**
     * 登录用户token
     */
    private String token;
    /**
     * 角色
     */
    private Role role;
    /**
     * 姓名
     */
    private String name;
    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String mobile;

    public LoginResponse(String code, String msg) {
        setReturnErrCode(code);
        setReturnErrMsg(msg);
    }
}
