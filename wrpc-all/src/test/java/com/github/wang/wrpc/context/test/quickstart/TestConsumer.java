package com.github.wang.wrpc.context.test.quickstart;

import com.github.wang.wrpc.context.config.ConsumerConfig;
import com.github.wang.wrpc.context.config.RegistryConfig;
import com.github.wang.wrpc.context.test.service.IDemoService;
import org.junit.Test;

import java.io.IOException;

/**
 * @author : wang
 * @date : 2020/1/11
 */
public class TestConsumer {

    @Test
    public void test1() throws IOException {
        //注册中心
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol("zookeeper");//协议
        registryConfig.setAddress("118.89.196.99:2181");//注册中心地址
        ConsumerConfig<IDemoService> consumerConfig = new ConsumerConfig<>();
        consumerConfig.setInterfaceClass(IDemoService.class);//设置接口类
        consumerConfig.setRegistry(registryConfig);//设置注册中心
        consumerConfig.setAppName("consumer");//设置应用程序
        IDemoService iDemoService = consumerConfig.refer();//获取远程代理类
        while (true){
            try {
                System.out.println(iDemoService.sayHello("wang"));
                //Thread.sleep(1000);
            }catch (Exception e){

            }

        }
//        System.in.read();
    }
    @Test
    public void test2() throws IOException {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol("zookeeper");
        registryConfig.setAddress("118.89.196.99:2181");
        ConsumerConfig<IDemoService> consumerConfig = new ConsumerConfig<>();
        consumerConfig.setInterfaceClass(IDemoService.class);
        consumerConfig.setRegistry(registryConfig);
        consumerConfig.setAppName("consumer");
        IDemoService iDemoService = consumerConfig.refer();
//        while (true){
//            try {
//                System.out.println(iDemoService.sayHello("wang"));
//                Thread.sleep(1000);
//            }catch (Exception e){
//
//            }
//
//        }
        System.in.read();
    }

}
