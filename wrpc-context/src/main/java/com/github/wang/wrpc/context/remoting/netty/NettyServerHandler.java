package com.github.wang.wrpc.context.remoting.netty;

import com.github.wang.wrpc.context.common.Request;
import com.github.wang.wrpc.context.common.Response;
import com.github.wang.wrpc.context.remoting.disruptor.EventDisruptor;
import com.github.wang.wrpc.context.remoting.disruptor.MessageEvent;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : wang
 * @date : 2019/12/24
 */
@Slf4j
@io.netty.channel.ChannelHandler.Sharable
public class NettyServerHandler extends ChannelDuplexHandler {
    private EventDisruptor eventDisruptor;
    public NettyServerHandler(EventDisruptor eventDisruptor) {
        this.eventDisruptor = eventDisruptor;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("NettyServerHandler channelActive :{}",ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("NettyServerHandler channelActive :{}",ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("server receive msg:{}", msg);
        }
        if (msg instanceof Request){
            Request request = (Request) msg;
            if (request.isHeartbeat()){
                ctx.channel().writeAndFlush(handleHeartbeat(request));
            }else {
                eventDisruptor.publishEvent(new MessageEvent(ctx.channel(),request));
            }
        }

    }

    public Response handleHeartbeat(Request request){
        Response response = new Response(request.getId());
        response.setHeartbeat(true);
        return response;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            try {
                log.info("IdleStateEvent triggered, close channel " + ctx.channel());
                ctx.channel().close();
            } finally {

            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        log.debug("NettyServerHandler channelActive :{}",cause);
    }

}
