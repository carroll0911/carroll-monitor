package com.carroll.monitor.analyzer.service;


import com.carroll.monitor.analyzer.model.WarningData;
import com.carroll.monitor.analyzer.request.CurrentMonitorRequest;
import com.carroll.monitor.analyzer.request.HistoryMonitorPageRequest;
import com.carroll.monitor.analyzer.request.IdRequest;
import com.carroll.monitor.analyzer.request.MonitorStatisticRequest;
import com.carroll.monitor.analyzer.response.*;

/**
 * 告警数据接口
 * @author: carroll
 * @date 2019/9/9
 */
public interface IWarningDataService {

    /**
     * 获取已存在的未清除的告警数据
     *
     * @param itemId
     * @return
     */
    WarningData getCurrentData(String itemId, String applicationName, String host, String target);

    /**
     * 保存告警数据
     *
     * @param warningData
     * @return
     */
    WarningData save(WarningData warningData);


    /**
     * 定时清除僵尸数据
     */
    void clearUselessData();

    /**
     * 定时重发告警数据
     */
    void resendData();

    /**
     * 查询历史告警
     *
     * @param request
     * @return
     */
    HistoryMonitorPageResponse historyMonitor(HistoryMonitorPageRequest request);

    /**
     * 查询实时告警
     *
     * @param request
     * @return
     */
    CurrentMonitorResponse currentMonitor(CurrentMonitorRequest request);

    /**
     * 查询告警详情
     *
     * @param request
     * @return
     */
    MonitorDetailResponse monitorDetail(IdRequest request);

    /**
     * 统计查询
     *
     * @param request
     * @return
     */
    MonitorStatisticResponse statistic(MonitorStatisticRequest request);

    /**
     * 查询 调用链信息
     * @param traceId
     * @return
     */
    TraceDetailResponse getTraceDetail(String traceId);
}
