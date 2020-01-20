package com.github.wang.wrpc.context.config;

import com.github.wang.wrpc.context.common.SerializationConstants;

/**
 * @author : wang
 * @date : 2020/1/8
 */
public class RpcDefaultConfig {

    /**
     * 默认协议
     */
    public static final  String PROTOCOL = "wang";

    /**
     * 默认端口
     */
    public static final  Integer PORT = 28000;

    /**
     * 默认序列化
     */
    public static final String SERIALIZATION = "kryo";

    /**
     * 默认序列化
     */
    public static final byte SERIALIZATION_ID = SerializationConstants.KRYO_SERIALIZATION_ID;

    /**
     * 默认注册中心
     */
    public static final String REGISTRY = "zookeeper";

    /**
     * 默认负载均衡算法
     */
    public static final String LOADBALANCE = "random";


    /**
     * 默认连注册中心的超时时间
     */
    public static final int REGISTRY_CONNECT_TIMEOUT = 20000;

    /**
     * 注册中心调用超时时间
     */
    public static final int REGISTRY_INVOKE_TIMEOUT = 10000;


    /**
     * 默认服务端权重
     */
    public static final int  PROVIDER_WEIGHT = 100;


    /**
     * 默认失败重试次数
     */
    public static final int  CONSUMER_RETRIES = 2;


    /**
     *消息处理的线程数
     */
    public static final int MESSAGE_HANDLE_THREAD_SIZE = 8;

    /**
     * disruptor ring buffer 大小
     */
    public static final int RING_BUFFER_SIZE = 1024 * 1024;


    /**
     * 默认负载均衡算法
     */
    public static final String DEFAULT_CLUSTER = "failover";


    /**
     * 服务调用超时时间
     */
    public static final long SERVICE_INVOKE_TIMEOUT = 10000L;


}
