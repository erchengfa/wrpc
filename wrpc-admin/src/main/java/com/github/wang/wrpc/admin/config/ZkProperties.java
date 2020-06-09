package com.github.wang.wrpc.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zk")
@Data
public class ZkProperties {
    private String address;
    private int connectionTimeout;
}
