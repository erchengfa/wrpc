package com.github.wang.wrpc.context.consumer;

import com.github.wang.wrpc.context.common.*;
import com.github.wang.wrpc.context.config.ConsumerConfig;
import com.github.wang.wrpc.context.remoting.DefaultFuture;
import com.github.wang.wrpc.context.remoting.netty.NettyClient;
import lombok.extern.slf4j.Slf4j;

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
        DefaultFuture defaultFuture = DefaultFuture.newFuture(request, consumerConfig.getInvokeTimeout());
        rpcClient.send(request);
        return new WRPCFutureResult(defaultFuture);
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
