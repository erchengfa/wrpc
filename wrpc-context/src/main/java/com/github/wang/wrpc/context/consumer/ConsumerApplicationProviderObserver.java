package com.github.wang.wrpc.context.consumer;

import com.github.wang.wrpc.context.registry.ProviderGroup;
import com.github.wang.wrpc.context.observer.ProviderObserver;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : wang
 * @date : 2020/1/12
 */
@Slf4j
public class ConsumerApplicationProviderObserver implements ProviderObserver {

    private RpcInvokerHolder rpcInvokerHolder;

    public ConsumerApplicationProviderObserver(RpcInvokerHolder rpcInvokerHolder){
        this.rpcInvokerHolder = rpcInvokerHolder;
    }

    @Override
    public void update(ProviderGroup providerGroup) {
        rpcInvokerHolder.refresh(providerGroup);
    }

    @Override
    public String getServiceName() {
        return rpcInvokerHolder.getConsumerConfig().getServiceName();
    }
}
