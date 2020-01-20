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
        consumerConfig.setAppName("consumer");
        IDemoService iDemoService = consumerConfig.refer();
        while (true){
            try {
                System.out.println(iDemoService.sayHello("wang"));
                Thread.sleep(1000);
            }catch (Exception e){

            }

        }
//        System.in.read();
    }

}
