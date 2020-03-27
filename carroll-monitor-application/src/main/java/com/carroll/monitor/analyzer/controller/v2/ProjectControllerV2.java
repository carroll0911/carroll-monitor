package com.carroll.monitor.analyzer.controller.v2;

import com.carroll.monitor.analyzer.enums.ErrEnum;
import com.carroll.monitor.analyzer.model.Project;
import com.carroll.monitor.analyzer.request.IdRequest;
import com.carroll.monitor.analyzer.request.ProjectRequest;
import com.carroll.monitor.analyzer.request.ProjectUpdateRequest;
import com.carroll.monitor.analyzer.response.ProjectDetailResponse;
import com.carroll.monitor.analyzer.response.ProjectListResponse;
import com.carroll.monitor.analyzer.response.ProjectResponse;
import com.carroll.monitor.analyzer.service.IProjectService;
import com.carroll.spring.rest.starter.BaseController;
import com.carroll.spring.rest.starter.BaseResponse;
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
@Api(value = "ProjectController", description = "运营项目管理")
@RestController
@RequestMapping("/v2/project")
public class ProjectControllerV2 extends BaseController {

    @Autowired
    private IProjectService projectService;

    @ApiOperation(value = "手动将project数据刷入共享数据,仅供内部使用")
    @RequestMapping(value = "refresh", method = RequestMethod.GET)
    public BaseResponse refresh() {
        projectService.refreshProject();
        return new BaseResponse();
    }

    @ApiOperation(value = "新增项目")
    @RequestMapping(method = RequestMethod.POST)
    public ProjectResponse save(@Valid @RequestBody ProjectRequest request, BindingResult result) {
        return projectService.save(request);
    }

    @ApiOperation(value = "修改项目")
    @RequestMapping(method = RequestMethod.PUT)
    public ProjectResponse update(@Valid @RequestBody ProjectUpdateRequest request, BindingResult result) {
        return projectService.update(request);
    }

    @ApiOperation(value = "查询所有项目")
    @RequestMapping(value = "all", method = RequestMethod.GET)
    public ProjectListResponse list() {
        ProjectListResponse response = new ProjectListResponse();
        response.setList(projectService.list());
        return response;
    }

    @ApiOperation(value = "查询告警内容详情")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public ProjectDetailResponse detail(@RequestParam(name = "id") String id) {
        return projectService.detail(id);
    }

    @ApiOperation(value = "重置密码")
    @RequestMapping(value = "reset-pwd", method = RequestMethod.PUT)
    public BaseResponse resetPwd(@Valid @RequestBody IdRequest request, BindingResult result) {
        projectService.resetPassword(request.getId());
        return new BaseResponse();
    }

    @ApiOperation(value = "禁用")
    @RequestMapping(value = "disable", method = RequestMethod.PUT)
    public BaseResponse disable(@Valid @RequestBody IdRequest request, BindingResult result) {
        projectService.changeStatus(request.getId(), Project.Status.DISABLED);
        return new BaseResponse();
    }

    @ApiOperation(value = "启用")
    @RequestMapping(value = "enable", method = RequestMethod.PUT)
    public BaseResponse enable(@Valid @RequestBody IdRequest request, BindingResult result) {
        projectService.changeStatus(request.getId(), Project.Status.ENABLED);
        return new BaseResponse();
    }

    @SuppressWarnings("unused")
    public BaseResponse refreshFallBack(Throwable throwable) {
        return fallBackResponse(throwable);
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
