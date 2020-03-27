package com.carroll.monitor.analyzer.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.carroll.utils.Md5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author: carroll
 * @date 2019/9/9
 */
public class SignKit {

    private static final Logger logger = LoggerFactory.getLogger(SignKit.class);

    private static final String TIMESTAMP = "timestamp";

    private static final String SIGN = "sign";

    private SignKit() {
    }


    /**
     * 时间戳
     *
     * @return
     */
    public static long getTimestamp() {
        long currentTime = System.currentTimeMillis();
        logger.info("{}", currentTime);
        return currentTime;
    }

    /**
     * 签名算法
     *
     * @param jsonObject
     * @param secretKey
     * @return
     */
    public static String getSign(JSONObject jsonObject, String secretKey) {
        SortedMap<String, Object> sortedMap;

        sortedMap = JSON.parseObject(jsonObject.toString(), SortedMap.class);

        return getSign(sortedMap, secretKey);
    }

    /**
     * 签名算法
     *
     * @param obj javaBean
     * @return
     */
    public static String getSign(Object obj, String secretKey) {
        SortedMap<String, Object> sortedMap;
        sortedMap = JSON.parseObject(JSON.toJSONString(obj), SortedMap.class);

        return getSign(sortedMap, secretKey);
    }

    /**
     * 签名算法
     *
     * @param sortedMap
     * @param secretKey
     * @return
     */
    public static String getSign(SortedMap<String, Object> sortedMap, String secretKey) {
        StringBuilder sb = new StringBuilder();
        //所有参与传参的参数按照accsii排序（升序）
        Set es = sortedMap.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            Object value = entry.getValue();
            if (value != null && !StringUtils.isEmpty(value.toString()) && !SIGN.equals(key)) {
                sb.append(key).append("=").append(value).append("&");
            }
        }
        sb.append("key=").append(secretKey);
        logger.info("pre-toMD5:{}", sb.toString());
        String result = Md5Util.md5Encode(sb.toString()).toUpperCase();
        logger.info("MD5:{}", result);
        return result;
    }

    /**
     * 签名验证
     *
     * @param jsonObject
     * @param secretKey
     * @return
     */
    public static boolean verifySign(JSONObject jsonObject, String secretKey, int timeOut) {
        SortedMap<String, Object> sortedMap;
        sortedMap = JSON.parseObject(jsonObject.toString(), SortedMap.class);
        return verifySign(sortedMap, secretKey, timeOut);
    }

    /**
     * 签名验证
     *
     * @param obj javaBean
     * @return
     */
    public static boolean verifySign(Object obj, String secretKey, int timeOut) {
        String jsonObject = JSONObject.toJSONString(obj);
        logger.info("secretKey:{}", secretKey);
        logger.info("sign params:{}", jsonObject);
        SortedMap<String, Object> sortedMap = JSON.parseObject(jsonObject, SortedMap.class);
        return verifySign(sortedMap, secretKey, timeOut);
    }

    public static boolean verifySign(SortedMap<String, Object> sortedMap, String secretKey, int timeOut) {
        long distance = (getTimestamp() - Long.parseLong(sortedMap.get(TIMESTAMP).toString())) / 1000;
        logger.info("distance:{},timeOut:{}", distance, timeOut);
        if (distance >= timeOut) {
            logger.info("{}:sortedMap timestamp illegal", sortedMap);
            return false;
        } else {
            String sign = getSign(sortedMap, secretKey);
            return sign.equals(sortedMap.get(SIGN));
        }
    }

    public static String getSign4Tsp(String urlResource, Map<String, Object> params, String secretKey) throws UnsupportedEncodingException {
        LinkedList parameters = new LinkedList(params.entrySet());
        Collections.sort(parameters, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return ((String) o1.getKey()).compareTo((String) o2.getKey());
            }
        });
        StringBuilder sb = new StringBuilder();
        sb.append(urlResource).append("_");
        Iterator baseString = parameters.iterator();
        while (baseString.hasNext()) {
            Map.Entry param = (Map.Entry) baseString.next();
            sb.append((String) param.getKey()).append("=").append((String) param.getValue()).append("_");
        }

        sb.append(secretKey);
        logger.info("pre-toMD5:{}", sb.toString());
        String baseString1 = URLEncoder.encode(sb.toString(), "UTF-8");
        logger.info("pre-toMD5:{}", baseString1);
        String result = Md5Util.md5Encode(baseString1);
        logger.info("MD5:{}", result);
        return result;
    }

}
