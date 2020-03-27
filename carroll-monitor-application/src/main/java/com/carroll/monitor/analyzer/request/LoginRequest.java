package com.carroll.monitor.analyzer.request;

import com.carroll.spring.rest.starter.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Getter
@Setter
@ApiModel
public class LoginRequest extends BaseRequest {
    /**
     * 登陆的手机号或Email
     */
    @ApiModelProperty(value = "邮箱或手机号", position = 1,required = true)
    @NotBlank
    private String phoneOrEmail;
    /**
     * 密码  MD5
     */
    @ApiModelProperty(value = "密码，非空，MD5加密后的密文，长度32位", position = 2,required = true)
    @NotBlank
    @Length(max = 32,message = "长度错误")
    private String password;
}
