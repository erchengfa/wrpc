package com.github.wang.wrpc.context.test.service;

import com.github.wang.wrpc.context.annotation.WRpcMethod;

public interface IDemoService {


    @WRpcMethod
    public void sayHello(String name,Integer age);

    @WRpcMethod
    public String sayHello(String name);

    @WRpcMethod(back = false)
    public String sayHello1(String name);

    public String sayHello2(String name);

    @WRpcMethod(exclude = true)
    public String sayHello3(String name);

}
