package com.github.wang.wrpc.context.common;

import com.github.wang.wrpc.common.utils.CommonUtils;
import com.github.wang.wrpc.common.utils.StringUtils;
import com.github.wang.wrpc.context.config.ConsumerConfig;
import com.github.wang.wrpc.context.config.ProviderConfig;
import com.github.wang.wrpc.context.config.ServerConfig;

import java.util.ArrayList;
import java.util.HashMap;
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
        return  "/wrpc/" + serviceName + "/consumers";
    }

    public static String buildConfigPath(String rootPath, String serviceName) {
        return  "/wrpc/" + serviceName + "/configs";
    }



    /**
     * Convert provider to url.
     *
     * @param providerConfig the ProviderConfig
     * @return the url list
     */
    public static List<String> convertProviderToUrls(ProviderConfig providerConfig) {
        @SuppressWarnings("unchecked")
        List<ServerConfig> servers = providerConfig.getServers();
        if (servers != null && !servers.isEmpty()) {
            List<String> urls = new ArrayList<String>();
            for (ServerConfig server : servers) {
                StringBuilder sb = new StringBuilder(200);
                String host = server.getHost();
                Integer port = server.getPort();
                Map<String, String> metaData = convertProviderToMap(providerConfig, server);
                //noinspection unchecked
                sb.append(server.getProtocol()).append("://").append(host).append(":")
                        .append(port).append("?").append("applicationName=").append(//
                                providerConfig.getApplicationName()).append(convertMap2Pair(metaData));
                urls.add(sb.toString());
            }
            return urls;
        }
        return null;
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


    public static Map<String, String> convertProviderToMap(ProviderConfig providerConfig, ServerConfig server) {
        Map<String, String> metaData = new HashMap<String, String>();
        metaData.put(RpcConstants.CONFIG_KEY_WEIGHT, String.valueOf(providerConfig.getWeight()));

        return metaData;
    }

    public static String convertConsumerToUrl(ConsumerConfig consumerConfig) {
        StringBuilder sb = new StringBuilder(200);
        String host = SystemInfo.getLocalHost();
        sb.append(consumerConfig.getProtocol()).append("://").append(host).append("?")//
                .append(RegistryUtils.getKeyPairs(RpcConstants.CONFIG_KEY_APP_NAME, consumerConfig.getApplicationName()))
                .append(getKeyPairs(RpcConstants.CONFIG_KEY_SERIALIZATION,
                        consumerConfig.getSerialization()));
        return sb.toString();
    }

}
