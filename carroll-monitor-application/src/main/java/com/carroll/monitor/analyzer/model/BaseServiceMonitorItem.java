package com.carroll.monitor.analyzer.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 基础服务监控项
 * @author: carroll
 * @date 2019/9/9
 */
@Getter
@Setter
public class BaseServiceMonitorItem extends BaseModel {

    /**
     * tag
     */
    private String tag;

    /**
     * 所属项目ID
     */
    private String projectId;

    /**
     * 告警值
     */
    private Double warnLine;

    /**
     * 主机名/IP
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;


    private String uri;

    /**
     * 认证
     * mq  userName:password base64
     */
    private String authorization;
}
