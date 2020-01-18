package com.github.wang.wrpc.context.consumer;

import com.github.wang.wrpc.common.exception.RPCRuntimeException;
import com.github.wang.wrpc.common.utils.ClassUtils;
import com.github.wang.wrpc.context.annotation.WRpcMethod;
import com.github.wang.wrpc.context.common.*;
import com.github.wang.wrpc.context.config.ConsumerConfig;
import com.github.wang.wrpc.context.registry.ProviderInfo;
import com.github.wang.wrpc.context.remoting.DefaultFuture;
import com.github.wang.wrpc.context.remoting.netty.NettyClient;
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

    private ConsumerConfig consumerConfig;

    public RpcInvoker(ProviderInfo providerInfo,ConsumerConfig consumerConfig) {
        this.providerInfo = providerInfo;
        this.consumerConfig = consumerConfig;
        this.rpcClient = new NettyClient(providerInfo);
    }

    public void connect() {
        try {
            rpcClient.connect();
        } catch (Throwable e) {
            log.error("rpc invoker connect fail:{}", e);
        }
    }

    @Override
    public WRPCResult invoke(Invocation invocation) {
        log.debug("send request providerInfo invocation:{},{}", providerInfo, invocation);

        Request request = new Request();
        request.setHeartbeat(false);
        request.setBody(invocation);
        assembleRequest(request,invocation);
        if (request.isBack()){
            DefaultFuture defaultFuture = DefaultFuture.newFuture(request, consumerConfig.getInvokeTimeout());
            rpcClient.send(request);
            return new WRPCFutureResult(defaultFuture);
        }else {
            rpcClient.send(request);
            return new WRPCFutureResult();
        }

    }

    public void assembleRequest(Request request,Invocation invocation){
        String invocationMethodName = ClassUtils.getMethodName(invocation.getMethodName(), invocation.getParameterTypes());
        Method invokeMethod = null;
        for (Method method : (List<Method>)consumerConfig.getMethods()){
            String methodName = ClassUtils.getMethodName(method.getName(), method.getParameterTypes());
            if (methodName.equals(invocationMethodName)){
                invokeMethod = method;
            }
        }
        if (null  == invokeMethod){
            throw new RPCRuntimeException(String.format("Unreasonable method %s",invocation.getMethodName()));
        }
        WRpcMethod wRpcMethod = invokeMethod.getAnnotation(WRpcMethod.class);
        if (wRpcMethod != null){
            request.setBack(wRpcMethod.back());
        }else {
            request.setBack(true);
        }

    }




    public int getWeight() {
        return providerInfo.getWeight();
    }

    public boolean isActive(){

        return rpcClient.isActive();
    }

    public boolean isRemove(){
        return rpcClient.isRemove();
    }
}