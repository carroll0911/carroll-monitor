package com.carroll.monitor.analyzer.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Getter
@Setter
public class OperatorUpdateRequest extends BmBaseRequest {
    @ApiModelProperty(value = "id", required = true, position = 0)
    @NotBlank(message = "id不能为空")
    private String id;

    @ApiModelProperty(value = "姓名", required = true, position = 1)
    @NotBlank
    @Pattern(regexp ="^[a-zA-Z\\-\u4e00-\u9fa5]{2,10}$",message = "2-10位中文/英文字符")
    @Length(min = 2,max = 10,message = "长度不对")
    private String name;

    @ApiModelProperty(value = "邮箱", required = true, position = 2)
    @NotBlank
    @Pattern(regexp ="\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,100}",message = "邮箱格式不对")
    @Length(max = 100, message = "长度不对")
    private String email;

    @ApiModelProperty(value = "电话", required = true, position = 3)
    @NotBlank(message = "电话不能为空")
    @Pattern(regexp ="[0-9-()（）]{7,18}",message = "手机号码格式不对")
    @Length(min = 11, max = 11,message = "长度不对")
    private String mobile;
}
