package com.github.wang.example.springbootdemo.consumer.config;

import com.github.wang.example.springbootdemo.api.IHelloService;
import com.github.wang.wrpc.context.config.ConsumerConfig;
import com.github.wang.wrpc.context.config.RegistryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : wang
 * @date : 2020/1/21
 */
@Configuration
public class WRPCConfig {

    @Bean
    public RegistryConfig registryConfig(){
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol("zookeeper");
        registryConfig.setAddress("118.89.196.99:2181");
        return registryConfig;
    }


    @Bean
    public ConsumerConfig helloService(){
        ConsumerConfig<IHelloService> consumerConfig = new ConsumerConfig<>();
        consumerConfig.setInterfaceClass(IHelloService.class);
        consumerConfig.setRegistry(registryConfig());
        consumerConfig.setAppName("consumer");
        return consumerConfig;
    }

    @Bean
    public ConsumerConfig helloService2(){
        ConsumerConfig<IHelloService> consumerConfig = new ConsumerConfig<>();
        consumerConfig.setInterfaceClass(IHelloService.class);
        consumerConfig.setServiceVersion("v2");
        consumerConfig.setRegistry(registryConfig());
        consumerConfig.setAppName("consumer");
        return consumerConfig;
    }


}
