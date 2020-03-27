package com.carroll.monitor.analyzer.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 监控项统计记录
 *
 * @author: carroll
 * @date 2019/10/16
 */
@Getter
@Setter
public class ItemSummaryRecord extends BaseModel {

    /**
     * 监控项ID
     */
    private String itemId;
    /**
     * 统计日期
     */
    private Date date;
    /**
     * 总数
     */
    private int totalCount;
    /**
     * 成功次数
     */
    private int successCount;

    /**
     * 成功率
     */
    private double successPer = 0;

    /**
     * 失败次数
     */
    private int failCount;

    /**
     * 失败率
     */
    private double failPer = 0;

    /**
     * 超时次数
     */
    private int timeoutCount;

    /**
     * 超时率
     */
    private double timeoutPer = 0;

    /**
     * 成功调用总用时
     */
    private long successUseTimeMs;
    /**
     * 成功调用最大耗时
     */
    private long maxSuccessUseTimeMs;

    /**
     * 失败功调用总用时
     */
    private long failUseTimeMs;
    /**
     * 失败功调用最大耗时
     */
    private long maxFailUseTimeMs;

    public void succeed(Long useTimeMs) {
        totalCount += 1;
        successCount += 1;
        if (useTimeMs != null && useTimeMs > maxSuccessUseTimeMs) {
            maxSuccessUseTimeMs = useTimeMs;
            successUseTimeMs += useTimeMs;
        }
        successPer = successCount * 1.0 / totalCount;
    }

    public void failed(Long useTimeMs) {
        totalCount += 1;
        failCount += 1;
        if (useTimeMs != null && useTimeMs > maxSuccessUseTimeMs) {
            maxFailUseTimeMs = useTimeMs;
            failUseTimeMs += useTimeMs;
        }
        failPer = failCount * 1.0 / totalCount;
    }

    public void timeouted() {
        timeoutCount += 1;
        if (totalCount > 0) {
            timeoutPer = timeoutCount * 1.0 / totalCount;
        }
    }

    public Double getValue(String type){
        switch (type){
            case "FAIL_PER":
                return this.failPer;
            case "TIMEOUT_PER":
                return  this.timeoutPer;
            case "SUCCESS_PER":
                return this.successPer;
                default:
                    return this.failPer;

        }
    }

}
