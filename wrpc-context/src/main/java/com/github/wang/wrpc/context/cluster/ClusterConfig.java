package com.github.wang.wrpc.context.cluster;

import com.github.wang.wrpc.context.common.Invocation;
import com.github.wang.wrpc.context.config.ConsumerConfig;
import com.github.wang.wrpc.context.consumer.RpcInvoker;
import com.github.wang.wrpc.context.consumer.RpcInvokerHolder;

import java.util.List;


public class ClusterConfig {

    private RpcInvokerHolder rpcInvokerHolder;
    private LoadBalance loadBalance;
    private ConsumerConfig consumerConfig;

    public ClusterConfig(RpcInvokerHolder rpcInvokerHolder, LoadBalance loadBalance, ConsumerConfig consumerConfig){
        this.rpcInvokerHolder = rpcInvokerHolder;
        this.loadBalance = loadBalance;
        this.consumerConfig = consumerConfig;
    }

    public Integer getRetries() {
        return consumerConfig.getRetries();
    }

    public List<RpcInvoker> listRpcInvoker(Invocation invocation) {
        return rpcInvokerHolder.listRpckInvoker(invocation);
    }

    public LoadBalance getLoadBalance(){
        return loadBalance;
    }
}
