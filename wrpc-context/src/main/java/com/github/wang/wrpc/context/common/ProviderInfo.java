package com.github.wang.wrpc.context.common;

import com.github.wang.wrpc.context.config.RpcDefaultConfig;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : wang
 * @date : 2020/1/6
 */
@Data
public class ProviderInfo implements Serializable {

    private static final long serialVersionUID = -6438690329875954051L;

    /**
     * 原始地址
     */
    private transient String originUrl;

    /**
     * 协议
     */
    private String protocol;
    /**
     * The host.
     */
    private String host;

    /**
     * The Port.
     */
    private int port;

    private String serialization;

    /**
     * 权重
     */
    private transient volatile int weight = RpcDefaultConfig.PROVIDER_WEIGHT;

}
