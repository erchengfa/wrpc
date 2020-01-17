package com.github.wang.wrpc.context.cluster;

import com.github.wang.wrpc.context.common.Invocation;
import com.github.wang.wrpc.context.consumer.RpcInvoker;

import java.util.List;

/**
 * @author : wang
 * @date : 2020/1/12
 */
public abstract class AbstractLoadBalance implements LoadBalance{

    @Override
    public RpcInvoker select(Invocation invocation,List<RpcInvoker> rpcInvokers) {

        return this.doSelect(invocation,rpcInvokers);
    }

    public abstract RpcInvoker doSelect(Invocation invocation,List<RpcInvoker> rpcInvokers);


    /**
     * 获取权重
     * @param rpcInvoker
     * @return
     */
    protected int getWeight(RpcInvoker rpcInvoker) {
        // 从provider中或得到相关权重,默认值100
        return rpcInvoker.getWeight() < 0 ? 0 : rpcInvoker.getWeight();
    }

}
