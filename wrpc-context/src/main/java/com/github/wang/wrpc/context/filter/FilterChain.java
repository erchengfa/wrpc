package com.github.wang.wrpc.context.filter;

import com.github.wang.wrpc.common.ext.ServiceLoader;
import com.github.wang.wrpc.common.ext.ServiceLoaderFactory;
import com.github.wang.wrpc.context.common.Invocation;
import com.github.wang.wrpc.context.common.Invoker;
import com.github.wang.wrpc.context.common.WRPCResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class FilterChain{
    public static List<Filter> filters = new ArrayList<>();

    static {
        ServiceLoader<Filter> extensionLoader = ServiceLoaderFactory.getExtensionLoader(Filter.class);
        filters = extensionLoader.getInstances();
        Collections.sort(filters, new Comparator<Filter>() {
            @Override
            public int compare(Filter o1, Filter o2) {
                if (o1.getOrder() > o2.getOrder()){
                    return -1;
                }
                if (o1.getOrder() < o2.getOrder()){
                    return 1;
                }
                return 0;
            }
        });
    }

    public static Invoker buildInvokerChain(Invoker invoker,Invocation invocation) {
        Invoker last = invoker;
        for (Filter filter:filters){
            Invoker next = last;
            //装饰器模式 层层包装
            last = new Invoker() {
                @Override
                public WRPCResult invoke(Invocation invocation) {
                    return filter.filter(next,invocation);
                }
            };
        }
        return last;
    }

}
