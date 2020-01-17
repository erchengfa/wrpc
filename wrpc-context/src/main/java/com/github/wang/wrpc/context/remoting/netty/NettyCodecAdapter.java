package com.github.wang.wrpc.context.remoting.netty;

import com.github.wang.wrpc.context.ext.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.List;

/**
 * @author : wang
 * @date : 2019/12/25
 */
public class NettyCodecAdapter {

    private final Codec codec;

    public NettyCodecAdapter(Codec codec) {
        this.codec = codec;
    }

    public ChannelHandler getEncoder() {
        return new InternalEncoder();
    }

    public ChannelHandler getDecoder() {
        return new InternalDecoder();
    }

    private class InternalEncoder extends MessageToByteEncoder {

        @Override
        protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
            codec.encode(ctx,msg,out);
        }
    }

    private class InternalDecoder extends ByteToMessageDecoder {

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf input, List<Object> out) throws Exception {
            codec.decode(ctx,input,out);
        }
    }
}
