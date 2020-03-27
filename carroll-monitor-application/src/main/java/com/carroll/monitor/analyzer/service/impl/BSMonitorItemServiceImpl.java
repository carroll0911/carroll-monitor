package com.carroll.monitor.analyzer.service.impl;

import com.carroll.monitor.analyzer.model.BaseServiceMonitorItem;
import com.carroll.monitor.analyzer.repository.BaseServiceMonitorItemRepository;
import com.carroll.monitor.analyzer.service.IBSMonitorItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Service
public class BSMonitorItemServiceImpl implements IBSMonitorItemService {

    @Autowired
    private BaseServiceMonitorItemRepository baseServiceMonitorItemRepository;

    @Override
    public List<BaseServiceMonitorItem> findAllByTagAndProjectId(String tag, String projectId) {
        return baseServiceMonitorItemRepository.findAllByTagAndProjectId(tag,projectId);
    }
}
