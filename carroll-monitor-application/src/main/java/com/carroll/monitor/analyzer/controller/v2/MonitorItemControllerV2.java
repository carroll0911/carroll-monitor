package com.carroll.monitor.analyzer.controller.v2;

import com.carroll.monitor.analyzer.dto.ConstantDto;
import com.carroll.monitor.analyzer.enums.ErrEnum;
import com.carroll.monitor.analyzer.model.MonitorItem;
import com.carroll.monitor.analyzer.request.*;
import com.carroll.monitor.analyzer.response.MonitorItemConstantResponse;
import com.carroll.monitor.analyzer.response.MonitorItemListResponse;
import com.carroll.monitor.analyzer.response.MonitorItemPageResponse;
import com.carroll.monitor.analyzer.response.MonitorItemResponse;
import com.carroll.monitor.analyzer.service.IMonitorItemService;
import com.carroll.spring.rest.starter.BaseController;
import com.carroll.spring.rest.starter.BaseException;
import com.carroll.spring.rest.starter.BaseResponse;
import com.carroll.utils.BeanUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Slf4j
@Api(value = "MonitorItemController", description = "告警内容管理")
@RestController
@RequestMapping("/v2/item")
public class MonitorItemControllerV2 extends BaseController {

    @Autowired
    private IMonitorItemService monitorItemService;

    @ApiOperation(value = "查询告警内容id和name")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public MonitorItemListResponse list(@Valid @ModelAttribute BmBaseRequest request, BindingResult result) {
        return monitorItemService.list(request);
    }

    @SuppressWarnings("unused")
    public MonitorItemListResponse listFallBack(BmBaseRequest request, BindingResult result, Throwable throwable) {
        MonitorItemListResponse response = new MonitorItemListResponse();
        BeanUtils.copyPropertiesIgnorException(fallBackResponse(throwable), response);
        return response;
    }

    @ApiOperation(value = "查询告警内容详情")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public MonitorItemResponse detail(@Valid @ModelAttribute IdRequest request, BindingResult result) {
        return monitorItemService.detail(request);
    }

    @SuppressWarnings("unused")
    public MonitorItemResponse detailFallBack(IdRequest request, BindingResult result, Throwable throwable) {
        MonitorItemResponse response = new MonitorItemResponse();
        BeanUtils.copyPropertiesIgnorException(fallBackResponse(throwable), response);
        return response;
    }

    @ApiOperation(value = "分页查询告警内容")
    @RequestMapping(method = RequestMethod.GET)
    public MonitorItemPageResponse page(@Valid @ModelAttribute MonitorItemPageRequest request, BindingResult result) {
        return monitorItemService.page(request);
    }

    @SuppressWarnings("unused")
    public MonitorItemPageResponse pageFallBack(MonitorItemPageRequest request, BindingResult result, Throwable throwable) {
        MonitorItemPageResponse response = new MonitorItemPageResponse();
        BeanUtils.copyPropertiesIgnorException(fallBackResponse(throwable), response);
        return response;
    }

    @ApiOperation(value = "编辑告警内容")
    @RequestMapping(method = RequestMethod.PUT)
    public MonitorItemResponse update(@Valid @RequestBody MonitorItemUpdateRequest request, BindingResult result) {
        return monitorItemService.update(request);
    }

    @SuppressWarnings("unused")
    public MonitorItemResponse updateFallBack(MonitorItemUpdateRequest request, BindingResult result, Throwable throwable) {
        MonitorItemResponse response = new MonitorItemResponse();
        BeanUtils.copyPropertiesIgnorException(fallBackResponse(throwable), response);
        return response;
    }

    @ApiOperation(value = "通知设置")
    @RequestMapping(value = "/notify", method = RequestMethod.PUT)
    public BaseResponse notify(@Valid @RequestBody MonitorItemNotifyRequest request, BindingResult result) {
        return monitorItemService.notify(request);
    }

    @SuppressWarnings("unused")
    public BaseResponse notifyFallBack(MonitorItemNotifyRequest request, BindingResult result, Throwable throwable) {
        return fallBackResponse(throwable);
    }

    @ApiOperation(value = "获取告警级别")
    @RequestMapping(value = "/level", method = RequestMethod.GET)
    public MonitorItemConstantResponse loadLevel() {
        MonitorItemConstantResponse response = new MonitorItemConstantResponse();
        List<ConstantDto> list = new ArrayList<>();
        for (MonitorItem.Level level : MonitorItem.Level.values()) {
            ConstantDto dto = new ConstantDto();
            dto.setCode(level.name());
            dto.setDesc(level.getDesc());
            list.add(dto);
        }
        response.setList(list);
        return response;
    }

    @ApiOperation(value = "获取告警类别")
    @RequestMapping(value = "/category", method = RequestMethod.GET)
    public MonitorItemConstantResponse loadCategory() {
        MonitorItemConstantResponse response = new MonitorItemConstantResponse();
        List<ConstantDto> list = new ArrayList<>();
        for (MonitorItem.Category category : MonitorItem.Category.values()) {
            ConstantDto dto = new ConstantDto();
            dto.setCode(category.name());
            dto.setDesc(category.getDesc());
            list.add(dto);
        }
        response.setList(list);
        return response;
    }

    @ApiOperation(value = "获取告警通知类型")
    @RequestMapping(value = "/message", method = RequestMethod.GET)
    public MonitorItemConstantResponse loadMessage() {
        MonitorItemConstantResponse response = new MonitorItemConstantResponse();
        List<ConstantDto> list = new ArrayList<>();
        for (MonitorItem.MessageType msg : MonitorItem.MessageType.values()) {
            ConstantDto dto = new ConstantDto();
            dto.setCode(msg.name());
            dto.setDesc(msg.getDesc());
            list.add(dto);
        }
        response.setList(list);
        return response;
    }

    @ApiOperation(value = "新增告警内容,仅供内部使用")
    @RequestMapping(method = RequestMethod.POST)
    public MonitorItemResponse save(@Valid @RequestBody MonitorItemSaveRequest request, BindingResult result) {
        MonitorItemRequest itemRequest = new MonitorItemRequest();
        itemRequest.setCategory(request.getCategory());
        itemRequest.setLevel(request.getLevel());
        itemRequest.setProjectId(request.getProjectId());
        itemRequest.setName(request.getName());
        itemRequest.setTag(request.getTag());
        itemRequest.setDescription(request.getDescription());
        itemRequest.setSuggest(request.getSuggest());
        itemRequest.setResultScript(request.getResultScript());
        return monitorItemService.save(itemRequest);
    }

    @ApiOperation(value = "启用")
    @RequestMapping(value = "enable", method = RequestMethod.PUT)
    public BaseResponse enable(@Valid @RequestBody IdRequest request) {
        monitorItemService.changeStatus(request.getId(), MonitorItem.Status.ENABLED);
        return new BaseResponse();
    }

    @ApiOperation(value = "禁用")
    @RequestMapping(value = "disable", method = RequestMethod.PUT)
    public BaseResponse disable(@Valid @RequestBody IdRequest request) {
        monitorItemService.changeStatus(request.getId(), MonitorItem.Status.DISABLED);
        return new BaseResponse();
    }

    @ApiOperation(value = "删除")
    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    public BaseResponse delete(@Valid @ModelAttribute IdRequest request) {
        monitorItemService.delete(request.getId());
        return new BaseResponse();
    }

    @SuppressWarnings("unused")
    public MonitorItemConstantResponse loadConstantFallBack(Throwable throwable) {
        MonitorItemConstantResponse response = new MonitorItemConstantResponse();
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
