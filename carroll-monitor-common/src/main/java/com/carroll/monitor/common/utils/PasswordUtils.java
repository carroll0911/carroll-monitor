package com.carroll.monitor.common.utils;

/**
 * @author: carroll
 * @date 2019/9/9
 */
public class PasswordUtils {

    private static final String SEPERATOR="_";

    private PasswordUtils(){}

    public static String encodePassword(String projectTag,String password) {
        return MD5Util.md5Encode(projectTag+SEPERATOR+password);
    }

    public static boolean checkPassword(String projectTag,String password,String encoded){
        return MD5Util.md5Encode(projectTag+SEPERATOR+password).equals(encoded);
    }
}
