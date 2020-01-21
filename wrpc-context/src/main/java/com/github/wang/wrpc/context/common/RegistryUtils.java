package com.github.wang.wrpc.context.common;

import com.github.wang.wrpc.common.utils.CommonUtils;
import com.github.wang.wrpc.common.utils.StringUtils;
import com.github.wang.wrpc.context.config.ConsumerConfig;
import com.github.wang.wrpc.context.config.ProviderConfig;
import com.github.wang.wrpc.context.config.ServerConfig;
import com.github.wang.wrpc.context.registry.ProviderInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author : wang
 * @date : 2020/1/6
 */
public class RegistryUtils {

    public static String buildProviderPath(String serviceName) {
        return "/wrpc/" + serviceName + "/providers";

    }
    public static String buildConsumerPath( String serviceName) {
        return "/wrpc/" + serviceName + "/consumers";
    }
    public static List<ProviderInfo> convertProviderInfos(ProviderConfig providerConfig) {
        List<ServerConfig> servers = providerConfig.getServers();
        List<ProviderInfo> providerInfos = new ArrayList<>(servers.size());
        if (servers != null && !servers.isEmpty()) {
            for (ServerConfig server : servers) {
                ProviderInfo providerInfo = new ProviderInfo();
                providerInfo.setHost(server.getHost());
                providerInfo.setPort(server.getPort());
                providerInfo.setWeight(server.getWeight());
                providerInfo.setProtocol(server.getProtocol());
                providerInfo.setAppName(providerConfig.getAppName());
                StringBuilder sb = new StringBuilder(200);
                sb.append(providerInfo.getProtocol()).append("://")//
                        .append(providerInfo.getHost()).append(":")
                        .append(providerInfo.getPort()).append("?")//
                        .append(RpcConstants.CONFIG_KEY_APP_NAME).append("=")//
                        .append(providerInfo.getAppName());
                providerInfo.setUrl(sb.toString());
                providerInfos.add(providerInfo);
            }
        }
        return providerInfos;
    }
    /**
     * 转换 map to url pair
     *
     * @param map 属性
     */
    private static String convertMap2Pair(Map<String, String> map) {

        if (CommonUtils.isEmpty(map)) {
            return StringUtils.EMPTY;
        }

        StringBuilder sb = new StringBuilder(128);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(getKeyPairs(entry.getKey(), entry.getValue()));
        }

        return sb.toString();
    }

    /**
     * Gets key pairs.
     *
     * @param key   the key
     * @param value the value
     * @return the key pairs
     */
    public static String getKeyPairs(String key, Object value) {
        if (value != null) {
            return "&" + key + "=" + value.toString();
        } else {
            return "";
        }
    }

    public static String convertConsumerToUrl(ConsumerConfig consumerConfig) {
        StringBuilder sb = new StringBuilder(200);
        String host = SystemInfo.getLocalHost();
        sb.append(consumerConfig.getProtocol()).append("://").append(host).append("?")//
                .append(RpcConstants.CONFIG_KEY_APP_NAME).append("=").append(consumerConfig.getAppName())
                .append("&time=").append(System.currentTimeMillis());
        return sb.toString();
    }

}
