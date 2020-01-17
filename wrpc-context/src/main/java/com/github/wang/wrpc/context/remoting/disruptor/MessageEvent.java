package com.github.wang.wrpc.context.remoting.disruptor;

import com.github.wang.wrpc.context.common.Request;
import io.netty.channel.Channel;
import lombok.Data;


/**
 * @author : wang
 * @date : 2020/1/10
 */
@Data
public class MessageEvent {
    private long sequence;
    private Channel channel;
    private Request request;
    public MessageEvent(){

    }
    public MessageEvent(Channel channel,Request request){
        this.channel = channel;
        this.request = request;
    }

    public void setSequence(long sequence){
        this.sequence = sequence;
    }

}
