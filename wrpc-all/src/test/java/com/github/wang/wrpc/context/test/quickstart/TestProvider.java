package com.github.wang.wrpc.context.test.quickstart;

import com.github.wang.wrpc.context.config.ProviderConfig;
import com.github.wang.wrpc.context.config.RegistryConfig;
import com.github.wang.wrpc.context.config.ServerConfig;
import com.github.wang.wrpc.context.test.service.DemoServiceImpl;
import com.github.wang.wrpc.context.test.service.IDemoService;
import org.junit.Test;

import java.io.IOException;

/**
 * @author : wang
 * @date : 2020/1/11
 */
public class TestProvider {

    @Test
    public void test1() throws IOException {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol("zookeeper");
        registryConfig.setAddress("118.89.196.99:2181");

        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPort(20801);
        ProviderConfig<IDemoService> providerConfig = new ProviderConfig<>();
        providerConfig.setApplicationName("demo1");
        providerConfig.setInterfaceName(IDemoService.class.getName());
        providerConfig.setServiceBean(new DemoServiceImpl());
        providerConfig.setServer(serverConfig);
        providerConfig.setRegistry(registryConfig);
        providerConfig.export();
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
        providerConfig.setApplicationName("demo2");
        providerConfig.setInterfaceName(IDemoService.class.getName());
        providerConfig.setServiceBean(new DemoServiceImpl());
        providerConfig.setServer(serverConfig);
        providerConfig.setRegistry(registryConfig);
        providerConfig.export();
        System.in.read();

    }

}
