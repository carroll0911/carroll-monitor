package com.carroll.monitor.data.collector.component;

import com.alibaba.fastjson.JSON;
import com.carroll.monitor.common.dto.KafkaTopic;
import com.carroll.monitor.common.dto.MonitorData;
import com.carroll.monitor.common.utils.PasswordUtils;
import com.carroll.monitor.data.collector.config.ProjectConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 数据发送工具
 * @author: carroll
 * @date 2019/9/9
 */
@Component
@Slf4j
public class DataSender {


    @Resource(name = "monitorKafkaTemplate")
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private ProjectConfig projectConfig;

    @Autowired
    private Environment env;

    private static final String APPLICATION_NAME_KEY = "spring.application.name";

    private Class log4jMdc;
    private Class slf4jMdc;
    private Method log4jMdcGet;
    private Method slf4jMdcGet;

    @SuppressWarnings("unused")
    @PostConstruct
    private void init() {
        try {
            log4jMdc = Class.forName("org.apache.log4j.MDC");
            log4jMdcGet = slf4jMdc.getMethod("get", String.class);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        try {
            slf4jMdc = Class.forName("org.slf4j.MDC");
            slf4jMdcGet = slf4jMdc.getMethod("get", String.class);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    /**
     * 发送监控数据
     *
     * @param tag    监控项tag
     * @param result 结果
     */
    public void send(String tag, Boolean result) {
        this.send(tag, result, null, null);

    }

    /**
     * 发送监控数据
     *
     * @param tag      监控项tag
     * @param result   结果
     * @param params   参数
     * @param response 监控点调用的返回值
     */
    public void send(String tag, Boolean result, Object[] params, Object response) {
        send(tag, result, params, response, null);
    }

    /**
     * 发送监控数据
     *
     * @param tag      监控项tag
     * @param result   结果
     * @param params   参数
     * @param response 返回结果
     * @param target   告警对象
     */
    public void send(String tag, Boolean result, Object[] params, Object response, String target) {
        send(tag, result, params, response, target, false);
    }

    /**
     * 发送监控数据
     *
     * @param tag       监控项tag
     * @param result    结果
     * @param params    参数
     * @param response  监控点调用的返回值
     * @param target    监控对象
     * @param withFlag  禁止相同tag重复发送
     * @param useTimeMs 方法执行所用时间
     * @param timeoutMs 监控超时时间,方法执行时间超出timeoutMs 触发报警
     */
    public void send(String tag, Boolean result, Object[] params, Object response, String target, boolean withFlag, Long useTimeMs, Long timeoutMs, String joinpoint) {
        send(tag, result, params, response, target, withFlag, useTimeMs, timeoutMs, joinpoint, getTraceId());
    }

    /**
     * 发送监控数据
     *
     * @param tag       监控项tag
     * @param result    结果
     * @param params    参数
     * @param response  监控点调用的返回值
     * @param target    监控对象
     * @param withFlag  禁止相同tag重复发送
     * @param useTimeMs 方法执行所用时间
     * @param timeoutMs 监控超时时间,方法执行时间超出timeoutMs 触发报警
     * @param joinpoint 被监控的方法
     * @param traceId   调用链ID
     */
    public void send(String tag, Boolean result, Object[] params, Object response, String target, boolean withFlag, Long useTimeMs, Long timeoutMs, String joinpoint, String traceId) {
        if (kafkaTemplate == null) {
            log.debug("not config monitor kafka");
            return;
        }
        if (result == null || (SendFlagHolder.sended(tag) && withFlag)) {
            log.debug("not need send monitor data:{}---{}---{}---{}", result, SendFlagHolder.sended(tag), SendFlagHolder.count(tag), withFlag);
            return;
        }
        try {
            MonitorData data = new MonitorData();
            data.setProjectTag(projectConfig.getTag());
            data.setPassword(PasswordUtils.encodePassword(projectConfig.getTag(), projectConfig.getPassword()));
            data.setResult(result);
            data.setTag(tag);
            data.setTime(System.currentTimeMillis());
            data.setTimeoutMs(timeoutMs);
            data.setUseTimeMs(useTimeMs);
            String applicationName = projectConfig.getApplicationName();
            if (StringUtils.isEmpty(applicationName)) {
                applicationName = env.getProperty(APPLICATION_NAME_KEY);
            }
            if (StringUtils.isEmpty(applicationName)) {
                applicationName = projectConfig.getTag();
            }

            if (!StringUtils.isEmpty(target)) {
                data.setTarget(target);
            }
            data.setApplicationName(applicationName);
            data.setHost(getIp());
            data.setTraceId(traceId);

            if (!result) {
                if (params != null) {
                    data.setParams(JSON.toJSONString(filterParams(params)));
                }
                if (response != null) {
                    data.setResponse(JSON.toJSONString(response));
                }
            }

            String dataJson = JSON.toJSONString(data);
            log.debug("send monitor data:{}", dataJson);
            kafkaTemplate.send(KafkaTopic.MONITOR_DATA, dataJson);
            SendFlagHolder.setSended(tag);
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
        }

    }

    /**
     * 发送监控数据
     *
     * @param tag      监控项tag
     * @param result   结果
     * @param params   参数
     * @param response 监控点调用的返回值
     * @param target   监控对象
     * @param withFlag 禁止相同tag重复发送
     */
    public void send(String tag, Boolean result, Object[] params, Object response, String target, boolean withFlag) {
        this.send(tag, result, params, response, target, withFlag, null, null, null);
    }

    /**
     * 过滤掉 无用参数
     *
     * @param params
     * @return
     */
    private List<Object> filterParams(Object[] params) {
        List<Object> paramList = new ArrayList<>();
        for (Object obj : params) {
            if (null == obj) {
                continue;
            }
            log.debug("param class:{}", obj.getClass().getName());
            if (obj instanceof BindingResult) {
                continue;
            }
            paramList.add(obj);
        }
        return paramList;
    }

    /*
    获取IP地址
     */
    private String getIp() {
        String host = "UNKNOWN";
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress ip = (InetAddress) addresses.nextElement();
                    if (ip != null
                            && ip instanceof Inet4Address
                            && !ip.isLoopbackAddress() //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                            && ip.getHostAddress().indexOf(":") == -1) {
                        log.debug("本机的IP = {}", ip.getHostAddress());
                        host = ip.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            log.error("get server host Exception e:", e);
        }
        return host;
    }

    /**
     * 基于 slf4j/log4j 获取调用链ID
     *
     * @return
     */
    private String getTraceId() {
        String traceId = null;
        if (slf4jMdc != null && slf4jMdcGet != null) {
            try {
                traceId = (String) slf4jMdcGet.invoke(null, "TraceId");
            } catch (Exception e) {
                log.debug(e.getMessage());
            }
        }

        if (StringUtils.isEmpty(traceId)) {
            if (log4jMdc != null && log4jMdcGet != null) {
                try {
                    traceId = String.valueOf(log4jMdcGet.invoke(null, "TraceId"));
                } catch (Exception e) {
                    log.debug(e.getMessage());
                }
            }
        }
        return traceId;
    }

}
