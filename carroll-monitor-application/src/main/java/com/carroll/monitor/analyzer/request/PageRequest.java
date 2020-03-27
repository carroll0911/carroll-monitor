package com.carroll.monitor.analyzer.request;

import com.carroll.spring.rest.starter.BaseRequest;
import com.carroll.spring.rest.starter.infra.IPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@ApiModel
public class PageRequest extends BaseRequest implements IPage {
    @ApiModelProperty(value = "页码，整数，索引从0开始", required = true)
    @Min(value = 0)
    private Integer curPage;
    @ApiModelProperty(value = "页大小，非0整数，最大极值500", required = true)
    @DecimalMin(message = "页大小为非0整数", value = "1")
    @DecimalMax(message = "页大小最大极值500",value = "500")
    private Integer pageSize=20;

    @Override
    public Integer getCurPage() {
        return curPage;
    }

    @Override
    public void setCurPage(Integer i) {
        this.curPage = i;
    }

    @Override
    public Integer getPageSize() {
        return pageSize;
    }

    @Override
    public void setPageSize(Integer i) {
        this.pageSize = i;
    }
}
