package com.github.wang.example.springbootdemo.provider.service;

import com.github.wang.example.springbootdemo.api.IHelloService;
import org.springframework.stereotype.Service;

/**
 * @author : wang
 * @date : 2020/1/21
 */
@Service("helloService1")
public class HelloService1 implements IHelloService {
    @Override
    public String sayHello(String name) {
        return "welcome " + name;
    }
}
