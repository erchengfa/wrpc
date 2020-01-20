package com.github.wang.wrpc.context.test.register.zk;


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

/**
 * @author : wang
 * @date : 2020/1/6
 */
public class TestZookeeperRegistry {


    @Test
    public void test1() throws IOException {

        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setProtocol("wang");
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol("zookeeper");
        registryConfig.setAddress("118.89.196.99:2181");
        ServiceLoader<Registry> registryServiceLoader = ServiceLoaderFactory.getExtensionLoader(Registry.class);
        Registry registry = registryServiceLoader.getInstance("zookeeper",//
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
    public void test3() throws IOException {

        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setProtocol("wang");
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol("zookeeper");
        registryConfig.setAddress("118.89.196.99:2181");
        ServiceLoader<Registry> registryServiceLoader = ServiceLoaderFactory.getExtensionLoader(Registry.class);
        Registry registry = registryServiceLoader.getInstance("zookeeper",//
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


    @Test
    public void test2() throws IOException {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol("zookeeper");
        registryConfig.setAddress("118.89.196.99:2181");

        ServiceLoader<Registry> serviceLoader = ServiceLoaderFactory.getExtensionLoader(Registry.class);
        Registry registry = serviceLoader.getInstance("zookeeper",//
                new Class[]{RegistryConfig.class}, new RegistryConfig[]{registryConfig});
        registry.registerObserver(new ProviderObserver() {
            @Override
            public void update(ProviderGroup providerGroup) {
                System.out.println("providerGroup:" + providerGroup);
            }
        });
        registry.start();
        ConsumerConfig<IDemoService> consumerConfig = new ConsumerConfig<>();
        consumerConfig.setAppName("hello-consumer");
        consumerConfig.setInterfaceClass(IDemoService.class);
        consumerConfig.setRegistry(registryConfig);
        registry.subscribe(consumerConfig);

        System.in.read();
    }


}
