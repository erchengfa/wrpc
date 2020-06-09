package com.github.wang.wrpc.admin.config;

import com.github.wang.wrpc.admin.common.ZKClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@Slf4j
@EnableConfigurationProperties(ZkProperties.class)
public class AdminConfig {

    @Autowired
    ZkProperties zkProperties;

    @Bean
    public ZKClient zkClient(){

        return new ZKClient(zkProperties);
    }

}
