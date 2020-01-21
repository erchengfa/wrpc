package com.github.wang.example.springbootdemo.provider.config;

import com.github.wang.example.springbootdemo.api.IHelloService;
import com.github.wang.wrpc.context.config.ProviderConfig;
import com.github.wang.wrpc.context.config.RegistryConfig;
import com.github.wang.wrpc.context.config.ServerConfig;
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
    public ServerConfig serverConfig(){
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setProtocol("wang");
        serverConfig.setPort(20801);
        return serverConfig;
    }

    @Bean
    public ProviderConfig providerHelloService(IHelloService helloService1){
        ProviderConfig<IHelloService> providerConfig = new ProviderConfig<>();
        providerConfig.setAppName("demo1");
        providerConfig.setInterfaceClass(IHelloService.class);
        providerConfig.setServiceBean(helloService1);
        providerConfig.setServer(serverConfig());
        providerConfig.setRegistry(registryConfig());
        return providerConfig;
    }

    @Bean
    public ProviderConfig providerHelloServiceV2(IHelloService helloService2){
        ProviderConfig<IHelloService> providerConfig = new ProviderConfig<>();
        providerConfig.setAppName("demo1");
        providerConfig.setInterfaceClass(IHelloService.class);
        providerConfig.setServiceBean(helloService2);
        providerConfig.setServiceVersion("v2");
        providerConfig.setServer(serverConfig());
        providerConfig.setRegistry(registryConfig());
        return providerConfig;
    }

}
