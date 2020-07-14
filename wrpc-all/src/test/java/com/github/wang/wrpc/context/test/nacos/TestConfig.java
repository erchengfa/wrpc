package com.github.wang.wrpc.context.test.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executor;

public class TestConfig {

    @Test
    public void test1(){
        try {
            String serverAddr = "localhost";
            String dataId = "hello.yml";
            String group = "DEFAULT_GROUP";
            Properties properties = new Properties();
            properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
            ConfigService configService = NacosFactory.createConfigService(properties);
            String content = configService.getConfig(dataId, group, 5000);
            System.out.println(content);
            configService.addListener(dataId, group, new Listener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    System.out.println("recieve:" + configInfo);
                }

                @Override
                public Executor getExecutor() {
                    return null;
                }
            });

            System.in.read();
        } catch (NacosException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
