package com.github.wang.wrpc.context.remoting.netty;


import com.github.wang.wrpc.context.common.Request;
import com.github.wang.wrpc.context.common.Response;
import com.github.wang.wrpc.context.remoting.DefaultFuture;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : wang
 * @date : 2019/12/24
 */
@Slf4j
@io.netty.channel.ChannelHandler.Sharable
public class NettyClientHandler extends ChannelDuplexHandler {

    private NettyClient nettyClient;

    public NettyClientHandler(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channelActive :{}",nettyClient.getProviderInfo());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("channelInactive :{}",nettyClient.getProviderInfo());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug("NettyClientHandler receive msg:{}", msg);
        if (msg instanceof Response) {
            Response res = (Response) msg;
            if (res.isHeartbeat()){
                nettyClient.receiveHeartbeat();
            }else{
                DefaultFuture.received(res);
            }
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // send heartbeat when read idle.
        if (evt instanceof IdleStateEvent) {
            try {
                Request req = new Request();
                req.setHeartbeat(true);
                ctx.channel().writeAndFlush(req);
            } finally {
                log.debug("wrpc send heartbeat message:{}",nettyClient.getProviderInfo());
                nettyClient.handleHeartBeatException();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        log.debug("exceptionCaught :{}",nettyClient.getProviderInfo());
    }


}
