package com.github.wang.wrpc.context.common;

import lombok.Data;

/**
 * @author : wang
 * @date : 2019/12/27
 */
@Data
public class Invocation {

    private String serviceName;
    private String serviceVersion;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;

}
