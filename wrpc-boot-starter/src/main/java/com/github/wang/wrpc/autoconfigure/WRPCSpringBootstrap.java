package com.github.wang.wrpc.autoconfigure;

import com.github.wang.wrpc.context.config.ProviderConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WRPCSpringBootstrap implements ApplicationContextAware,InitializingBean {

    private Set<ProviderConfig> providerConfigs;

    @Override
    public void afterPropertiesSet() throws Exception {
        for(ProviderConfig providerConfig: providerConfigs){
            providerConfig.export();
        }
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, ProviderConfig> serviceBeanMap = applicationContext.getBeansOfType(ProviderConfig.class);
        this.providerConfigs = new HashSet<>(serviceBeanMap.values());
    }
}
