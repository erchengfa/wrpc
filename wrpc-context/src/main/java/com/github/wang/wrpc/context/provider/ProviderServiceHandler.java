package com.github.wang.wrpc.context.provider;

import com.github.wang.wrpc.common.exception.RPCRuntimeException;
import com.github.wang.wrpc.context.annotation.WRpcMethod;
import com.github.wang.wrpc.context.common.Invocation;
import com.github.wang.wrpc.context.common.Request;
import com.github.wang.wrpc.context.common.Response;
import com.github.wang.wrpc.context.config.ProviderConfig;
import com.github.wang.wrpc.context.remoting.disruptor.MessageEvent;
import com.github.wang.wrpc.context.remoting.handler.MessageHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : wang
 * @date : 2020/1/11
 */
@Slf4j
@Data
public class ProviderServiceHandler implements MessageHandler {
    private ConcurrentHashMap<String, Object> serviceBeanMap;

    private ProviderConfig providerConfig;

    public ProviderServiceHandler(ProviderConfig providerConfig) {
        this.serviceBeanMap = new ConcurrentHashMap<>();
        this.providerConfig = providerConfig;
    }

    @Override
    public void handle(MessageEvent messageEvent) {
        Request request = messageEvent.getRequest();
        Response response = handleRequest(request);
        if (request.isBack()){
            messageEvent.getChannel().writeAndFlush(response);
        }
    }


    private Response handleRequest(Request request) {
        Response response = new Response(request.getId());
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

    public void registerServiceBean(String serviceName, Object o) {
        serviceBeanMap.put(serviceName, o);
    }
}