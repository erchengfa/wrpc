package com.github.wang.wrpc.context.Interceptor;

import com.github.wang.wrpc.context.common.Invocation;
import com.github.wang.wrpc.context.common.Invoker;
import com.github.wang.wrpc.context.registry.ProviderInfo;
import com.github.wang.wrpc.context.common.WRPCResult;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author : wang
 * @date : 2020/1/12
 */
@Slf4j
public class InterceptorChainInvoker implements Invoker {

    List<Interceptor> interceptors;
    public InterceptorChainInvoker(){


    }
    @Override
    public WRPCResult invoke(Invocation invocation) {

        return null;
    }

    public Object interceptorChain(ProviderInfo providerInfo,Invocation invocation){
        log.debug("InterceptorChainInvoker providerInfo invocation:{},{}",providerInfo,invocation);

        return null;
    }

}
