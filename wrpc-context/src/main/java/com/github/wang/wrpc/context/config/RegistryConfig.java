package com.github.wang.wrpc.context.config;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Data
public class RegistryConfig {

    /**
     * 协议
     */
    private String protocol =  RpcDefaultConfig.REGISTRY;

    /**
     * 指定注册中心的地址
     */
    private String address;

    /**
     * 调用注册中心超时时间
     */
    private int timeout = RpcDefaultConfig.REGISTRY_INVOKE_TIMEOUT;

    /**
     * 连接注册中心超时时间
     */
    private int connectTimeout = RpcDefaultConfig.REGISTRY_CONNECT_TIMEOUT;

    /**
     * The Parameters. 自定义参数
     */
    protected Map<String, String> parameters;


    /**
     * Gets parameters.
     *
     * @return the parameters
     */
    public Map<String, String> getParameters() {
        return parameters;
    }

    /**
     * Sets parameters.
     *
     * @param parameters the parameters
     * @return the RegistryConfig
     */
    public RegistryConfig setParameters(Map<String, String> parameters) {
        if (this.parameters == null) {
            this.parameters = new ConcurrentHashMap<String, String>();
            this.parameters.putAll(parameters);
        }
        return this;
    }

    /**
     * Sets parameter.
     *
     * @param key   the key
     * @param value the value
     * @return the RegistryConfig
     */
    public RegistryConfig setParameter(String key, String value) {
        if (parameters == null) {
            parameters = new ConcurrentHashMap<String, String>();
        }
        parameters.put(key, value);
        return this;
    }

    /**
     * Gets parameter.
     *
     * @param key the key
     * @return the value
     */
    public String getParameter(String key) {
        return parameters == null ? null : parameters.get(key);
    }

}
