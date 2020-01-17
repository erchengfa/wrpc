package com.github.wang.wrpc.context.test.quickstart;

import com.github.wang.wrpc.context.config.ConsumerConfig;
import com.github.wang.wrpc.context.config.RegistryConfig;
import com.github.wang.wrpc.context.test.service.IDemoService;

import java.io.IOException;

/**
 * @author : wang
 * @date : 2020/1/11
 */
public class TestConsumer {

    public static void main(String[] args) throws IOException, InterruptedException {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol("zookeeper");
        registryConfig.setAddress("118.89.196.99:2181");
        ConsumerConfig<IDemoService> consumerConfig = new ConsumerConfig<>();
        consumerConfig.setInterfaceClass(IDemoService.class);
        consumerConfig.setRegistry(registryConfig);
        consumerConfig.setApplicationName("consumer");
        IDemoService iDemoService = consumerConfig.refer();
        while (true){
            String wang = iDemoService.sayHello("wang");
            System.out.println(wang);
            Thread.sleep(1000);
        }

    }

}
