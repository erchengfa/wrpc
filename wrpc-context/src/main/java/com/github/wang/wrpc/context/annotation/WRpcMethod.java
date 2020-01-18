package com.github.wang.wrpc.context.annotation;

import java.lang.annotation.*;

/**
 * @author : wang
 * @date : 2020/1/17
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface WRpcMethod {

    /**
     * 是否排除
     * @return
     */
    boolean exclude() default false;

    /**
     * 是否返回结果
     * @return
     */
    boolean back() default true;

}
