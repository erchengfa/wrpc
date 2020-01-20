package com.github.wang.wrpc.filter;

import com.github.wang.wrpc.context.common.Invocation;
import com.github.wang.wrpc.context.common.Invoker;
import com.github.wang.wrpc.context.common.WRPCResult;
import com.github.wang.wrpc.context.filter.Filter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : wang
 * @date : 2020/1/19
 */
@Slf4j
public class TimerFilter extends Filter {
    @Override
    public WRPCResult filter(Invoker invoker, Invocation invocation) {
        long s = System.currentTimeMillis();
        WRPCResult wrpcResult = invoker.invoke(invocation);
        long e = System.currentTimeMillis();
        log.info("time filter:{},cost time:{}",invocation,(e - s));
        return wrpcResult;
    }

    @Override
    public Integer getOrder() {
        return 0;
    }
}
