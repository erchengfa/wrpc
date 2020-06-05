package com.github.wang.wrpc.context.remoting.disruptor;

import com.github.wang.wrpc.common.utils.ThreadPoolUtils;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author : wang
 * @date : 2020/1/10
 */
@Slf4j
public class EventDisruptor {

    private Disruptor<MessageEvent> disruptor;

    private ConcurrentHashMap<String, Object> serviceBeanMap = new ConcurrentHashMap<>();

    public EventDisruptor(int messageHandleThreadSize, int ringBufferSize) {
        ThreadPoolExecutor threadPoolExecutor = ThreadPoolUtils.newFixedThreadPool(messageHandleThreadSize);
        disruptor = new Disruptor<MessageEvent>(
                new EventFactory<MessageEvent>() {
                    public MessageEvent newInstance() {
                        return new MessageEvent();
                    }
                },
                ringBufferSize,
                threadPoolExecutor,
                ProducerType.MULTI,
                new BlockingWaitStrategy());
        EventWorkHandler[] eventWorkHandlers = new EventWorkHandler[messageHandleThreadSize];
        for (int i = 0; i < eventWorkHandlers.length; i++) {
            eventWorkHandlers[i] = new EventWorkHandler(serviceBeanMap);
        }
        disruptor.handleEventsWithWorkerPool(eventWorkHandlers);
    }

    public void publishEvent(MessageEvent messageEvent) {
        disruptor.publishEvent(new EventTranslator<MessageEvent>() {
            @Override
            public void translateTo(MessageEvent event, long sequence) {
                event.setSequence(sequence);
                event.setChannel(messageEvent.getChannel());
                event.setRequest(messageEvent.getRequest());
            }
        });
    }


    public void registerServiceBean(String serviceName, Object serviceBean) {
        serviceBeanMap.put(serviceName, serviceBean);
    }

    public void start() {
        // 启动disruptor
        disruptor.start();
    }

}
