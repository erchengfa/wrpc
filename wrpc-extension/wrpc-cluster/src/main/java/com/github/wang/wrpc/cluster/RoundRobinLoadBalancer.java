package com.github.wang.wrpc.cluster;

import com.github.wang.wrpc.context.cluster.AbstractLoadBalance;
import com.github.wang.wrpc.context.common.Invocation;
import com.github.wang.wrpc.context.consumer.RpcInvoker;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class RoundRobinLoadBalancer extends AbstractLoadBalance {

    private static AtomicLong pos = new AtomicLong(0);

    @Override
    public RpcInvoker doSelect(Invocation invocation, List<RpcInvoker> RpcInvokers) {
        long count = pos.intValue();
        int index = (int)(count % RpcInvokers.size());
        pos.incrementAndGet(); // ++
        return RpcInvokers.get(index);
    }
}
