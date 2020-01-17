package com.github.wang.wrpc.context.consumer;

import com.github.wang.wrpc.common.exception.RPCRuntimeException;
import com.github.wang.wrpc.common.utils.ThreadPoolUtils;
import com.github.wang.wrpc.context.common.ProviderInfo;
import com.github.wang.wrpc.context.config.ConsumerConfig;
import com.github.wang.wrpc.context.config.ProviderGroup;
import com.github.wang.wrpc.context.timer.TimerManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author : wang
 * @date : 2020/1/11
 */
@Slf4j
public class RpcInvokerHolder {

    private static Map<String, RpcInvoker> aliveRpcInvokerMap = new ConcurrentHashMap<>();

    private static Map<String, RpcInvoker> deadRpcInvokerMap = new ConcurrentHashMap<>();

    private static ThreadPoolExecutor rpcInkokerPoolExecutor = ThreadPoolUtils.newFixedThreadPool(8,new LinkedBlockingQueue<>(10000));

    private List<String> urls = new CopyOnWriteArrayList<>();
    private ConsumerConfig consumerConfig;
    private ProviderGroup providerGroup;

    //定义开始等待时间  ---
    private static final long DELAY = 1000 * 30;
    //间隔时间
    private static final long INTEVAL_PERIOD = 1000 * 30;

    static {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                for (String url : aliveRpcInvokerMap.keySet()){
                    RpcInvoker rpcInvoker = aliveRpcInvokerMap.get(url);
                    if (!rpcInvoker.isActive()){
                        rpcInkokerPoolExecutor.submit(new Runnable() {
                            @Override
                            public void run() {
                                log.debug("wrpc aliveRpcInvokerMap check reconnect:{}",rpcInvoker);
                                rpcInvoker.connect();
                            }
                        });
                    }else if(rpcInvoker.isRemove()){
                        aliveRpcInvokerMap.remove(rpcInvoker);
                        deadRpcInvokerMap.put(url,rpcInvoker);
                        log.error("RpcInvokerHolder deadRpcInvokerMap:{}",deadRpcInvokerMap);
                    }
                }
            }
        };
        TimerManager.registerTimerTask(task, DELAY, INTEVAL_PERIOD);
    }

    public RpcInvokerHolder(ConsumerConfig consumerConfig) {
        this.consumerConfig = consumerConfig;
    }

    public synchronized void refresh(ProviderGroup providerGroup) {
        log.debug("rpc client poll refresh provider group:{}", providerGroup);
        List<ProviderInfo> providerInfos = providerGroup.getProviderInfos();
        List<String> newUrls = new CopyOnWriteArrayList<String>();
        removeRpcInvoker(providerInfos);
        for (ProviderInfo providerInfo : providerInfos) {
            RpcInvoker rpcInvoker = aliveRpcInvokerMap.get(providerInfo.getOriginUrl());
            if (rpcInvoker == null) {
                rpcInvoker = new RpcInvoker(providerInfo, consumerConfig);
                rpcInvoker.connect();
            }else if(!rpcInvoker.isActive()) {
                rpcInvoker.connect();
            }
            if (!rpcInvoker.isActive()){
                continue;
            }
            newUrls.add(providerInfo.getOriginUrl());
            aliveRpcInvokerMap.put(providerInfo.getOriginUrl(), rpcInvoker);
        }
        this.urls = newUrls;
        this.providerGroup = providerGroup;
        log.info("rpc client poll refresh after:{},{},{}", this.providerGroup, this.urls);
    }

    private void removeRpcInvoker(List<ProviderInfo> providerInfos) {
        List<String> activeUrls = new ArrayList<>();
        for (ProviderInfo providerInfo : providerInfos) {
            deadRpcInvokerMap.remove(providerInfo.getOriginUrl());
            activeUrls.add(providerInfo.getOriginUrl());
        }
        if (CollectionUtils.isNotEmpty(urls)) {
            for (String url : urls) {
                if (!activeUrls.contains(url)) {
                    aliveRpcInvokerMap.remove(url);
                }
            }
        }
    }

    public List<RpcInvoker> listRpckInvoker() {
        if (CollectionUtils.isEmpty(urls)) {
            throw new RPCRuntimeException(String.format("%s no active provider", consumerConfig.getInterfaceName()));
        }
        List<RpcInvoker> rpcInvokers = new ArrayList<>();
        for (String url : urls) {
            RpcInvoker rpcInvoker = aliveRpcInvokerMap.get(url);
            if (rpcInvoker != null && rpcInvoker.isActive()) {
                rpcInvokers.add(rpcInvoker);
            }
        }
        if (CollectionUtils.isEmpty(rpcInvokers)) {
            throw new RPCRuntimeException(String.format("%s no active provider", consumerConfig.getInterfaceName()));
        }
        return rpcInvokers;
    }
}
