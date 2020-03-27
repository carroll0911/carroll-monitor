package com.carroll.monitor.analyzer.service;


import com.carroll.monitor.analyzer.dto.ProjectListDto;
import com.carroll.monitor.analyzer.model.Project;
import com.carroll.monitor.analyzer.request.ProjectRequest;
import com.carroll.monitor.analyzer.request.ProjectUpdateRequest;
import com.carroll.monitor.analyzer.response.ProjectDetailResponse;
import com.carroll.monitor.analyzer.response.ProjectResponse;

import java.util.List;

/**
 * 产品项目管理
 * @author: carroll
 * @date 2019/9/9
 */
public interface IProjectService {

    /**
     * 根据Tag获取项目信息
     * @param tag
     * @return
     */
    Project getByTag(String tag);

    /**
     * 根据id获取项目信息
     * @param id
     * @return
     */
    Project getByID(String id);

    /**
     * 将project数据写入redis
     */
    void refreshProject();

    /**
     * 新增project
     * @param request
     * @return
     */
    ProjectResponse save(ProjectRequest request);

    /**
     * 修改project
     * @param request
     * @return
     */
    ProjectResponse update(ProjectUpdateRequest request);

    /**
     * 获取所有项目
     * @return
     */
    List<Project> findAll();

    /**
     * 项目列表
     * @return
     */
    List<ProjectListDto> list();

    /**
     * 获取项目详情
     * @param projectId
     * @return
     */
    ProjectDetailResponse detail(String projectId);

    /**
     * 重置密码
     */
    void resetPassword(String projectId);

    /**
     * 修改状态
     * @param projectId
     * @param status
     */
    void changeStatus(String projectId, Project.Status status);
}
