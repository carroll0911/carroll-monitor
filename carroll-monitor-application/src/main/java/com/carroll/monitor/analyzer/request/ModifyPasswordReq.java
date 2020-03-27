package com.carroll.monitor.analyzer.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 修改密码请求
 *
 * @author: carroll
 * @date 2019/9/29
 */
@ApiModel
@Getter
@Setter
public class ModifyPasswordReq {
    @ApiModelProperty(value = "旧密码，非空，MD5加密后的密文，长度32位", position = 1,required = true)
    @NotBlank
    @Length(max = 32,message = "长度错误")
    private String oldPwd;
    @ApiModelProperty(value = "密码，非空，MD5加密后的密文，长度32位", position = 2,required = true)
    @NotBlank
    @Length(max = 32,message = "长度错误")
    private String newPwd;

}
