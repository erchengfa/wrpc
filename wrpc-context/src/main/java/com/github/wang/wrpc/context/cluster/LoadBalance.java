package com.github.wang.wrpc.context.cluster;

import com.github.wang.wrpc.common.ext.Spi;
import com.github.wang.wrpc.context.common.Invocation;
import com.github.wang.wrpc.context.consumer.RpcInvoker;

import java.util.List;


@Spi
public interface  LoadBalance {

     RpcInvoker select(Invocation invocation,List<RpcInvoker> rpcInvokers);

}
