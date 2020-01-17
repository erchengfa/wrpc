package com.github.wang.wrpc.context.config;

import com.github.wang.wrpc.common.utils.StringUtils;
import com.github.wang.wrpc.context.provider.ProviderApplicationContext;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : wang
 * @date : 2020/1/5
 */
@Data
@Accessors
public class ProviderConfig<T> {

    /**
     * 权重
     */
    protected int weight = RpcDefaultConfig.PROVIDER_WEIGHT;


    /**
     * 服务接口名
     */
    protected String interfaceName;
    /**
     * 服务实例
     */
    protected transient T serviceBean;

    /**
     * 服务的版本号
     */
    protected transient String serviceVersion;


    /**
     * 应用名
     */
    protected String applicationName;

    /**
     * 配置的服务列表
     */
    protected List<ServerConfig> servers;

    /**
     * 注册中心配置，可配置多个
     */
    protected RegistryConfig  registry;


    private ProviderApplicationContext providerApplicationContext;
    public ProviderConfig(){

    }

    /**
     * 发布服务
     */
    public synchronized void export() {
        providerApplicationContext = new ProviderApplicationContext(this);
        providerApplicationContext.init();
        providerApplicationContext.start();
    }

    public void setServer(ServerConfig serverConfig){
        if (servers == null){
            servers = new ArrayList<>();
        }
        servers.add(serverConfig);
    }


    public void setRegistry(RegistryConfig registryConfig){
        registry = registryConfig;
    }

    public String getServiceName(){
        if (StringUtils.isBlank(this.serviceVersion)){
            return interfaceName;
        }
        return interfaceName + "-" + serviceVersion;
    }



}
