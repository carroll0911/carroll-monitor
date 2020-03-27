package com.carroll.monitor.analyzer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 短信平台配置信息
 * @author: carroll
 * @date 2019/9/9
 */
@Component
@ConfigurationProperties(prefix="monitor.sms")
@Data
public class SmsServiceConfig {
    private String syscode;
    private String password;
    private String code;
    private String uri;
}
