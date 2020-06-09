package com.github.wang.wrpc.context.test.quickstart;

import com.github.wang.wrpc.context.config.ProviderConfig;
import com.github.wang.wrpc.context.config.RegistryConfig;
import com.github.wang.wrpc.context.config.ServerConfig;
import com.github.wang.wrpc.context.test.service.DemoServiceImpl;
import com.github.wang.wrpc.context.test.service.IDemoService;
import org.junit.Test;

import java.io.IOException;


public class TestProvider {

    @Test
    public void test1() throws IOException {
        //注册中心
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol("zookeeper");//协议
        registryConfig.setAddress("118.89.196.99:2181");//注册中心地址

        //配置服务
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(20801);        //设置端口

        ProviderConfig<IDemoService> providerConfig = new ProviderConfig<>();
        providerConfig.setAppName("demo1");//设置应用名
        providerConfig.setInterfaceClass(IDemoService.class);//设置接口类
        providerConfig.setServiceBean(new DemoServiceImpl());//设置服务实现类
        providerConfig.setServer(serverConfig);//设置服务
        providerConfig.setRegistry(registryConfig);//设置注册中心
        providerConfig.export();//暴露服务
        System.in.read();

    }

    @Test
    public void test2() throws IOException {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol("zookeeper");
        registryConfig.setAddress("118.89.196.99:2181");

        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(20802);
        ProviderConfig<IDemoService> providerConfig = new ProviderConfig<>();
        providerConfig.setAppName("demo2");
        providerConfig.setInterfaceClass(IDemoService.class);
        providerConfig.setServiceBean(new DemoServiceImpl());
        providerConfig.setServer(serverConfig);
        providerConfig.setRegistry(registryConfig);
        providerConfig.export();
        System.in.read();

    }


    @Test
    public void test3() throws IOException {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol("zookeeper");
        registryConfig.setAddress("118.89.196.99:2181");

        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(20803);
        ProviderConfig<IDemoService> providerConfig = new ProviderConfig<>();
        providerConfig.setAppName("demo2");
        providerConfig.setInterfaceClass(IDemoService.class);
        providerConfig.setServiceBean(new DemoServiceImpl());
        providerConfig.setServer(serverConfig);
        providerConfig.setRegistry(registryConfig);
        providerConfig.export();
        System.in.read();

    }

}
