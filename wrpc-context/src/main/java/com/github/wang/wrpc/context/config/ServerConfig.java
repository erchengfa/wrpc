package com.github.wang.wrpc.context.config;

import com.github.wang.wrpc.common.utils.NetUtils;
import com.github.wang.wrpc.context.common.Server;
import com.github.wang.wrpc.context.remoting.netty.NettyServer;
import lombok.Data;


/**
 * @author : wang
 * @date : 2020/1/5
 */
@Data
public class ServerConfig {
    /**
     * 配置名称
     */
    private String protocol = RpcDefaultConfig.PROTOCOL;

    /**
     * 权重
     */
    protected int weight = RpcDefaultConfig.PROVIDER_WEIGHT;

    /**
     * 实际监听IP，与网卡对应
     */
    private String host = NetUtils.getLocalIpv4();

    /**
     * 监听端口
     */
    private int port = RpcDefaultConfig.PORT;

    /**
     * 序列化
     */
    private String serialization = RpcDefaultConfig.SERIALIZATION;

    /**
     * 消息处理的线程数
     */
    private int messageHandleThreadSize  = RpcDefaultConfig.MESSAGE_HANDLE_THREAD_SIZE;

    /**
     * disruptor ring buffer 大小
     */
    private int ringBufferSize = RpcDefaultConfig.RING_BUFFER_SIZE;

    private volatile Server server;

    public Server getServer(){
        if (null == server){
            synchronized (this){
                if (null == server){
                    server = new NettyServer(this);
                }
            }
        }

        return server;
    }



}
