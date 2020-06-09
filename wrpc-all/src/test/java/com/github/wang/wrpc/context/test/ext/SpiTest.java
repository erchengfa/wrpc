package com.github.wang.wrpc.context.test.ext;


import com.github.wang.wrpc.common.ext.ServiceLoader;
import com.github.wang.wrpc.common.ext.ServiceLoaderFactory;


public class SpiTest {

    public static void main(String[] args) {

        ServiceLoader<Hello> serviceLoader = ServiceLoaderFactory.getExtensionLoader(Hello.class);
        Hello hello3 = serviceLoader.getInstance("hello3");
        System.out.println(hello3.getHello());

    }

}
