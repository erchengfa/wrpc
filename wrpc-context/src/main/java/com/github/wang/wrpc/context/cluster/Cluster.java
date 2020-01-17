package com.github.wang.wrpc.context.cluster;

import com.github.wang.wrpc.common.ext.Spi;
import com.github.wang.wrpc.context.common.Invocation;
import com.github.wang.wrpc.context.common.Invoker;
import com.github.wang.wrpc.context.common.WRPCResult;

/**
 * @author : wang
 * @date : 2020/1/12
 */
@Spi
public abstract class Cluster implements Invoker {

    protected ClusterConfig clusterConfig;
    public Cluster(ClusterConfig clusterConfig){
       this.clusterConfig = clusterConfig;
    }
    @Override
    public WRPCResult invoke(Invocation invocation) {

        return this.doInvoke(invocation);
    }


    public abstract WRPCResult doInvoke(Invocation invocation);


}
