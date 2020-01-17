package com.github.wang.wrpc.context.test.remoting.netty;


import com.github.wang.wrpc.context.common.Request;
import com.github.wang.wrpc.context.common.Response;
import com.github.wang.wrpc.context.common.Server;
import com.github.wang.wrpc.context.config.ServerConfig;
import com.github.wang.wrpc.context.remoting.disruptor.MessageEvent;
import com.github.wang.wrpc.context.remoting.handler.MessageHandler;
import com.github.wang.wrpc.context.remoting.netty.NettyServer;

import java.io.IOException;

/**
 * @author : wang
 * @date : 2019/12/29
 */
public class TestServer {


    public static void main(String[] args) throws IOException {

        ServerConfig serverConfig = new ServerConfig();
        Server server = new NettyServer(serverConfig);
        server.registerServiceHanler(new MessageHandler() {
            @Override
            public void handle(MessageEvent messageEvent) {
                Request request = messageEvent.getRequest();
                Response response = new Response(request.getId());
                response.setHeartbeat(false);
                response.setBody("hello");
                messageEvent.getChannel().writeAndFlush(response);
            }
        });

        server.open();

        System.in.read();
    }

}
