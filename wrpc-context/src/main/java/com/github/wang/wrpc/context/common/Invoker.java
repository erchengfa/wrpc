package com.github.wang.wrpc.context.common;

/**
 * @author : wang
 * @date : 2020/1/12
 */
public interface Invoker {

    WRPCResult invoke(Invocation invocation);

}
