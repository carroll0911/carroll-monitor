package com.carroll.monitor.analyzer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Configuration
public class WSConfig {
    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.carroll.monitor.analyzer.ws");
        return marshaller;
    }
}
