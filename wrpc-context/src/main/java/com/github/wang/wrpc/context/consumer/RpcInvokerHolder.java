package com.github.wang.wrpc.context.consumer;

import com.github.wang.wrpc.common.exception.RPCRuntimeException;
import com.github.wang.wrpc.context.common.GlobalExecutor;
import com.github.wang.wrpc.context.common.Invocation;
import com.github.wang.wrpc.context.config.ConsumerConfig;
import com.github.wang.wrpc.context.registry.ProviderGroup;
import com.github.wang.wrpc.context.registry.ProviderInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class RpcInvokerHolder {

    private static Map<String, RpcInvoker> aliveRpcInvokerMap = new ConcurrentHashMap<>();

    private List<String> urls = new ArrayList<>();

    private ProviderGroup providerGroup;

    private ConsumeApplicationContext context;

    public RpcInvokerHolder(ConsumeApplicationContext context) {
        this.context = context;
    }

    @PostConstruct
    public void init(){
        GlobalExecutor.registerTaskToTimer(()->{
            for (String url : aliveRpcInvokerMap.keySet()){
                RpcInvoker rpcInvoker = aliveRpcInvokerMap.get(url);
                if(rpcInvoker.isDead()){
                    log.error("RpcInvokerHolder dead rpc invoker:{}",rpcInvoker);
                }else if (!rpcInvoker.isActive()){
                    log.debug("wrpc aliveRpcInvokerMap check reconnect:{}",rpcInvoker);
                    rpcInvoker.connect();
                }
            }
        });
    }

    public synchronized void refresh(ProviderGroup providerGroup) {
        log.debug("rpc client poll refresh provider group:{}", providerGroup);
        if (providerGroup == null){
            return;
        }
        List<ProviderInfo> providerInfos = providerGroup.getProviderInfos();
        List<String> newUrls = new ArrayList<>();
        removeRpcInvoker(providerInfos);
        for (ProviderInfo providerInfo : providerInfos) {
            RpcInvoker rpcInvoker = aliveRpcInvokerMap.get(providerInfo.getUrl());
            if (rpcInvoker == null) {
                rpcInvoker = new RpcInvoker(providerInfo,this);
            }else{
                rpcInvoker.updateProviderInfo(providerInfo);
            }
            RpcInvoker finalRpcInvoker = rpcInvoker;
            GlobalExecutor.registerTaskToPool(()->{
                finalRpcInvoker.connect();
                aliveRpcInvokerMap.put(providerInfo.getUrl(), finalRpcInvoker);
            });
            newUrls.add(providerInfo.getUrl());
        }
        this.urls = newUrls;
        this.providerGroup = providerGroup;
        log.info("rpc client poll refresh after:{},{},{}", this.providerGroup, this.urls);
    }

    private void removeRpcInvoker(List<ProviderInfo> providerInfos) {
        List<String> activeUrls = new ArrayList<>();
        for (ProviderInfo providerInfo : providerInfos) {
            activeUrls.add(providerInfo.getUrl());
        }
        if (CollectionUtils.isNotEmpty(urls)) {
            for (String url : urls) {
                if (!activeUrls.contains(url)) {
                    aliveRpcInvokerMap.remove(url);
                }
            }
        }
    }

    public List<RpcInvoker> listRpckInvoker(Invocation invocation) {
        if (CollectionUtils.isEmpty(urls)) {
            throw new RPCRuntimeException(String.format("%s no active provider", context.getConsumerConfig().getServiceName()));
        }
        List<RpcInvoker> rpcInvokers = new ArrayList<>();
        for (String url : urls) {
            RpcInvoker rpcInvoker = aliveRpcInvokerMap.get(url);
            if (rpcInvoker != null && rpcInvoker.isActive()) {
                rpcInvokers.add(rpcInvoker);
            }
        }
        if (CollectionUtils.isEmpty(rpcInvokers)) {
            throw new RPCRuntimeException(String.format("%s no active provider", context.getConsumerConfig().getServiceName()));
        }
        return rpcInvokers;
    }

    public ConsumerConfig getConsumerConfig(){
        return context.getConsumerConfig();
    }


}
