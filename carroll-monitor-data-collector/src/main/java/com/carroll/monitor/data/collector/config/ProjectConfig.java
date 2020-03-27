package com.carroll.monitor.data.collector.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 项目配置
 * @author: carroll
 * @date 2019/9/9
 */
@Data
@Component
@ConfigurationProperties(prefix="monitor.project")
public class ProjectConfig {

    private String tag;
    private String password;
    private String applicationName;
}
