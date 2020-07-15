package com.github.wang.wrpc.context.consumer;


public interface RpcClient {

    /**
     * 连接
     * @throws Throwable
     */
    void connect() throws Throwable;

    /**
     * 发送消息
     * @param message
     */
    void send(Object message);

    /**
     * 是否活跃
     * @return
     */
    boolean isActive();
}
