package com.carroll.monitor.analyzer.dto;

import com.carroll.monitor.analyzer.enums.Role;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@ApiModel
@Getter
@Setter
public class OperatorDtoV2 {
    private String id;
    @ApiModelProperty(value = "告警人员名称", position = 1)
    private String name;
    @ApiModelProperty(value = "告警人员邮件", position = 2)
    private String email;
    @ApiModelProperty(value = "告警人员手机号码", position = 3)
    private String mobile;
    @ApiModelProperty(value = "角色", position = 4)
    private Role role;
}
