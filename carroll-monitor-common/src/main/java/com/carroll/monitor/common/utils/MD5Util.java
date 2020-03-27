package com.carroll.monitor.common.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5工具类
 * @author: carroll
 * @date 2019/9/9
 */
public class MD5Util {
    private static Logger log = LoggerFactory.getLogger(MD5Util.class);

    private static final String CHAR_SET_NAME = "UTF-8";

    private MD5Util() {
    }

    public static String stringToMD5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes(CHAR_SET_NAME));
        } catch (NoSuchAlgorithmException|UnsupportedEncodingException e) {
            log.warn(e.getMessage(), e);
            return null;
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    // MD5加密函数
    public static String md5Encode(String sourceString) {
        String resultString = null;
        try {
            resultString = sourceString;
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byte2hexString(md.digest(resultString.getBytes(CHAR_SET_NAME)));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return resultString;
    }

    public static String getMd5(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return byte2hexString(md.digest(bytes));
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
        }
        return null;
    }

    public static final String byte2hexString(byte[] bytes) {
        StringBuilder bf = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            if ((bytes[i] & 0xff) < 0x10) {
                bf.append("0");
            }
            bf.append(Long.toString(bytes[i] & 0xff, 16));
        }
        return bf.toString();
    }
}
