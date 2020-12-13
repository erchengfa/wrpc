package com.github.wang.example.springbootdemo.provider.service;

import com.github.wang.example.springbootdemo.api.IHelloService;
import com.github.wang.wrpc.autoconfigure.annotation.RpcService;


@RpcService(value = IHelloService.class,version = "v2")
public class HelloService2 implements IHelloService {
    @Override
    public String sayHello(String name) {
        return "welcome v2 " + name;
    }
}
