package com.github.wang.wrpc.context.test.utils;

import com.github.wang.wrpc.common.utils.ClassUtils;
import com.github.wang.wrpc.context.test.service.IDemoService;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author : wang
 * @date : 2020/1/18
 */
public class TestClassUtils {

    @Test
    public void testMethod(){
        List<Method> allMethods = ClassUtils.getAllMethods(IDemoService.class);
        for (Method method:allMethods){
            StringBuilder stringBuilder = new StringBuilder(method.getName());
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (Class clazz: parameterTypes){
                stringBuilder.append(clazz.getName());
            }
            System.out.println( stringBuilder.toString());
        }

    }

}
