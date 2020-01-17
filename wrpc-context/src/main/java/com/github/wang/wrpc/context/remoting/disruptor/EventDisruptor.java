package com.github.wang.wrpc.context.remoting.disruptor;

import com.github.wang.wrpc.common.utils.ThreadPoolUtils;
import com.github.wang.wrpc.context.remoting.handler.MessageHandler;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author : wang
 * @date : 2020/1/10
 */
@Slf4j
public class EventDisruptor {

    private Disruptor<MessageEvent> disruptor;

    private MessageHandler serviceHandler;

    public EventDisruptor(int messageHandleThreadSize,int ringBufferSize){
        ThreadPoolExecutor threadPoolExecutor = ThreadPoolUtils.newFixedThreadPool(messageHandleThreadSize);
        disruptor = new Disruptor<MessageEvent>(
                new EventFactory<MessageEvent>() {
                    public MessageEvent newInstance() {
                        return new MessageEvent();
                    }
                },
                ringBufferSize,
                threadPoolExecutor,
                ProducerType.SINGLE,
                new BlockingWaitStrategy());
        disruptor.handleEventsWith(new EventHandler<MessageEvent>(){
            @Override
            public void onEvent(MessageEvent event, long sequence, boolean endOfBatch) throws Exception {
                log.debug("EventDisruptor event receive  msg:{}",event);
                serviceHandler.handle(event);
            }
        });

    }

    public void publishEvent(MessageEvent messageEvent){
            disruptor.publishEvent(new EventTranslator<MessageEvent>() {
                @Override
                public void translateTo(MessageEvent event, long sequence) {
                    event.setSequence(sequence);
                    event.setChannel(messageEvent.getChannel());
                    event.setRequest(messageEvent.getRequest());
                }
            });
    }

    public void registerServiceHanler(MessageHandler serviceHandler) {
        this.serviceHandler = serviceHandler;
    }

    public void start(){
       // 启动disruptor
        disruptor.start();
    }

}
