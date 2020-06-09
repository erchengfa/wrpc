package com.github.wang.wrpc.context.codec;

import com.github.wang.wrpc.common.ext.Spi;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.List;


@Spi
public interface Codec {

    /**
     * 编码
     * @param channelHandlerContext
     * @param o
     * @param byteBuf
     * @throws IOException
     */
    void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws IOException;

    /**
     * 解码
     * @param channelHandlerContext
     * @param byteBuf
     * @param list
     * @return
     * @throws IOException
     */
    Object decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws IOException;
}
