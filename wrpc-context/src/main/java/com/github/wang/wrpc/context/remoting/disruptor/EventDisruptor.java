package com.github.wang.wrpc.context.remoting.disruptor;

import com.github.wang.wrpc.common.exception.RPCRuntimeException;
import com.github.wang.wrpc.common.utils.ThreadPoolUtils;
import com.github.wang.wrpc.context.annotation.WRpcMethod;
import com.github.wang.wrpc.context.common.Invocation;
import com.github.wang.wrpc.context.common.Request;
import com.github.wang.wrpc.context.common.Response;
import com.github.wang.wrpc.context.remoting.handler.MessageHandler;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author : wang
 * @date : 2020/1/10
 */
@Slf4j
public class EventDisruptor {

    private Disruptor<MessageEvent> disruptor;

    private  ConcurrentHashMap<String, Object> serviceBeanMap = new ConcurrentHashMap<>();

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
                handle(event);
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


    public void handle(MessageEvent messageEvent) {
        Request request = messageEvent.getRequest();
        Response response = handleRequest(request);
        if (request.isBack()){
            messageEvent.getChannel().writeAndFlush(response);
        }
    }
    private Response handleRequest(Request request) {
        Response response = new Response(request.getId(),request.getSerializerId());
        Invocation requestBody = (Invocation) request.getBody();
        String serviceName = requestBody.getServiceName();
        String serviceVersion = requestBody.getServiceVersion();
        if (StringUtils.isNotEmpty(serviceVersion)) {
            serviceName += "-" + serviceVersion;
        }
        Object serviceBean = serviceBeanMap.get(serviceName);
        if (serviceBean == null) {
            response.setBody(new RPCRuntimeException(//
                    String.format("Can't find the corresponding bean processing: %s", serviceName)));
            return response;
        }
        // 获取反射调用所需的参数
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = requestBody.getMethodName();

        Class<?>[] parameterTypes = requestBody.getParameterTypes();
        Object[] parameters = requestBody.getParameters();
        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        Method method = serviceFastMethod.getJavaMethod();
        WRpcMethod wRpcMethod = method.getAnnotation(WRpcMethod.class);
        if (wRpcMethod != null){
            boolean exclude = wRpcMethod.exclude();
            if (exclude){
                response.setBody(new RPCRuntimeException(//
                        String.format("Can't find the corresponding method processing: %s", methodName)));
                return response;
            }
        }
        try {
            Object result = serviceFastMethod.invoke(serviceBean, parameters);
            response.setBody(result);
        } catch (InvocationTargetException e) {
            log.error("service invoke error", e);
            response.setBody(new RPCRuntimeException(e));
        }
        return response;
    }

    public void registerServiceBean(String serviceName,Object serviceBean){
        serviceBeanMap.put(serviceName,serviceBean);
    }

    public void start(){
       // 启动disruptor
        disruptor.start();
    }

}
