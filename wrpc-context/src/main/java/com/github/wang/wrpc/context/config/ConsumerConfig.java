package com.github.wang.wrpc.context.config;

import com.github.wang.wrpc.common.utils.ClassUtils;
import com.github.wang.wrpc.context.annotation.WRpcMethod;
import com.github.wang.wrpc.context.consumer.ConsumeApplicationContext;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : wang
 * @date : 2020/1/5
 */
@Data
public class ConsumerConfig<T> {

    /**
     * 调用的协议
     */
    protected String protocol = RpcDefaultConfig.PROTOCOL;

    /**
     * 默认序列化
     */
    protected String serialization = RpcDefaultConfig.SERIALIZATION;

    /**
     * The App name.
     */
    protected String applicationName;

    /**
     * The Retries. 失败后重试次数
     */
    protected int retries = RpcDefaultConfig.CONSUMER_RETRIES;

    /**
     * 注册中心配置，可配置多个
     */
    protected RegistryConfig registry;

    private String loadBlance = RpcDefaultConfig.LOADBALANCE;

    private String cluster = RpcDefaultConfig.DEFAULT_CLUSTER;

    private long invokeTimeout = RpcDefaultConfig.SERVICE_INVOKE_TIMEOUT;

    /**
     * 接口名
     */
    protected Class<?> interfaceClass ;

    private String serviceVersion;

    private ConsumeApplicationContext consumeApplicationContext;

    private List<Method> methods;


    public T refer(){
        setMethods();
        consumeApplicationContext = new ConsumeApplicationContext(this);
        return  (T) consumeApplicationContext.refer();
    }

    public String getInterfaceName(){
        return interfaceClass.getName();
    }


    private void setMethods() {
        List<Method> allMethods = ClassUtils.getAllMethods(interfaceClass);
        this.methods = new ArrayList<>();
        for (Method method:allMethods){
            WRpcMethod wRpcMethod = method.getAnnotation(WRpcMethod.class);
            if (wRpcMethod != null){
                if (wRpcMethod.exclude()){
                    continue;
                }
            }
            this.methods.add(method);
        }
    }



}
