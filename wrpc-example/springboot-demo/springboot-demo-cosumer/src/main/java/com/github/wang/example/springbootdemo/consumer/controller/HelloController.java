package com.github.wang.example.springbootdemo.consumer.controller;

import com.github.wang.example.springbootdemo.api.IHelloService;
import com.github.wang.wrpc.autoconfigure.annotation.RpcReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HelloController {
    @RpcReference
    private IHelloService iHelloService;
    @RpcReference(version = "v2")
    private IHelloService iHelloServiceV2;

    @GetMapping("/say-hello")
    public String syaHello() {
        return iHelloService.sayHello("wrpc");
    }

    @GetMapping("/say-hello-v2")
    public String syaHelloV2() {
        return iHelloServiceV2.sayHello("wrpc");
    }

}
