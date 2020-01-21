package com.github.wang.wrpc.context.registry;

import lombok.Data;

import java.util.List;

/**
 * @author : wang
 * @date : 2020/1/6
 */
@Data
public class ProviderGroup {



    /**
     * 服务名称
     */
    protected String serviceName;

    /**
     * 服务分组下服务端列表（缓存的是List，方便快速读取）
     */
    protected List<ProviderInfo> providerInfos;

}
