package com.carroll.monitor.analyzer.utils;

import com.carroll.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Slf4j
public class HttpUtils {


    @SuppressWarnings("unused")
    private static final String PKCS12 = "PKCS12";
    public static final String UTF8 = "UTF-8";
    @SuppressWarnings("unused")
    public static final String JSON = "application/json;charset=utf-8";
    @SuppressWarnings("unused")
    private static final String TLSv1 = "TLSv1";
    private static final int SOCKET_TIMEOUT = 10000;                                                          //连接超时时间，默认10秒
    private static final int CONNECT_TIMEOUT = 30000;                                                         //传输超时时间，默认30秒

    private static RequestConfig requestConfig;                                                               //请求器的配置

    static {
        //根据默认超时限制初始化requestConfig
        requestConfig = RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT).build();
    }

    private HttpUtils() {
    }

    public static String doPost(String url, String request, String contentType, int connectTimeOut, int socketTimeOut) {
        RequestConfig config = RequestConfig.custom().setSocketTimeout(socketTimeOut).setConnectTimeout(connectTimeOut).build();
        return post(url, request, contentType, config, null);
    }

    public static String doPost(String url, String request, String contentType) {
        return post(url, request, contentType, requestConfig, null);
    }

    public static String doPost(String url, String request, String contentType, Map<String, String> headers) {
        return post(url, request, contentType, requestConfig, headers);
    }

    private static String post(String url, String request, String contentType, RequestConfig config, Map<String, String> headers) {
        String result = null;

        //创建HttpClientBuilder
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        //HttpClient
        CloseableHttpClient httpClient = httpClientBuilder.build();

        HttpPost httpPost = new HttpPost(url);

        //得指明使用UTF-8编码，否则到API服务器XML的中文不能被成功识别
        StringEntity postEntity = new StringEntity(request, UTF8);
        httpPost.addHeader("Content-Type", contentType);
        if (headers != null) {
            headers.forEach((k, v) -> httpPost.addHeader(k, v));
        }
        httpPost.setEntity(postEntity);
        //设置请求器的配置
        httpPost.setConfig(config);
        try {
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, UTF8);
        } catch (ClientProtocolException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            httpPost.abort();
        }
        return result;
    }

    public static String doGet(String url, String request, String contentType) {
        url = StringUtil.getUrl(url, request);
        return doGet(url, contentType,new HashMap<>(16));
    }

    public static String doGet(String url, String request, String contentType, Map<String, String> headers) {
        url = StringUtil.getUrl(url, request);
        return doGet(url, contentType,headers);
    }

    public static String doGet(String url, String contentType, Map<String, String> headers) {
        String result = null;
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        CloseableHttpClient httpClient = httpClientBuilder.build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Content-Type", contentType);
        if (headers != null) {
            headers.forEach((k, v) -> httpGet.addHeader(k, v));
        }
        httpGet.setConfig(requestConfig);
        try {
            HttpResponse response = httpClient.execute(httpGet);

            HttpEntity entity = response.getEntity();

            result = EntityUtils.toString(entity, UTF8);
        } catch (ClientProtocolException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            httpGet.abort();
        }
        return result;
    }


}
