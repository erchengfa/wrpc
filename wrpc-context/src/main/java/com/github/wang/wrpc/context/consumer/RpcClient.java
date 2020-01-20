package com.github.wang.wrpc.context.consumer;

/**
 * @author : wang
 * @date : 2020/1/12
 */
public interface RpcClient {

    void connect() throws Throwable;

    void send(Object message);

    boolean isActive();

    boolean isDead();
}
