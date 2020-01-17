package com.github.wang.wrpc.context.registry;

import com.github.wang.wrpc.context.registry.ProviderInfo;
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
    protected String interfaceName;

    /**
     * 服务分组下服务端列表（缓存的是List，方便快速读取）
     */
    protected List<ProviderInfo> providerInfos;

}
