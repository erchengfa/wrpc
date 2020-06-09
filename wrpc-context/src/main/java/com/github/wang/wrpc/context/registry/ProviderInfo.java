package com.github.wang.wrpc.context.registry;

import com.github.wang.wrpc.common.utils.JSONUtils;
import com.github.wang.wrpc.context.config.RpcDefaultConfig;
import lombok.Data;

import java.io.Serializable;


@Data
public class ProviderInfo implements Serializable {

    private static final long serialVersionUID = -6438690329875954051L;

    private String appName;

    private String url;

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
    private Integer port;

    /**
     * 权重
     */
    private  volatile int weight = RpcDefaultConfig.PROVIDER_WEIGHT;

    public byte[] convertData(){
        ProviderInfo providerInfo = new ProviderInfo();
        providerInfo.setWeight(this.weight);
        return JSONUtils.toJSONString(providerInfo).getBytes();
    }


}
