package com.carroll.monitor.analyzer.config;

import com.carroll.monitor.analyzer.service.IMonitorItemService;
import com.carroll.monitor.analyzer.service.IProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Component
@Slf4j
public class RefreshApplicationListener implements ApplicationRunner {

    @Autowired
    private IProjectService projectService;
    @Autowired
    private IMonitorItemService monitorItemService;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        log.info("......refresh project share data begin......");
        projectService.refreshProject();
        monitorItemService.initSystemMonitorItem();
        log.info("......refresh project share data finished......");
    }
}
