package com.carroll.monitor.analyzer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author: carroll
 * @date 2019/9/9
 **/
@SpringBootApplication
@EnableSwagger2
@ServletComponentScan
@EnableMongoRepositories(basePackages = {"com.carroll.monitor.analyzer.repository"},
        repositoryFactoryBeanClass = MongoRepositoryFactoryBean.class)
@ComponentScan(basePackages = "com.carroll")
@Slf4j
@EnableMongoAuditing
public class AnalyzerApplication {

    @Value("${restTemplateConnTimeout}")
    private int restTemplateConnTimeout;

    public static void main(String[] args) {
//        if (args == null || args.length == 0) {
//            args = new String[3];
//            args[0] = "--config.label=test";
//            args[1] = "--config.profile=test";
//            args[2] = "--config.discovery.enable=false";
//        }
        new SpringApplicationBuilder(AnalyzerApplication.class).web(true).run(args);

    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate template = new RestTemplate();
        SimpleClientHttpRequestFactory factory = (SimpleClientHttpRequestFactory) template.getRequestFactory();
        factory.setConnectTimeout(restTemplateConnTimeout);
        factory.setReadTimeout(restTemplateConnTimeout);
        return template;
    }

}
