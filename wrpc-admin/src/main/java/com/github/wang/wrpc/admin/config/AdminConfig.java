package com.github.wang.wrpc.admin.config;

import com.github.wang.wrpc.admin.common.ZKClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : wang
 * @date : 2020/2/5
 */
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
