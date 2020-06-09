package com.github.wang.wrpc.context.proxy;

import com.github.wang.wrpc.context.common.Invocation;
import com.github.wang.wrpc.context.common.Invoker;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


@Slf4j
public class InvocationProxy<T> {

    private Invoker invoker;
    public InvocationProxy(Invoker invoker){
        this.invoker = invoker;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(final Class<?> interfaceClass, final String serviceVersion) {
        // 创建动态代理对象
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        String methodName = method.getName();
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        if (method.getDeclaringClass() == Object.class) {
                            return method.invoke(invoker, args);
                        }
                        if ("toString".equals(methodName) && parameterTypes.length == 0) {
                            return invoker.toString();
                        }
                        if ("hashCode".equals(methodName) && parameterTypes.length == 0) {
                            return invoker.hashCode();
                        }
                        if ("equals".equals(methodName) && parameterTypes.length == 1) {
                            return invoker.equals(args[0]);
                        }

                        //构建请求
                        Invocation invocation = new Invocation();
                        invocation.setServiceName(interfaceClass.getName());
                        invocation.setServiceVersion(serviceVersion);
                        invocation.setMethodName(method.getName());
                        invocation.setParameterTypes(method.getParameterTypes());
                        invocation.setParameters(args);
                        return invoker.invoke(invocation).getResult();
                    }
                }
        );
    }



}
