package com.github.wang.wrpc.common.test;

import com.github.wang.wrpc.common.utils.UrlUtils;
import org.junit.Test;

import java.util.Map;

/**
 * @author : wang
 * @date : 2020/1/11
 */
public class TestUrlUtils {
    @Test
    public void test1() {
        String path = UrlUtils.getURLDecoderString("wang%3A%2F%2F192.168.224.1%3A28000%3Fweight%3D100");
        System.out.println(path);
        Map<String, String> urlParams = UrlUtils.getUrlParams(path);
        System.out.println(urlParams);
    }
}
