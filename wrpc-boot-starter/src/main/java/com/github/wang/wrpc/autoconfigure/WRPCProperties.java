package com.github.wang.wrpc.autoconfigure;


import com.github.wang.wrpc.context.config.RegistryConfig;
import com.github.wang.wrpc.context.config.ServerConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Setter
@Getter
@ConfigurationProperties(prefix = "wrpc")
public class WRPCProperties {

    private String appName;

    private RegistryConfig registry;

    private ServerConfig server;

}
