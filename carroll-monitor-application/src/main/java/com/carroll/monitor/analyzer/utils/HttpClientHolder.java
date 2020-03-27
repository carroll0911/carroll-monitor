package com.carroll.monitor.analyzer.utils;

/**
 * @author: carroll
 * @date 2018/7/31
 */
public class HttpClientHolder {

    private static final ThreadLocal<String> requestBody = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return null;
        }
    };

    private static final ThreadLocal<String> responseBody = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return null;
        }
    };

    public static void setRequestBody(String request) {
        requestBody.set(request);
    }

    public static void setResponseBody(String response) {
        responseBody.set(response);
    }

    public static String getRequestBody() {
        return requestBody.get();
    }

    public static String getResponseBody() {
        return responseBody.get();
    }
}
