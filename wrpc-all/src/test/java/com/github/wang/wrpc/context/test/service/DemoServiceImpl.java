package com.github.wang.wrpc.context.test.service;

/**
 * @author : wang
 * @date : 2020/1/11
 */
public class DemoServiceImpl implements IDemoService{
    @Override
    public String sayHello(String name){
        return "hello " + name;
    }
}
