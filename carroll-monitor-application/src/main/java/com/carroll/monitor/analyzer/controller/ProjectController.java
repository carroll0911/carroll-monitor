package com.carroll.monitor.analyzer.controller;

import com.carroll.monitor.analyzer.dto.ProjectListDto;
import com.carroll.monitor.analyzer.dto.UserCacheDto;
import com.carroll.monitor.analyzer.enums.ErrEnum;
import com.carroll.monitor.analyzer.enums.Role;
import com.carroll.monitor.analyzer.request.ProjectRequest;
import com.carroll.monitor.analyzer.request.ProjectUpdateRequest;
import com.carroll.monitor.analyzer.response.ProjectListResponse;
import com.carroll.monitor.analyzer.response.ProjectResponse;
import com.carroll.monitor.analyzer.service.IProjectService;
import com.carroll.monitor.analyzer.utils.BizContext;
import com.carroll.spring.rest.starter.BaseController;
import com.carroll.spring.rest.starter.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Slf4j
@Api(value = "ProjectController", description = "运营项目管理")
@RestController
@RequestMapping("/project")
public class ProjectController extends BaseController {

    @Autowired
    private IProjectService projectService;

    @ApiOperation(value = "手动将project数据刷入共享数据,仅供内部使用")
    @RequestMapping(value = "refresh", method = RequestMethod.GET)
    public BaseResponse refresh() {
        projectService.refreshProject();
        return new BaseResponse();
    }

    @ApiOperation(value = "新增项目,仅供内部使用")
    @RequestMapping(method = RequestMethod.POST)
    public ProjectResponse save(@Valid @RequestBody ProjectRequest request, BindingResult result) {
        return projectService.save(request);
    }

    @ApiOperation(value = "修改项目,仅供内部使用")
    @RequestMapping(method = RequestMethod.PUT)
    public ProjectResponse update(@Valid @RequestBody ProjectUpdateRequest request, BindingResult result) {
        return projectService.update(request);
    }

    @ApiOperation(value = "查询所有项目")
    @RequestMapping(value = "all", method = RequestMethod.GET)
    public ProjectListResponse list(@RequestParam(value = "status", required = false) String status) {
        ProjectListResponse response = new ProjectListResponse();
        response.setList(new ArrayList<>());
        UserCacheDto userCacheDto = (UserCacheDto) BizContext.getData(BizContext.MONITOR_USER_CACHE);
        if (userCacheDto == null || (CollectionUtils.isEmpty(userCacheDto.getProjects()) && !Role.SUPPER.equals(userCacheDto.getRole()))) {
            return response;
        }
        List<ProjectListDto> projects = projectService.list();
        if (Role.SUPPER.equals(userCacheDto.getRole())) {
            response.getList().addAll(projects);
        } else {
            for (ProjectListDto project : projects) {
                for (String pid : userCacheDto.getProjects().keySet()) {
                    if (project.getId().equals(pid)) {
                        response.getList().add(project);
                    }
                }
            }
        }
        if(!StringUtils.isEmpty(status)){
            response.setList(response.getList().stream().filter(p->p.getStatus().name().equals(status)).collect(Collectors.toList()));
        }
        return response;
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
