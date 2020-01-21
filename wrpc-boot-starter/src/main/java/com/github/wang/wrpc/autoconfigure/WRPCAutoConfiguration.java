package com.github.wang.wrpc.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : wang
 * @date : 2020/1/20
 */
@Configuration
public class WRPCAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "wrpc", name = "enabled", havingValue = "true")
    public WRPCSpringBootstrap wrpcSpringBootstrap() {
        return new WRPCSpringBootstrap();
    }

    @Bean
    @ConditionalOnProperty(prefix = "wrpc", name = "enabled", havingValue = "true")
    public RpcReferenceBeanPostProcessor rpcReferenceBeanPostProcessor() {
        return new RpcReferenceBeanPostProcessor();
    }
}
