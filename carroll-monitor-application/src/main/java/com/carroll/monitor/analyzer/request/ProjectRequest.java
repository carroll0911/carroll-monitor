package com.carroll.monitor.analyzer.request;

import com.carroll.spring.rest.starter.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Getter
@Setter
public class ProjectRequest extends BaseRequest {
    @ApiModelProperty(value = "名称", required = true)
    @NotBlank(message = "名称不能为空")
    private String name;

    @ApiModelProperty(value = "tag", required = true)
    @NotBlank(message = "tag不能为空")
    private String tag;

    /**
     * 用户ID列表
     */
    @ApiModelProperty(value = "用户id列表")
    private List<String> users;

    /**
     * 管理员用户id列表
     */
    @ApiModelProperty(value = "管理员用户id列表")
    private List<String> admins;
}
