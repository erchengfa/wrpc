package com.github.wang.wrpc.context.observer;

import com.github.wang.wrpc.context.registry.ProviderGroup;

/**
 * @author : wang
 * @date : 2020/1/12
 */
public interface ProviderObserver {

    /**
     * 变更通知
     *
     * @param providerGroup 服务端列表组
     */
    void update(ProviderGroup providerGroup);

    String getServiceName();

}
