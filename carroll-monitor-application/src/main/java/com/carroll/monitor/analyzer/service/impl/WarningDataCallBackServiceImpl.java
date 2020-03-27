package com.carroll.monitor.analyzer.service.impl;

import com.carroll.monitor.analyzer.dto.WarnDataCallBackDto;
import com.carroll.monitor.analyzer.dto.WarningDataCallBackDto;
import com.carroll.monitor.analyzer.enums.ErrEnum;
import com.carroll.monitor.analyzer.model.Project;
import com.carroll.monitor.analyzer.model.WarningDataCallBack;
import com.carroll.monitor.analyzer.repository.ProjectRepository;
import com.carroll.monitor.analyzer.repository.WarnDataCallBackRepository;
import com.carroll.monitor.analyzer.request.WarnDataCallBackRequest;
import com.carroll.monitor.analyzer.response.WarnDataCallBackResponse;
import com.carroll.monitor.analyzer.service.IWarningDataCallBackService;
import com.carroll.spring.rest.starter.BaseException;
import com.carroll.utils.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Slf4j
@Service
public class WarningDataCallBackServiceImpl implements IWarningDataCallBackService {

    @Autowired
    private WarnDataCallBackRepository wdcbRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public WarnDataCallBackResponse save(WarnDataCallBackRequest request) {
        Project project = projectRepository.findOne(request.getProjectId());
        if (project == null) {
            throw new BaseException(ErrEnum.PROJECT_NOT_EXIST.getCode(), ErrEnum.PROJECT_NOT_EXIST.getMsg());
        }
        WarningDataCallBack wdc = wdcbRepository.findByProjectTag(project.getTag());
        if (wdc == null) {
            wdc = new WarningDataCallBack();
        }
        BeanUtils.copyPropertiesIgnorException(request, wdc);
        wdc = wdcbRepository.save(wdc);
        WarningDataCallBackDto dto = new WarningDataCallBackDto();
        BeanUtils.copyPropertiesIgnorException(wdc, dto);
        WarnDataCallBackResponse response = new WarnDataCallBackResponse();
        response.setData(dto);
        return response;
    }

    @Override
    public WarningDataCallBack findByProjectTagAndEnable(String tag, boolean enable) {
        return wdcbRepository.findByProjectTagAndEnable(tag, enable);
    }

    @Override
    public WarnDataCallBackDto detail(String projectId) {
        Project project = projectRepository.findOne(projectId);
        if (project == null) {
            throw new BaseException(ErrEnum.PROJECT_NOT_EXIST.getCode(), ErrEnum.PROJECT_NOT_EXIST.getMsg());
        }
        WarnDataCallBackDto dto = new WarnDataCallBackDto();
        dto.setProjectId(projectId);
        dto.setProjectName(project.getName());
        dto.setProjectTag(project.getTag());
        WarningDataCallBack wdc = wdcbRepository.findByProjectTag(project.getTag());
        if (wdc != null) {
            dto.setEnable(wdc.isEnable());
            dto.setCallback(wdc.getCallback());
        }
        return dto;
    }
}
