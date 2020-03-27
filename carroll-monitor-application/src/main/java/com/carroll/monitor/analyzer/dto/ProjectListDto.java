package com.carroll.monitor.analyzer.dto;

import com.carroll.monitor.analyzer.model.Project;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@ApiModel
@Getter
@Setter
public class ProjectListDto {

    @ApiModelProperty("id")
    private String id;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("应用标签")
    private String tag;
    @ApiModelProperty("管理员列表")
    private List<OperatorDtoV2> admins;
    @ApiModelProperty("状态")
    private Project.Status status;
}
