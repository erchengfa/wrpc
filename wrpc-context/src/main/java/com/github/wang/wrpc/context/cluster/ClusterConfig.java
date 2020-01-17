package com.github.wang.wrpc.context.cluster;

import com.github.wang.wrpc.context.config.ConsumerConfig;
import com.github.wang.wrpc.context.consumer.RpcInvoker;
import com.github.wang.wrpc.context.consumer.RpcInvokerHolder;

import java.util.List;

/**
 * @author : wang
 * @date : 2020/1/16
 */
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

    public List<RpcInvoker> listRpcInvoker() {
        return rpcInvokerHolder.listRpckInvoker();
    }

    public LoadBalance getLoadBalance(){
        return loadBalance;
    }
}
