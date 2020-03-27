package com.carroll.monitor.analyzer.controller;

import com.carroll.monitor.analyzer.enums.ErrEnum;
import com.carroll.monitor.analyzer.request.*;
import com.carroll.monitor.analyzer.response.OperatorListResponse;
import com.carroll.monitor.analyzer.response.OperatorPageResponse;
import com.carroll.monitor.analyzer.response.OperatorResponse;
import com.carroll.monitor.analyzer.service.IOperatorService;
import com.carroll.spring.rest.starter.BaseController;
import com.carroll.spring.rest.starter.BaseResponse;
import com.carroll.utils.BeanUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author: carroll
 * @date 2019/9/9 
 */
@Slf4j
@Api(value = "OperatorController",description = "运营人员管理")
@RestController
@RequestMapping("/operator")
public class OperatorController extends BaseController {

    @Autowired
    private IOperatorService operatorService;

    @ApiOperation(value = "新增运营人员")
    @RequestMapping(method = RequestMethod.POST)
    public OperatorResponse save(@RequestBody @Valid final OperatorRequest request, BindingResult result) {
        return operatorService.save(request);
    }

    @SuppressWarnings("unused")
    public OperatorResponse saveFallBack(OperatorRequest request, BindingResult result, Throwable throwable) {
        OperatorResponse response = new OperatorResponse();
        BeanUtils.copyPropertiesIgnorException(fallBackResponse(throwable), response);
        return response;
    }

    @ApiOperation(value = "编辑运营人员")
    @RequestMapping(method = RequestMethod.PUT)
    public OperatorResponse update(@Valid @RequestBody OperatorUpdateRequest request, BindingResult result) {
        return operatorService.update(request);
    }

    @SuppressWarnings("unused")
    public OperatorResponse updateFallBack(OperatorUpdateRequest request, BindingResult result, Throwable throwable) {
        OperatorResponse response = new OperatorResponse();
        BeanUtils.copyPropertiesIgnorException(fallBackResponse(throwable), response);
        return response;
    }

    @ApiOperation(value = "删除运营人员")
    @RequestMapping(method = RequestMethod.DELETE)
    public BaseResponse delete(@Valid @ModelAttribute IdRequest request, BindingResult result) {
        return operatorService.delete(request);
    }

    @SuppressWarnings("unused")
    public BaseResponse deleteFallBack(IdRequest request, BindingResult result, Throwable throwable) {
        return fallBackResponse(throwable);
    }

    @ApiOperation(value = "分页查询运营人员")
    @RequestMapping(method = RequestMethod.GET)
    public OperatorPageResponse page(@Valid @ModelAttribute OperatorPageRequest request, BindingResult result) {
        return operatorService.page(request);
    }

    @SuppressWarnings("unused")
    public OperatorPageResponse pageFallBack(OperatorPageRequest request, BindingResult result, Throwable throwable) {
        OperatorPageResponse response = new OperatorPageResponse();
        BeanUtils.copyPropertiesIgnorException(fallBackResponse(throwable), response);
        return response;
    }

    @ApiOperation(value = "列表查询运营人员")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public OperatorListResponse list(@Valid @ModelAttribute BmBaseRequest request, BindingResult result) {
        return operatorService.list(request);
    }

    @SuppressWarnings("unused")
    public OperatorListResponse listFallBack(BmBaseRequest request, BindingResult result, Throwable throwable) {
        OperatorListResponse response = new OperatorListResponse();
        BeanUtils.copyPropertiesIgnorException(fallBackResponse(throwable), response);
        return response;
    }

    private BaseResponse fallBackResponse(Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
        BaseResponse response = new BaseResponse();
        response.setReturnSuccess(false);
        response.setReturnErrMsg(ErrEnum.SERVICE_UNAVAILABLE.getMsg());
        response.setReturnErrCode(ErrEnum.SERVICE_UNAVAILABLE.getCode());
        return response;
    }
}
