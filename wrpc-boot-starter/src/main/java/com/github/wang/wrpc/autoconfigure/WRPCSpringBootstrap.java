package com.github.wang.wrpc.autoconfigure;

import com.github.wang.wrpc.autoconfigure.annotation.RpcService;
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

    private ApplicationContext applicationContext;

    private WRPCProperties wrpcProperties;

    public WRPCSpringBootstrap(WRPCProperties wrpcProperties) {
        this.wrpcProperties = wrpcProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for(ProviderConfig providerConfig: providerConfigs){
            providerConfig.export();
        }
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        Map<String, ProviderConfig> serviceBeanMap = applicationContext.getBeansOfType(ProviderConfig.class);
        this.providerConfigs = new HashSet<>(serviceBeanMap.values());
        Map<String, Object> autoServiceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        for (String key : autoServiceBeanMap.keySet()){
            Object o = autoServiceBeanMap.get(key);
            RpcService rpcService = o.getClass().getAnnotation(RpcService.class);
            ProviderConfig providerConfig = new ProviderConfig();
            providerConfig.setRegistry(wrpcProperties.getRegistry());
            providerConfig.setAppName(wrpcProperties.getAppName());
            providerConfig.setServer(wrpcProperties.getServer());
            providerConfig.setInterfaceClass(rpcService.value());
            providerConfig.setServiceBean(o);
            providerConfig.setServiceVersion(rpcService.version());
            providerConfigs.add(providerConfig);
        }
    }
}
