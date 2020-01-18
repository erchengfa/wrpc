package com.github.wang.wrpc.context.config;

import com.github.wang.wrpc.common.utils.ClassUtils;
import com.github.wang.wrpc.common.utils.StringUtils;
import com.github.wang.wrpc.context.annotation.WRpcMethod;
import com.github.wang.wrpc.context.provider.ProviderApplicationContext;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : wang
 * @date : 2020/1/5
 */
@Accessors
@Getter
public class ProviderConfig<T> {

    /**
     * 权重
     */
    protected int weight = RpcDefaultConfig.PROVIDER_WEIGHT;

    /**
     * 接口名
     */
    protected Class<?> interfaceClass ;
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

    private List<Method> methods;
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
        setMethods();
        providerApplicationContext = new ProviderApplicationContext(this);
        providerApplicationContext.init();
        providerApplicationContext.start();
    }

    private void setMethods() {
        List<Method> allMethods = ClassUtils.getAllMethods(interfaceClass);
        this.methods = new ArrayList<>();
        for (Method method:allMethods){
            WRpcMethod wRpcMethod = method.getAnnotation(WRpcMethod.class);
            if (wRpcMethod != null){
                if (wRpcMethod.exclude()){
                    continue;
                }
            }
            this.methods.add(method);
        }
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

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public void setInterfaceClass(Class interfaceClass){
        this.interfaceClass = interfaceClass;
        this.interfaceName = interfaceClass.getName();
    }

    public void setServiceBean(T serviceBean) {
        this.serviceBean = serviceBean;
    }
}
