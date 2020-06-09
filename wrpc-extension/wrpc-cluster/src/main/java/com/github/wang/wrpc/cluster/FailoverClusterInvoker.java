package com.github.wang.wrpc.cluster;

import com.github.wang.wrpc.common.exception.RPCRuntimeException;
import com.github.wang.wrpc.context.cluster.Cluster;
import com.github.wang.wrpc.context.cluster.ClusterConfig;
import com.github.wang.wrpc.context.cluster.LoadBalance;
import com.github.wang.wrpc.context.common.Invocation;
import com.github.wang.wrpc.context.common.WRPCResult;
import com.github.wang.wrpc.context.consumer.RpcInvoker;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Slf4j
public class FailoverClusterInvoker extends Cluster {

    public FailoverClusterInvoker(ClusterConfig clusterConfig) {
        super(clusterConfig);
    }

    @Override
    public WRPCResult doInvoke(Invocation invocation) {
        Integer retries = this.clusterConfig.getRetries();
        LoadBalance loadBalance = clusterConfig.getLoadBalance();
        List<RpcInvoker> rpcInvokers = clusterConfig.listRpcInvoker(invocation);
        for (int i=0; i < retries; i++){
            RpcInvoker rpcInvoker = loadBalance.select(invocation, rpcInvokers);
            try {
                WRPCResult result = rpcInvoker.invoke(invocation);
                return result;
            }catch (Throwable e){
                log.error("providerInfo send request error:{}",rpcInvoker,e);
            }
        }
        throw new RPCRuntimeException("failover cluster send request error");
    }
}
