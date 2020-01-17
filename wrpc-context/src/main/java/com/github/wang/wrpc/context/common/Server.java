package com.github.wang.wrpc.context.common;

import com.github.wang.wrpc.context.remoting.handler.MessageHandler;

/**
 * @author : wang
 * @date : 2020/1/5
 */
public interface Server {

    /**
     * 开启服务
     */
    public void open();

    /**
     * 注册服务处理器
     * @param serviceHandler
     */
    public void registerServiceHanler(MessageHandler serviceHandler);
}
