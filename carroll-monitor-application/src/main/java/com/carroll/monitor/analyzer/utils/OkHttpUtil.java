package com.carroll.monitor.analyzer.utils;


import com.alibaba.fastjson.JSONObject;
import com.carroll.monitor.analyzer.config.OkHttpConfig;
import com.carroll.utils.StringUtil;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: carroll
 * @date 2019/9/9
 */
@Component
public class OkHttpUtil {
    private static final Logger logger = LoggerFactory.getLogger(OkHttpUtil.class);

    private static int CONNECT_TIMEOUT = 10;
    private static int READ_TIMEOUT = 10;
    private static int WRITE_TIMEOUT = 10;

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType FORM = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    private static OkHttpClient client;

    @Autowired
    OkHttpConfig config;

    private OkHttpUtil() {
    }

    /**
     * 参数初始化
     */
    @PostConstruct
    private void init() {
        logger.info("\n init OkHttpUtil start");
        logger.info("\n params: " + StringUtil.objToJsonString(config));
        if (config.isEnable()) {
            CONNECT_TIMEOUT = config.getConnectTimeout();
            READ_TIMEOUT = config.getReadTimeout();
            WRITE_TIMEOUT = config.getWriteTimeout();
        }

        client = new OkHttpClient().newBuilder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

    /**
     * post请求
     *
     * @param url
     * @param content
     * @return
     * @throws IOException
     */
    public static String doPost(String url, String content) throws IOException {
        return doPost(url, content, null, null, null, null);
    }

    /**
     * post请求
     *
     * @param url
     * @param content
     * @return
     * @throws IOException
     */
    public static String doPost(String url, String content, Map<String, String> headers,
                                Long connectTimeout, Long readTimeout, Long writeTimeout) throws IOException {
        HttpClientHolder.setRequestBody(content);
        RequestBody body = RequestBody.create(JSON, content);
        Request.Builder builder = new Request.Builder().url(url);
        if (headers != null) {
            headers.forEach((k, v) -> builder.addHeader(k, v));
        }
        return invoke(builder.post(body).build(), connectTimeout, readTimeout, writeTimeout);
    }

    /**
     * post请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String doPost(String url, String content, Map<String, String> headers) throws IOException {
        if (content == null) {
            content = new String("{}");
        }
        HttpClientHolder.setRequestBody(content);
        RequestBody body = RequestBody.create(JSON, content);

        Request.Builder builder = new Request.Builder().url(url);
        if (headers != null) {
            headers.forEach((k, v) -> builder.addHeader(k, v));
        }

        builder.url(url)
                .post(body)
                .build();
        return invoke(builder.build());
    }

    /**
     * get请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String doGet(String url) throws IOException {
        return doGet(url, null, null, null, null, null);
    }

    /**
     * get请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String doGet(String url, String requestStr, Map<String, String> headers) throws IOException {
        return doGet(url, requestStr, headers, null, null, null);
    }

    /**
     * get请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String doGet(String url, String requestStr, Map<String, String> headers,
                               Long connectTimeout, Long readTimeout, Long writeTimeout) throws IOException {
        HttpClientHolder.setRequestBody(null);
        if (!StringUtils.isEmpty(requestStr)) {
            url = StringUtil.getUrl(url, requestStr);
        }

        Request.Builder builder = new Request.Builder().url(url);
        if (headers != null) {
            headers.forEach((k, v) -> builder.addHeader(k, v));
        }
        return invoke(builder.build(), connectTimeout, readTimeout, writeTimeout);
    }

    /**
     * delete请求
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static String doDelete(String url) throws IOException {
        HttpClientHolder.setRequestBody(null);
        Request request = new Request.Builder().url(url).delete().build();
        return invoke(request);
    }

    /**
     * put请求
     *
     * @param url
     * @param content
     * @return
     * @throws IOException
     */
    public static String doPut(String url, String content) throws IOException {
        if (content == null) {
            content = new String("{}");
        }
        HttpClientHolder.setRequestBody(content);
        RequestBody body = RequestBody.create(JSON, content);
        Request request = new Request.Builder().url(url).put(body).build();
        return invoke(request);
    }

    private static String invoke(Request request) throws IOException {
        return invoke(request, null, null, null);
    }

    private static String invoke(Request request, Long connectTimeout, Long readTimeout, Long writeTimeout) throws IOException {
        OkHttpClient tempclient = client;
        if (connectTimeout != null || readTimeout != null || writeTimeout != null) {
            tempclient = new OkHttpClient().newBuilder()
                    .connectTimeout(connectTimeout == null ? 10 : connectTimeout, TimeUnit.SECONDS)
                    .readTimeout(readTimeout == null ? 10 : readTimeout, TimeUnit.SECONDS)
                    .writeTimeout(writeTimeout == null ? 10 : writeTimeout, TimeUnit.SECONDS)
                    .build();
        }

        for (HttpClientListener listener : HttpClientListenerRegister.listeners()) {
            try {
                request = listener.pre(request);
            } catch (Exception e) {
                logger.warn("第三方接口调用pre监听器执行错误", e);
            }
        }

//        CallInfo info = new CallInfo();
//        info.setRequestTime(System.currentTimeMillis());
        try (Response response = tempclient.newCall(request).execute()) {
//            info.setResponseTime(System.currentTimeMillis());
            String resStr = null;
            String code = null;
            if (response == null) {
                throw new IOException("response is null:");
            }
            code = String.valueOf(response.code());
            boolean successful = response.isSuccessful();
            if (successful) {
                resStr = response.body().string();
                HttpClientHolder.setResponseBody(resStr);
            }

            for (HttpClientListener listener : HttpClientListenerRegister.listeners()) {
                try {
                    listener.execute(request, HttpClientHolder.getResponseBody());
                } catch (Exception e) {
                    logger.warn("第三方接口调用监听器执行错误", e);
                }
            }

            if (successful) {
                return resStr;
            } else {
                if (response.body() != null) {
                    logger.error("请求失败, 返回结果：{}", response.body().string());
                }
                throw new IOException("Unexpected code :" + code);
            }
        }
    }

    /**
     * 基于application/x-www-form-urlencoded的post请求
     *
     * @param url
     * @param content
     * @return
     * @throws IOException
     */
    public static String doPostFormUrlEncoded(String url, Map<String, String> content) throws IOException {
        if (content != null && !content.isEmpty()) {
            HttpClientHolder.setRequestBody(JSONObject.toJSONString(content));
        }

        FormBody.Builder builder = new FormBody.Builder();
        if (content != null) {
            content.entrySet().forEach(entry -> builder.add(entry.getKey(), entry.getValue()));
        }
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        return invoke(request);
    }

    /**
     * 基于application/x-www-form-urlencoded的put请求
     *
     * @param url
     * @param content
     * @return
     * @throws IOException
     */
    public static String doPutFormUrlEncoded(String url, Map<String, String> content) throws IOException {
        if (content != null && !content.isEmpty()) {
            HttpClientHolder.setRequestBody(JSONObject.toJSONString(content));
        }
        FormBody.Builder builder = new FormBody.Builder();
        if (content != null) {
            content.entrySet().forEach(entry -> builder.add(entry.getKey(), entry.getValue()));
        }
        Request request = new Request.Builder().url(url).put(builder.build()).build();
        return invoke(request);
    }

    /**
     * 基于multipart/form-data的post请求
     *
     * @param url
     * @param content
     * @return
     * @throws IOException
     */
    public static String doPostFormData(String url, Map<String, String> content) throws IOException {
        if (content != null && !content.isEmpty()) {
            HttpClientHolder.setRequestBody(JSONObject.toJSONString(content));
        }
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        if (content != null) {
            content.entrySet().forEach(entry -> builder.addFormDataPart(entry.getKey(), entry.getValue()));
        }
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        return invoke(request);
    }

    /**
     * 基于multipart/form-data的post请求,带文件上传
     *
     * @param url
     * @param content
     * @param fileMap      文件列表
     * @param mediaTypeMap 文件类型列表
     * @return
     * @throws IOException
     */
    public static String doPostFormData(String url, Map<String, String> content, Map<String, File> fileMap, Map<String, MediaType> mediaTypeMap) throws IOException {
        if (content != null && !content.isEmpty()) {
            HttpClientHolder.setRequestBody(JSONObject.toJSONString(content));
        }
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        if (fileMap != null && !fileMap.isEmpty()) {
            fileMap.forEach((key, file) -> {
                RequestBody fileBody = RequestBody.create(mediaTypeMap == null ? null : mediaTypeMap.get(key), file);
                builder.addFormDataPart(key, file.getName(), fileBody);
            });
        }
        if (content != null) {
            content.entrySet().forEach(entry -> builder.addFormDataPart(entry.getKey(), entry.getValue()));
        }
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        return invoke(request);
    }

    /**
     * 基于multipart/form-data的put请求
     *
     * @param url
     * @param content
     * @return
     * @throws IOException
     */
    public static String doPutFormData(String url, Map<String, String> content) throws IOException {
        if (content != null && !content.isEmpty()) {
            HttpClientHolder.setRequestBody(JSONObject.toJSONString(content));
        }
        FormBody.Builder builder = new FormBody.Builder();
        if (content != null) {
            content.entrySet().forEach(entry -> builder.add(entry.getKey(), entry.getValue()));
        }
        Request request = new Request.Builder().url(url).put(builder.build()).build();
        return invoke(request);
    }

    /**
     * url是否连通
     *
     * @param httpUrl url
     * @return boolean
     */
    public static boolean canGetHtmlCode(String httpUrl) {
        boolean result = false;
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/4.0");
            connection.setConnectTimeout(1000);
            connection.connect();
            connection.disconnect();
            result = true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            result = false;
        }
        HttpClientListenerRegister.listeners().forEach(listener -> {
            try {
                listener.execute(new Request.Builder().url(httpUrl).build(), null);
            } catch (Exception e) {
                logger.warn("第三方接口调用监听器执行错误", e);
            }
        });
        return result;
    }
}
