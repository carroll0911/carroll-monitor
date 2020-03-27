package com.carroll.monitor.analyzer.service;


import com.carroll.monitor.analyzer.dto.MonitorDataEx;
import com.carroll.monitor.analyzer.model.MonitorItem;
import com.carroll.monitor.analyzer.request.*;
import com.carroll.monitor.analyzer.response.MonitorItemListResponse;
import com.carroll.monitor.analyzer.response.MonitorItemPageResponse;
import com.carroll.monitor.analyzer.response.MonitorItemResponse;
import com.carroll.spring.rest.starter.BaseResponse;

/**
 * 监控项管理
 * @author: carroll
 * @date 2019/9/9
 */
public interface IMonitorItemService {

    /**
     * 根据监控项tag 获取监控项
     *
     * @param tag
     * @return
     */
    MonitorItem getByTag(String tag, String projectId);

    MonitorItem getById(String id);

    MonitorItem save(MonitorItem item);

    MonitorItemListResponse list(BmBaseRequest request);

    MonitorItemResponse detail(IdRequest request);

    MonitorItemPageResponse page(MonitorItemPageRequest request);

    MonitorItemResponse update(MonitorItemUpdateRequest request);

    BaseResponse notify(MonitorItemNotifyRequest request);

    MonitorItemResponse save(MonitorItemRequest request);


    /**
     * 初始化系统tag
     */
    void initSystemMonitorItem();

    /**
     * 判断是否监控数据表示的服务状态
     * @param monitorItem
     * @return
     */
    boolean isSuccess(MonitorItem monitorItem, MonitorDataEx data);

    /**
     * 修改状态
     * @param id
     * @param status
     */
    void changeStatus(String id, MonitorItem.Status status);

    /**
     * 删除监控项
     * @param id
     */
    void delete(String id);
}
