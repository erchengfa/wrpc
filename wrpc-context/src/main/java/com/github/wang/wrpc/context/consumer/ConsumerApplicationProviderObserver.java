package com.github.wang.wrpc.context.consumer;

import com.github.wang.wrpc.context.registry.ProviderGroup;
import com.github.wang.wrpc.context.observer.ProviderObserver;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ConsumerApplicationProviderObserver implements ProviderObserver {

    private RpcInvokerHolder rpcInvokerHolder;

    public ConsumerApplicationProviderObserver(RpcInvokerHolder rpcInvokerHolder){
        this.rpcInvokerHolder = rpcInvokerHolder;
    }

    @Override
    public void update(ProviderGroup providerGroup) {
        log.debug("ConsumerApplicationProviderObserver update:{}",providerGroup);
        rpcInvokerHolder.refresh(providerGroup);
    }

    @Override
    public String getServiceName() {
        return rpcInvokerHolder.getConsumerConfig().getServiceName();
    }
}
