package com.github.wang.wrpc.context.registry;

import com.github.wang.wrpc.common.ext.Spi;
import com.github.wang.wrpc.context.config.ConsumerConfig;
import com.github.wang.wrpc.context.config.ProviderConfig;
import com.github.wang.wrpc.context.config.RegistryConfig;
import com.github.wang.wrpc.context.observer.ProviderObserver;


/**
 * @author : wang
 * @date : 2020/1/5
 */
@Spi(singleton = false)
public abstract class Registry {

    protected RegistryConfig registryConfig;

    public Registry(RegistryConfig registryConfig) {
        this.registryConfig = registryConfig;
    }

    /**
     * 初始化
     */
    public abstract boolean start();

    public abstract void register(ProviderConfig config);

    public abstract ProviderGroup subscribe(final ConsumerConfig config);

    public abstract void registerObserver(ProviderObserver providerObserver);


}
