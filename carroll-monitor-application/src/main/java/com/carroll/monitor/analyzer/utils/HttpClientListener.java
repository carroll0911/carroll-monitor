package com.carroll.monitor.analyzer.utils;/**
 * Created by core_ on 2018/7/2.
 */

import com.alibaba.fastjson.JSON;
import okhttp3.Request;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 调用第三方接口监听接口
 *
 * @author: carroll
 * @date 2018/7/2
 */

public interface HttpClientListener {

    Logger log = LoggerFactory.getLogger(HttpClientListener.class);

    default void execute(HttpRequestBase httpRequest, String result) {
        if (httpRequest instanceof HttpPost) {
            log.debug("url:{},params:{},headers:{},result:{}", httpRequest.getURI(),
                    JSON.toJSONString(((HttpPost) httpRequest).getEntity()),
                    JSON.toJSONString(httpRequest.getAllHeaders()), result);
        } else {
            log.debug("url:{},headers:{},result:{}", httpRequest.getURI(),
                    JSON.toJSONString(httpRequest.getAllHeaders()), result);
        }

    }

    default void execute(Request request, String result) {
        log.debug("url:{},method:{},body:{},headers:{},response:{}", request.url(),
                request.method(),
                JSON.toJSONString(request.body()),
                JSON.toJSONString(request.headers()), result);

    }

    default HttpRequestBase pre(HttpRequestBase httpRequest) {
        return httpRequest;
    }

    default Request pre(Request request) {
        return request;
    }
}
