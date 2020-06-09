package com.github.wang.wrpc.context.test.service;



public class DemoServiceImpl implements IDemoService{
    @Override
    public void sayHello(String name, Integer age) {

    }

    @Override
    public String sayHello(String name){
        return "hello " + name;
    }

    @Override
    public String sayHello1(String name){
        return "hello " + name;
    }

    @Override
    public String sayHello2(String name){
        return "hello " + name;
    }

    @Override
    public String sayHello3(String name){
        return "hello " + name;
    }
}
