package com.github.wang.wrpc.context.consumer;

import com.github.wang.wrpc.common.ext.ServiceLoader;
import com.github.wang.wrpc.common.ext.ServiceLoaderFactory;
import com.github.wang.wrpc.context.cluster.Cluster;
import com.github.wang.wrpc.context.cluster.ClusterConfig;
import com.github.wang.wrpc.context.cluster.LoadBalance;
import com.github.wang.wrpc.context.config.ConsumerConfig;
import com.github.wang.wrpc.context.config.ProviderGroup;
import com.github.wang.wrpc.context.config.RegistryConfig;
import com.github.wang.wrpc.context.ext.Registry;
import com.github.wang.wrpc.context.observer.ProviderObserver;
import com.github.wang.wrpc.context.proxy.InvocationProxy;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : wang
 * @date : 2020/1/10
 */
@Slf4j
public class ConsumeApplicationContext{

    private ConsumerConfig consumerConfig;

    private Registry registry;

    private RpcInvokerHolder rpcInvokerHolder;

    private ProviderObserver providerObserver;

    private InvocationProxy invocationProxy;

    private LoadBalance loadBalance;

    private Cluster cluster;

    public ConsumeApplicationContext(ConsumerConfig consumerConfig){
        this.consumerConfig = consumerConfig;
        ServiceLoader<Registry> registryServiceLoader = ServiceLoaderFactory.getExtensionLoader(Registry.class);
        RegistryConfig registryConfig = consumerConfig.getRegistry();
        rpcInvokerHolder = new RpcInvokerHolder(consumerConfig);
        this.registry = registryServiceLoader.getInstance(registryConfig.getProtocol(), //
                new Class[]{RegistryConfig.class}, new RegistryConfig[]{registryConfig});
        providerObserver = new ConsumerApplicationProviderObserver(rpcInvokerHolder);
        this.registry.registerObserver(providerObserver);
        this.registry.start();
        ProviderGroup providerGroup = this.registry.subscribe(consumerConfig);
        rpcInvokerHolder.refresh(providerGroup);
    }


    public Object refer(){
        //1、构建invoker

        ServiceLoader<LoadBalance> loadBalanceServiceLoader = ServiceLoaderFactory.getExtensionLoader(LoadBalance.class);
        this.loadBalance = loadBalanceServiceLoader.getInstance(consumerConfig.getLoadBlance());

        ServiceLoader<Cluster> clusterLoader = ServiceLoaderFactory.getExtensionLoader(Cluster.class);
        ClusterConfig clusterConfig = new ClusterConfig(this.rpcInvokerHolder,loadBalance,consumerConfig);

        this.cluster = clusterLoader.getInstance(consumerConfig.getCluster(), //
                new Class[]{ClusterConfig.class}, new ClusterConfig[]{clusterConfig});

        invocationProxy = new InvocationProxy(cluster);

        return invocationProxy.create(consumerConfig.getInterfaceClass(),consumerConfig.getServiceVersion());
    }

}
