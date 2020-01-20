package com.github.wang.wrpc.context.consumer;

import com.github.wang.wrpc.common.exception.RPCRuntimeException;
import com.github.wang.wrpc.common.utils.ClassUtils;
import com.github.wang.wrpc.context.annotation.WRpcMethod;
import com.github.wang.wrpc.context.common.*;
import com.github.wang.wrpc.context.config.ConsumerConfig;
import com.github.wang.wrpc.context.filter.FilterChain;
import com.github.wang.wrpc.context.registry.ProviderInfo;
import com.github.wang.wrpc.context.remoting.DefaultFuture;
import com.github.wang.wrpc.context.remoting.netty.NettyClient;
import com.github.wang.wrpc.context.serializer.SerializerUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author : wang
 * @date : 2020/1/14
 */
@Slf4j
public class RpcInvoker implements Invoker {

    private RpcClient rpcClient;
    private ProviderInfo providerInfo;

    private RpcInvokerHolder rpcInvokerHolder;

    public RpcInvoker(ProviderInfo providerInfo,RpcInvokerHolder rpcInvokerHolder) {
        this.providerInfo = providerInfo;
        this.rpcInvokerHolder = rpcInvokerHolder;
        this.rpcClient = new NettyClient(providerInfo);
    }

    public  void connect() {
        try {
            rpcClient.connect();
        } catch (Throwable e) {
            log.error("rpc invoker connect fail:{}", e);
        }
    }

    @Override
    public WRPCResult invoke(Invocation invocation) {
        Invoker invoker = new Invoker() {
            @Override
            public WRPCResult invoke(Invocation invocation) {
                log.debug("send request providerInfo invocation:{},{}", providerInfo, invocation);
                byte serializerId = SerializerUtils.getSerializerId(getConsumerConfig().getSerialization());
                Request request = new Request(serializerId);
                request.setHeartbeat(false);
                request.setBody(invocation);
                assembleRequest(request, invocation);
                if (request.isBack()) {
                    DefaultFuture defaultFuture = DefaultFuture.newFuture(request, getConsumerConfig().getInvokeTimeout());
                    rpcClient.send(request);
                    return new WRPCFutureResult(defaultFuture);
                } else {
                    rpcClient.send(request);
                    return new WRPCFutureResult();
                }
            }
        };
        Invoker filterInvoker = FilterChain.buildInvokerChain(invoker, invocation);
        return filterInvoker.invoke(invocation);
    }

    public void assembleRequest(Request request, Invocation invocation) {
        String invocationMethodName = ClassUtils.getMethodName(invocation.getMethodName(), invocation.getParameterTypes());
        Method invokeMethod = null;
        for (Method method : (List<Method>) getConsumerConfig().getMethods()) {
            String methodName = ClassUtils.getMethodName(method.getName(), method.getParameterTypes());
            if (methodName.equals(invocationMethodName)) {
                invokeMethod = method;
            }
        }
        if (null == invokeMethod) {
            throw new RPCRuntimeException(String.format("Unreasonable method %s", invocation.getMethodName()));
        }
        WRpcMethod wRpcMethod = invokeMethod.getAnnotation(WRpcMethod.class);
        if (wRpcMethod != null) {
            request.setBack(wRpcMethod.back());
        } else {
            request.setBack(true);
        }

    }


    public int getWeight() {
        return providerInfo.getWeight();
    }

    public boolean isActive() {

        return rpcClient.isActive();
    }

    public boolean isDead() {
        return rpcClient.isDead();
    }

    private ConsumerConfig getConsumerConfig(){
        return rpcInvokerHolder.getConsumerConfig();
    }
}
