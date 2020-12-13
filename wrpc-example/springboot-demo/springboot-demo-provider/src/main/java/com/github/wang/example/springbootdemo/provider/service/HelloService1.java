package com.github.wang.example.springbootdemo.provider.service;

import com.github.wang.example.springbootdemo.api.IHelloService;
import com.github.wang.wrpc.autoconfigure.annotation.RpcService;


@RpcService(value = IHelloService.class)
public class HelloService1 implements IHelloService {
    @Override
    public String sayHello(String name) {
        return "welcome " + name;
    }
}
