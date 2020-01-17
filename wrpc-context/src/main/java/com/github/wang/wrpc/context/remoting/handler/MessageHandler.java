package com.github.wang.wrpc.context.remoting.handler;

import com.github.wang.wrpc.context.remoting.disruptor.MessageEvent;

/**
 * @author : wang
 * @date : 2020/1/10
 */
public interface MessageHandler {
    void handle(MessageEvent messageEvent);
}
