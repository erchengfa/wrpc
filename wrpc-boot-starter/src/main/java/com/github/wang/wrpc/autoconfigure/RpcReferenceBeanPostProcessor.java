package com.github.wang.wrpc.autoconfigure;

import com.github.wang.wrpc.autoconfigure.annotation.RpcReference;
import com.github.wang.wrpc.common.utils.StringUtils;
import com.github.wang.wrpc.context.config.ConsumerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author suzhe
 * @date 2018/11/26
 */
@Slf4j
public class RpcReferenceBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private ConcurrentHashMap<String,ConsumerConfig> consumerConfigConcurrentHashMap = new ConcurrentHashMap<>();
    /**
     * 给有RpcReference注解的属性 注入远程调用代理类
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        log.debug("RpcReferenceBeanPostProcessor beanName:{}",beanName);
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(RpcReference.class)) {
                continue;
            }
            Class<?> fieldType = field.getType();
            log.debug("RpcReferenceBeanPostProcessor file type:{}", fieldType);
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);
            String version = rpcReference.version();
            field.setAccessible(true);
            String serviceName = fieldType.getName();
            if (StringUtils.isNotBlank(version)){
                serviceName = serviceName + "-" + version;
            }
            try {
                field.set(bean, consumerConfigConcurrentHashMap.get(serviceName).refer());
            } catch (IllegalAccessException e) {
                log.error("nrpc error:", e);
            }
        }
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        Map<String, ConsumerConfig> consumerConfigMap = applicationContext.getBeansOfType(ConsumerConfig.class);
        for (ConsumerConfig consumerConfig: new HashSet<>(consumerConfigMap.values())){
            consumerConfigConcurrentHashMap.put(consumerConfig.getServiceName(),consumerConfig);
        }

    }
}
