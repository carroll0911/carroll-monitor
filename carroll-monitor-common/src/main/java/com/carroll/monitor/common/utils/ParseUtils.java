package com.carroll.monitor.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author: carroll
 * @date 2019/9/9
 **/
@Slf4j
public class ParseUtils {

    private ParseUtils() {
    }

    /**
     * 讲Object转为Map
     * @param obj
     * @return
     */
    public static Map parseObj2Map(Object obj) {
        if (obj == null) {
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(obj), Map.class);
    }

    /**
     * Double 转 double  如果为null 返回0.0
     * @param data
     * @return
     */
    public static double parseDouble(Double data){
        return data==null?0:data;
    }

    public static Double parseDouble(Object data) {
        double result=0.0;
        try {
            result = Double.parseDouble(String.valueOf(data));
        }catch (Exception e){
            log.debug(e.getMessage());
            return null;
        }
        return result;
    }

    /**
     * 从JSONObject 根据key 获取double 如果key不存在返回0.0
     * @param jsonObject
     * @param key
     * @return
     */
    public static double getDouble(JSONObject jsonObject,String key){
        return parseDouble(jsonObject.getDouble(key));
    }

    public static long parseLong(Long data) {
        return data == null ? 0 : data;
    }

    public static long getLong(JSONObject jsonObject,String key) {
        return parseLong(jsonObject.getLong(key));
    }
}
