package com.github.wang.wrpc.context.consumer;

import com.github.wang.wrpc.common.exception.RPCRuntimeException;
import com.github.wang.wrpc.common.utils.StringUtils;
import com.github.wang.wrpc.context.common.GlobalExecutor;
import com.github.wang.wrpc.context.common.Invocation;
import com.github.wang.wrpc.context.config.ConsumerConfig;
import com.github.wang.wrpc.context.registry.ProviderGroup;
import com.github.wang.wrpc.context.registry.ProviderInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


@Slf4j
public class RpcInvokerHolder {

    /**
     * key:服务名 ----  value：该服务所有对应的Invoker列表
     */
    private static Map<String, List<RpcInvoker>> aliveRpcInvokerMap = new ConcurrentHashMap<>();

    private ConsumeApplicationContext context;

    static {
        init();
    }

    public RpcInvokerHolder(ConsumeApplicationContext context) {
        this.context = context;
        init();
    }

    public static void init() {
        GlobalExecutor.registerTaskToTimer(() -> {
            log.debug("aliveRpcInvokerMap:{}", aliveRpcInvokerMap);
            for (String serviceName : aliveRpcInvokerMap.keySet()) {
                List<RpcInvoker> list = aliveRpcInvokerMap.get(serviceName);
                for (RpcInvoker rpcInvoker : list) {
                    if (!rpcInvoker.isActive()) {
                        log.debug("wrpc aliveRpcInvokerMap check reconnect:{}", rpcInvoker);
                        GlobalExecutor.registerTaskToPool(() -> {
                            rpcInvoker.connect();
                        });
                    }
                }
            }
        });
    }

    public synchronized void refresh(ProviderGroup providerGroup) {
        if (providerGroup == null) {
            return;
        }
        if (StringUtils.isEmpty(providerGroup.getServiceName())) {
            return;
        }
        List<ProviderInfo> providerInfos = providerGroup.getProviderInfos();
        Map<ProviderInfo, RpcInvoker> rpcInvokerMap = getServiceRpcInvokerMap(providerGroup.getServiceName());

        List<RpcInvoker> newRpcInvokerList = new CopyOnWriteArrayList<RpcInvoker>();
        for (ProviderInfo providerInfo : providerInfos) {
            RpcInvoker rpcInvoker = rpcInvokerMap.get(providerInfo);
            if (rpcInvoker == null) {
                rpcInvoker = new RpcInvoker(providerInfo, this);
            } else {
                rpcInvoker.updateProviderInfo(providerInfo);
            }
            rpcInvoker.connect();
            newRpcInvokerList.add(rpcInvoker);
        }
        aliveRpcInvokerMap.put(providerGroup.getServiceName(), newRpcInvokerList);
        log.info("rpc client poll refresh after:{},{}", newRpcInvokerList);
    }

    private Map<ProviderInfo, RpcInvoker> getServiceRpcInvokerMap(String serviceName) {
        Map<ProviderInfo, RpcInvoker> rpcInvokerMap = new HashMap<>();
        List<RpcInvoker> rpcInvokers = aliveRpcInvokerMap.get(serviceName);
        if (CollectionUtils.isNotEmpty(rpcInvokers)) {
            for (RpcInvoker rpcInvoker : rpcInvokers) {
                rpcInvokerMap.put(rpcInvoker.getProviderInfo(), rpcInvoker);
            }
        }
        return rpcInvokerMap;
    }


    public List<RpcInvoker> listRpckInvoker(Invocation invocation) {
        List<RpcInvoker> rpcInvokers = aliveRpcInvokerMap.get(invocation.getServiceName());
        if (CollectionUtils.isEmpty(rpcInvokers)) {
            throw new RPCRuntimeException(String.format("%s no active provider", context.getConsumerConfig().getServiceName()));
        }
        return rpcInvokers.stream().filter(item -> item.isActive()).collect(Collectors.toList());
    }

    public ConsumerConfig getConsumerConfig() {
        return context.getConsumerConfig();
    }


}
