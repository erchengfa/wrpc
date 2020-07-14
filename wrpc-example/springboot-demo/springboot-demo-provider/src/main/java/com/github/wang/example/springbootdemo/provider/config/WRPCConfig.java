package com.github.wang.example.springbootdemo.provider.config;

import com.github.wang.example.springbootdemo.api.IHelloService;
import com.github.wang.wrpc.context.config.ProviderConfig;
import com.github.wang.wrpc.context.config.RegistryConfig;
import com.github.wang.wrpc.context.config.ServerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class WRPCConfig {

    /**
     * 配置注册中心
     * @return
     */
    @Bean
    public RegistryConfig registryConfig(){
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol("zookeeper");
        registryConfig.setAddress("118.89.196.99:2181");
        return registryConfig;
    }

    /**
     * 配置暴露的服务协议和绑定的端口号
     * @return
     */
    @Bean
    public ServerConfig serverConfig(){
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setProtocol("wang");
        serverConfig.setPort(20801);
        return serverConfig;
    }

    /**
     * 配置服务
     * @param helloService1
     * @return
     */
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

    /**
     * 配置服务 版本号为v2
     * @param helloService2
     * @return
     */
    @Bean
    public ProviderConfig providerHelloServiceV2(IHelloService helloService2){
        ProviderConfig<IHelloService> providerConfig = new ProviderConfig<>();
        providerConfig.setAppName("demo1");
        providerConfig.setInterfaceClass(IHelloService.class);
        providerConfig.setServiceBean(helloService2);
        providerConfig.setServiceVersion("v2"); //设置服务版本号
        providerConfig.setServer(serverConfig());
        providerConfig.setRegistry(registryConfig());
        return providerConfig;
    }

}
