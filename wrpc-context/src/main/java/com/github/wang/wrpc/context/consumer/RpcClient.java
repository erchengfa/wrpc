package com.github.wang.wrpc.context.consumer;


public interface RpcClient {

    void connect() throws Throwable;

    void send(Object message);

    boolean isActive();

    boolean isDead();
}
