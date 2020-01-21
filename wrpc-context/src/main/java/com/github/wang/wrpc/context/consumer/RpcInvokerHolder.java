package com.github.wang.wrpc.context.consumer;

import com.github.wang.wrpc.common.exception.RPCRuntimeException;
import com.github.wang.wrpc.common.utils.ThreadPoolUtils;
import com.github.wang.wrpc.context.common.Invocation;
import com.github.wang.wrpc.context.config.ConsumerConfig;
import com.github.wang.wrpc.context.registry.ProviderGroup;
import com.github.wang.wrpc.context.registry.ProviderInfo;
import com.github.wang.wrpc.context.timer.TimerManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author : wang
 * @date : 2020/1/11
 */
@Slf4j
public class RpcInvokerHolder {

    private static Map<String, RpcInvoker> aliveRpcInvokerMap = new ConcurrentHashMap<>();

    private static ThreadPoolExecutor rpcInkokerPoolExecutor = ThreadPoolUtils.newFixedThreadPool(8,new LinkedBlockingQueue<>(10000));

    private List<String> urls = new ArrayList<>();

    private ProviderGroup providerGroup;

    private ConsumeApplicationContext context;

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
                    if(rpcInvoker.isDead()){
                        log.error("RpcInvokerHolder dead rpc invoker:{}",rpcInvoker);
                    }else if (!rpcInvoker.isActive()){
                        rpcInkokerPoolExecutor.submit(new Runnable() {
                            @Override
                            public void run() {
                                log.debug("wrpc aliveRpcInvokerMap check reconnect:{}",rpcInvoker);
                                rpcInvoker.connect();
                            }
                        });
                    }
                }
            }
        };
        TimerManager.registerTimerTask(task, DELAY, INTEVAL_PERIOD);
    }

    public RpcInvokerHolder(ConsumeApplicationContext context) {
        this.context = context;
    }

    public synchronized void refresh(ProviderGroup providerGroup) {
        log.debug("rpc client poll refresh provider group:{}", providerGroup);
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
            rpcInkokerPoolExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    finalRpcInvoker.connect();
                    aliveRpcInvokerMap.put(providerInfo.getUrl(), finalRpcInvoker);
                }
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
