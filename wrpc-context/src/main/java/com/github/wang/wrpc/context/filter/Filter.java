package com.github.wang.wrpc.context.filter;

import com.github.wang.wrpc.common.ext.Spi;
import com.github.wang.wrpc.context.common.Invocation;
import com.github.wang.wrpc.context.common.Invoker;
import com.github.wang.wrpc.context.common.Order;
import com.github.wang.wrpc.context.common.WRPCResult;

/**
 * @author : wang
 * @date : 2020/1/19
 */
@Spi
public abstract class Filter implements Order {

   public abstract WRPCResult filter(Invoker invoker,Invocation invocation);

}
