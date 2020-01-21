package com.github.wang.wrpc.context.provider;

import com.github.wang.wrpc.common.exception.RPCRuntimeException;
import com.github.wang.wrpc.common.ext.ServiceLoader;
import com.github.wang.wrpc.common.ext.ServiceLoaderFactory;
import com.github.wang.wrpc.context.common.Server;
import com.github.wang.wrpc.context.config.ProviderConfig;
import com.github.wang.wrpc.context.config.RegistryConfig;
import com.github.wang.wrpc.context.config.ServerConfig;
import com.github.wang.wrpc.context.registry.Registry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author : wang
 * @date : 2020/1/10
 */
@Slf4j
public class ProviderApplicationContext{

    private ProviderConfig providerConfig;


    private Registry registry;

    public ProviderApplicationContext(ProviderConfig providerConfig){
        this.providerConfig = providerConfig;
    }

    public void init() {
        //创建服务的处理器，并注册服务实例
        ServiceLoader<Registry> registryServiceLoader = ServiceLoaderFactory.getExtensionLoader(Registry.class);
        RegistryConfig registryConfig = providerConfig.getRegistry();
        this.registry = registryServiceLoader.getInstance(registryConfig.getProtocol(), new Class[]{RegistryConfig.class}, new RegistryConfig[]{registryConfig});
    }

    public void start(){
        Object serviceBean = providerConfig.getServiceBean();
        List<ServerConfig> serverConfigs = providerConfig.getServers();
        if (CollectionUtils.isEmpty(serverConfigs)){
            throw new RPCRuntimeException("server is empty");
        }
        for (ServerConfig serverConfig:serverConfigs){
            Server server = serverConfig.getServer();
            server.registerServiceBean(providerConfig.getServiceName(),serviceBean);
        }
        //注册服务
        registry.start();
        registry.register(providerConfig);
    }
}
