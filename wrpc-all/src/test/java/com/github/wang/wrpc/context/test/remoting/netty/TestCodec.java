package com.github.wang.wrpc.context.test.remoting.netty;


import com.github.wang.wrpc.context.common.Request;
import com.github.wang.wrpc.controller.remoting.netty.codec.WangCodec;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : wang
 * @date : 2019/12/27
 */
public class TestCodec {

    @Test
    public void testWangCodec() throws Throwable {
        WangCodec wangCodec = new WangCodec();
        Request request = new Request();
        request.setHeartbeat(false);
        request.setBody("hello");
        ByteBuf out = Unpooled.buffer();
        wangCodec.encode(null,request,out);
        List<Object> list = new ArrayList<>();
        wangCodec.decode(null,out,list);
        System.out.println(list);
    }

}
