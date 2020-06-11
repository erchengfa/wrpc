package com.github.wang.wrpc.nacos;


import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.github.wang.wrpc.common.utils.JSONUtils;
import com.github.wang.wrpc.context.config.ConsumerConfig;
import com.github.wang.wrpc.context.config.ProviderConfig;
import com.github.wang.wrpc.context.config.RegistryConfig;
import com.github.wang.wrpc.context.config.ServerConfig;
import com.github.wang.wrpc.context.observer.ProviderObserver;
import com.github.wang.wrpc.context.registry.ProviderGroup;
import com.github.wang.wrpc.context.registry.ProviderInfo;
import com.github.wang.wrpc.context.registry.Registry;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class NacosRegistry extends Registry {

    private NamingService namingService;

    private ConcurrentHashMap<String, ProviderObserver> observers = new ConcurrentHashMap<>();

    protected NacosRegistry(RegistryConfig registryConfig) {
        super(registryConfig);
        String address = registryConfig.getAddress();
        try {
            namingService = NamingFactory.createNamingService(address);
        } catch (NacosException e) {
            log.error("create naming service error:{}", e);
        }
    }

    @Override
    public boolean start() {
        return true;
    }

    @Override
    public void register(ProviderConfig config) {
        List<ServerConfig> servers = config.getServers();
        for (ServerConfig serverConfig : servers) {
            try {
                Map<String, String> metadata = new HashMap<>();
                metadata.put("protocol",serverConfig.getProtocol());
                Instance instance = new Instance();
                instance.setIp(serverConfig.getHost());
                instance.setPort(serverConfig.getPort());
                instance.setWeight(serverConfig.getWeight());
                instance.setClusterName(config.getAppName());
                instance.setMetadata(metadata);
                namingService.registerInstance(config.getServiceName(), instance);
            } catch (NacosException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ProviderGroup subscribe(ConsumerConfig config) {
        try {
            namingService.subscribe(config.getServiceName(), event -> {
                System.out.println(JSONUtils.toJSONString(event));
                if (event instanceof NamingEvent) {
                    NamingEvent namingEvent = ((NamingEvent) event);
                    List<Instance> instances = namingEvent.getInstances();
                    List<ProviderInfo> providerInfos = new ArrayList<>();
                    for (Instance instance : instances){
                        ProviderInfo providerInfo = new ProviderInfo();
                        providerInfo.setHost(instance.getIp());
                        providerInfo.setPort(instance.getPort());
                        providerInfo.setWeight((int)instance.getWeight());
                        Map<String, String> metadata = instance.getMetadata();
                        providerInfo.setProtocol(metadata.get("protocol"));
                        providerInfo.setUrl(instance.getIp()+":"+instance.getPort());
                        providerInfos.add(providerInfo);
                    }
                    ProviderGroup providerGroup = new ProviderGroup();
                    String serviceName = namingEvent.getServiceName();
                    serviceName = serviceName.substring(serviceName.indexOf("@@") + 2, serviceName.length());
                    providerGroup.setServiceName(serviceName);
                    providerGroup.setProviderInfos(providerInfos);
                    notifyObserver(providerGroup);
                }
            });
            //namingService.getAllInstances(config.getServiceName());
        } catch (NacosException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void notifyObserver(ProviderGroup providerGroup){
        observers.get(providerGroup.getServiceName()).update(providerGroup);
    }

    @Override
    public void registerObserver(ProviderObserver providerObserver) {
        observers.put(providerObserver.getServiceName(), providerObserver);
    }
}
