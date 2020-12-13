package com.github.wang.wrpc.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "wrpc", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(WRPCProperties.class)
public class WRPCAutoConfiguration {

    @Bean
    public WRPCSpringBootstrap wrpcSpringBootstrap(WRPCProperties wrpcProperties) {
        return new WRPCSpringBootstrap(wrpcProperties);
    }

    @Bean
    public RpcReferenceBeanPostProcessor rpcReferenceBeanPostProcessor(WRPCProperties wrpcProperties) {
        return new RpcReferenceBeanPostProcessor(wrpcProperties);
    }
}
