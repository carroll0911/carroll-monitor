package com.carroll.monitor.analyzer.controller;

import com.carroll.monitor.analyzer.dto.WarnDataCallBackDto;
import com.carroll.monitor.analyzer.enums.ErrEnum;
import com.carroll.monitor.analyzer.request.WarnDataCallBackRequest;
import com.carroll.monitor.analyzer.response.WarnDataCallBackResponse;
import com.carroll.monitor.analyzer.response.WarnDataCallbacktDetailResponse;
import com.carroll.monitor.analyzer.service.IWarningDataCallBackService;
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
@Api(value = "WarningDataCallBackController", description = "监控数据回调")
@RestController
@RequestMapping("/warningDataCallBack")
public class WarningDataCallBackController extends BaseController {

    @Autowired
    private IWarningDataCallBackService warningDataService;

    @ApiOperation(value = "设置监控数据推送", notes = "相同tag覆盖")
    @RequestMapping(method = RequestMethod.POST)
    public WarnDataCallBackResponse save(@Valid @RequestBody WarnDataCallBackRequest request, BindingResult result) {
        return warningDataService.save(request);
    }

    @ApiOperation(value = "获取监控数据推送")
    @RequestMapping(method = RequestMethod.GET)
    public WarnDataCallbacktDetailResponse detail(@RequestParam("projectId") String projectId) {
        WarnDataCallBackDto dto = warningDataService.detail(projectId);
        WarnDataCallbacktDetailResponse response = new WarnDataCallbacktDetailResponse();
        response.setData(dto);
        return response;
    }

    @SuppressWarnings("unused")
    public WarnDataCallBackResponse saveFallBack(WarnDataCallBackRequest request, BindingResult result, Throwable throwable) {
        WarnDataCallBackResponse response = new WarnDataCallBackResponse();
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
