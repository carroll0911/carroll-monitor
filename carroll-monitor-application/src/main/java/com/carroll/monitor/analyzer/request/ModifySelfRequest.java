package com.carroll.monitor.analyzer.request;

import com.carroll.spring.rest.starter.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;

/**
 * @author: carroll
 * @date 2019/9/25
 *
 */
@Getter
@Setter
@ApiModel
public class ModifySelfRequest extends BaseRequest {

    @ApiModelProperty(value = "邮箱", required = true, position = 2)
    @NotBlank
    @Pattern(regexp ="\\w[-\\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\\.)+[A-Za-z]{2,100}",message = "邮箱格式不对")
    @Length(max = 100, message = "长度不对")
    private String email;

    @ApiModelProperty(value = "电话", required = true, position = 3)
    @NotBlank
    @Pattern(regexp ="[0-9-()（）]{7,18}",message = "手机号码格式不对")
    @Length(min = 11, max = 11,message = "长度不对")
    private String mobile;

}
