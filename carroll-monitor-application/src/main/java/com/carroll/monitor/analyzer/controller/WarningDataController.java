package com.carroll.monitor.analyzer.controller;

import com.carroll.monitor.analyzer.enums.ErrEnum;
import com.carroll.monitor.analyzer.request.CurrentMonitorRequest;
import com.carroll.monitor.analyzer.request.HistoryMonitorPageRequest;
import com.carroll.monitor.analyzer.request.IdRequest;
import com.carroll.monitor.analyzer.request.MonitorStatisticRequest;
import com.carroll.monitor.analyzer.response.CurrentMonitorResponse;
import com.carroll.monitor.analyzer.response.HistoryMonitorPageResponse;
import com.carroll.monitor.analyzer.response.MonitorDetailResponse;
import com.carroll.monitor.analyzer.response.MonitorStatisticResponse;
import com.carroll.monitor.analyzer.service.IWarningDataService;
import com.carroll.spring.rest.starter.BaseController;
import com.carroll.spring.rest.starter.BaseResponse;
import com.carroll.utils.BeanUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Slf4j
@Api(value = "WarningDataController", description = "告警查询")
@RestController
@RequestMapping("/warningData")
public class WarningDataController extends BaseController {

    @Autowired
    private IWarningDataService warningDataService;

    @ApiOperation(value = "查询历史告警")
    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public HistoryMonitorPageResponse historyMonitor(@Valid @ModelAttribute HistoryMonitorPageRequest request, BindingResult result) {
        return warningDataService.historyMonitor(request);
    }

    @SuppressWarnings("unused")
    public HistoryMonitorPageResponse historyMonitorFallBack(HistoryMonitorPageRequest request, BindingResult result, Throwable throwable) {
        HistoryMonitorPageResponse response = new HistoryMonitorPageResponse();
        BeanUtils.copyPropertiesIgnorException(fallBackResponse(throwable), response);
        return response;
    }

    @ApiOperation(value = "分页查询实时告警")
    @RequestMapping(value = "/current", method = RequestMethod.GET)
    public CurrentMonitorResponse currentMonitor(@Valid @ModelAttribute CurrentMonitorRequest request, BindingResult result) {
        return warningDataService.currentMonitor(request);
    }

    @SuppressWarnings("unused")
    public CurrentMonitorResponse currentMonitorFallBack(CurrentMonitorRequest request, BindingResult result, Throwable throwable) {
        CurrentMonitorResponse response = new CurrentMonitorResponse();
        BeanUtils.copyPropertiesIgnorException(fallBackResponse(throwable), response);
        return response;
    }

    @ApiOperation(value = "查询告警详情")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public MonitorDetailResponse historyMonitorDetail(@Valid @ModelAttribute IdRequest request, BindingResult result) {
        return warningDataService.monitorDetail(request);
    }

    @SuppressWarnings("unused")
    public MonitorDetailResponse historyMonitorDetailFallBack(IdRequest request, BindingResult result, Throwable throwable) {
        MonitorDetailResponse response = new MonitorDetailResponse();
        BeanUtils.copyPropertiesIgnorException(fallBackResponse(throwable), response);
        return response;
    }

    @ApiOperation(value = "告警统计")
    @RequestMapping(value = "/statistic", method = RequestMethod.GET)
    public MonitorStatisticResponse statistic(@Valid @ModelAttribute MonitorStatisticRequest request, BindingResult result) {
        return warningDataService.statistic(request);
    }

    @SuppressWarnings("unused")
    public MonitorStatisticResponse statisticFallBack(MonitorStatisticRequest request, BindingResult result, Throwable throwable) {
        MonitorStatisticResponse response = new MonitorStatisticResponse();
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
