package com.carroll.monitor.analyzer.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Getter
@Setter
public class BaseDto {
    @ApiModelProperty(value = "id")
    @NotBlank
    private String id;
}
