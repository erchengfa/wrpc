package com.github.wang.wrpc.context.test.nacos;


import com.github.wang.wrpc.common.ext.ServiceLoader;
import com.github.wang.wrpc.common.ext.ServiceLoaderFactory;
import com.github.wang.wrpc.context.config.ConsumerConfig;
import com.github.wang.wrpc.context.config.ProviderConfig;
import com.github.wang.wrpc.context.config.RegistryConfig;
import com.github.wang.wrpc.context.config.ServerConfig;
import com.github.wang.wrpc.context.observer.ProviderObserver;
import com.github.wang.wrpc.context.registry.ProviderGroup;
import com.github.wang.wrpc.context.registry.Registry;
import com.github.wang.wrpc.context.test.service.DemoServiceImpl;
import com.github.wang.wrpc.context.test.service.IDemoService;
import org.junit.Test;

import java.io.IOException;


public class TestNacosRegistry {


    @Test
    public void test1() throws IOException {

        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setProtocol("wang");
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol("nacos");
        registryConfig.setAddress("127.0.0.1:8848");
        ServiceLoader<Registry> registryServiceLoader = ServiceLoaderFactory.getExtensionLoader(Registry.class);
        Registry registry = registryServiceLoader.getInstance("nacos",//
                new Class[]{RegistryConfig.class}, new RegistryConfig[]{registryConfig});

        registry.start();
        ProviderConfig<IDemoService> providerConfig = new ProviderConfig<>();
        providerConfig.setAppName("hello");
        providerConfig.setInterfaceClass(IDemoService.class);
        providerConfig.setServiceBean(new DemoServiceImpl());
        providerConfig.setRegistry(registryConfig);
        providerConfig.setServer(serverConfig);
        registry.register(providerConfig);

        System.in.read();
    }



    @Test
    public void test2() throws IOException {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol("nacos");
        registryConfig.setAddress("127.0.0.1:8848");

        ConsumerConfig<IDemoService> consumerConfig = new ConsumerConfig<>();
        consumerConfig.setAppName("hello-consumer");
        consumerConfig.setInterfaceClass(IDemoService.class);
        consumerConfig.setRegistry(registryConfig);

        ServiceLoader<Registry> serviceLoader = ServiceLoaderFactory.getExtensionLoader(Registry.class);
        Registry registry = serviceLoader.getInstance("nacos",//
                new Class[]{RegistryConfig.class}, new RegistryConfig[]{registryConfig});
        registry.registerObserver(new ProviderObserver() {
            @Override
            public void update(ProviderGroup providerGroup) {
                System.out.println("providerGroup:" + providerGroup);
            }

            @Override
            public String getServiceName() {
                return consumerConfig.getServiceName();
            }
        });
        registry.start();
        registry.subscribe(consumerConfig);

        System.in.read();
    }

    @Test
    public void test3() throws IOException {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setProtocol("wang");
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol("nacos");
        registryConfig.setAddress("127.0.0.1:8848");
        ServiceLoader<Registry> registryServiceLoader = ServiceLoaderFactory.getExtensionLoader(Registry.class);
        Registry registry = registryServiceLoader.getInstance("nacos",//
                new Class[]{RegistryConfig.class}, new RegistryConfig[]{registryConfig});
        registry.start();
        ProviderConfig<IDemoService> providerConfig = new ProviderConfig<>();
        providerConfig.setAppName("hello");
        providerConfig.setInterfaceClass(IDemoService.class);
        providerConfig.setServiceBean(new DemoServiceImpl());
        providerConfig.setRegistry(registryConfig);
        providerConfig.setServiceVersion("1.0");
        providerConfig.setServer(serverConfig);
        registry.register(providerConfig);

        System.in.read();
    }

}
