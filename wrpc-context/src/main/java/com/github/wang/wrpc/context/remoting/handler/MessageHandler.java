package com.github.wang.wrpc.context.remoting.handler;

import com.github.wang.wrpc.context.remoting.disruptor.MessageEvent;


public interface MessageHandler {
    void handle(MessageEvent messageEvent);
}
