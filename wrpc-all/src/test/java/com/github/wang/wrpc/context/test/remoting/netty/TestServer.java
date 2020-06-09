package com.github.wang.wrpc.context.test.remoting.netty;


import com.github.wang.wrpc.context.common.Server;
import com.github.wang.wrpc.context.config.ServerConfig;
import com.github.wang.wrpc.context.remoting.netty.NettyServer;

import java.io.IOException;


public class TestServer {


    public static void main(String[] args) throws IOException {

        ServerConfig serverConfig = new ServerConfig();
        Server server = new NettyServer(serverConfig);

        System.in.read();
    }

}
