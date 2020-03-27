package com.carroll.monitor.analyzer.job;

import com.carroll.monitor.analyzer.service.IWarningDataService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 清除僵尸数据定时任务
 * @author: carroll
 * @date 2019/9/9
 */

@Slf4j
@Component
@JobHandler(value="clearUselessDataJob")
public class ClearUselessDataJob extends IJobHandler {

    @Autowired
    private IWarningDataService warningDataService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        XxlJobLogger.log("----  clearUselessDataJob begin  ----");
        warningDataService.clearUselessData();
        XxlJobLogger.log("----  clearUselessDataJob end  ----");
        return SUCCESS;
    }
}
