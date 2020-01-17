package com.github.wang.wrpc.cluster;

import com.github.wang.wrpc.context.cluster.AbstractLoadBalance;
import com.github.wang.wrpc.context.common.Invocation;
import com.github.wang.wrpc.context.consumer.RpcInvoker;

import java.util.List;
import java.util.Random;

/**
 * @author : wang
 * @date : 2020/1/12
 */
public class RandomLoadBalancer extends AbstractLoadBalance {

    private final Random random = new Random();


    @Override
    public RpcInvoker doSelect(Invocation invocation, List<RpcInvoker> rpcInvokers) {
        RpcInvoker rpcInvoker = null;
        int size = rpcInvokers.size(); // 总个数
        int totalWeight = 0; // 总权重
        boolean isWeightSame = true; // 权重是否都一样
        for (int i = 0; i < size; i++) {
            int weight = getWeight(rpcInvokers.get(i));
            totalWeight += weight; // 累计总权重
            if (isWeightSame && i > 0 && weight != getWeight(rpcInvokers.get(i - 1))) {
                isWeightSame = false; // 计算所有权重是否一样
            }
        }
        if (totalWeight > 0 && !isWeightSame) {
            // 如果权重不相同且权重大于0则按总权重数随机
            int offset = random.nextInt(totalWeight);
            // 并确定随机值落在哪个片断上
            for (int i = 0; i < size; i++) {
                offset -= getWeight(rpcInvokers.get(i));
                if (offset < 0) {
                    rpcInvoker = rpcInvokers.get(i);
                    break;
                }
            }
        } else {
            // 如果权重相同或权重为0则均等随机
            rpcInvoker = rpcInvokers.get(random.nextInt(size));
        }
        return rpcInvoker;
    }
}
