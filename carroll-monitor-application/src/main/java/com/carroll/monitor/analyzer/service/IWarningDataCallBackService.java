package com.carroll.monitor.analyzer.service;

import com.carroll.monitor.analyzer.dto.WarnDataCallBackDto;
import com.carroll.monitor.analyzer.model.WarningDataCallBack;
import com.carroll.monitor.analyzer.request.WarnDataCallBackRequest;
import com.carroll.monitor.analyzer.response.WarnDataCallBackResponse;

/**
 * 监控数据回调管理
 * @author: carroll
 * @date 2019/9/9
 */
public interface IWarningDataCallBackService {


    /**
     * 新增监控数据回调
     *
     * @param request
     * @return
     */
    WarnDataCallBackResponse save(WarnDataCallBackRequest request);

    WarningDataCallBack findByProjectTagAndEnable(String tag, boolean enable);

    WarnDataCallBackDto detail(String projectId);

}
