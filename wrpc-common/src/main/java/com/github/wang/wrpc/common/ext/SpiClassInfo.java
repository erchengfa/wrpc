package com.github.wang.wrpc.common.ext;

import com.github.wang.wrpc.common.exception.RPCRuntimeException;
import com.github.wang.wrpc.common.utils.ClassUtils;
import lombok.Data;

@Data
public class SpiClassInfo<T> {


    protected final Class<? extends T> clazz;


    private volatile transient T instance;

    protected boolean singleton;


    public SpiClassInfo(Class<? extends T> clazz) {
        this.clazz = clazz;
    }


    public T getSpiInstance() {
        return getSpiInstance(null, null);
    }

    public T getSpiInstance(Class[] argTypes, Object[] args) {
        if (clazz != null) {
            try {
                if (singleton) { // 如果是单例
                    if (instance == null) {
                        synchronized (this) {
                            if (instance == null) {
                                instance = ClassUtils.newInstanceWithArgs(clazz, argTypes, args);
                            }
                        }
                    }
                    return instance; // 保留单例
                } else {
                    return ClassUtils.newInstanceWithArgs(clazz, argTypes, args);
                }
            } catch (Exception e) {
                throw new RPCRuntimeException("create " + clazz.getCanonicalName() + " instance error", e);
            }
        }
        throw new RPCRuntimeException("Class of ExtensionClass is null");
    }


}
