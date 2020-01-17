package com.github.wang.wrpc.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author : wang
 * @date : 2020/1/11
 */
@Slf4j
public class UrlUtils {
    private final static String ENCODE = "UTF-8";

    /**
     * URL 解码
     * @param str
     * @return
     */
    public static String getURLDecoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLDecoder.decode(str, ENCODE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * URL 转码
     * @param str
     * @return
     */
    public static String getURLEncoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        try {
            result = java.net.URLEncoder.encode(str, ENCODE);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static Map<String, String> getUrlParams(String url) {
        return getUrlParams(false, url);
    }

    public static Map<String, String> getUrlParams(boolean nameLowerCase, String url) {
        Map<String, String> paramMap = new LinkedHashMap<>();
        try {
            URI uri = new URI(url);
            String query = uri.getQuery();
            String[] params = query.split("&");
            for (String param : params) {
                String name = param.substring(0, param.indexOf("="));
                String value = param.substring(param.indexOf("=") + 1);
                if (nameLowerCase) {
                    paramMap.put(name.toLowerCase(), value);
                } else {
                    paramMap.put(name, value);
                }
            }
        } catch (Exception e) {
            log.warn(String.format("解析URL:%s 参数出错", url), e);
        }
        return paramMap;
    }



}
