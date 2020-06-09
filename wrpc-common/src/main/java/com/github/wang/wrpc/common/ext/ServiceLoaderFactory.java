package com.github.wang.wrpc.common.ext;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class ServiceLoaderFactory {

    private static final ConcurrentMap<Class, ServiceLoader> LOADER_MAP = new ConcurrentHashMap<Class, ServiceLoader>();

    public static <T> ServiceLoader<T> getExtensionLoader(Class<T> clazz) {
        ServiceLoader<T> loader = LOADER_MAP.get(clazz);
        if (loader == null) {
            synchronized (ServiceLoaderFactory.class) {
                loader = LOADER_MAP.get(clazz);
                if (loader == null) {
                    loader = new ServiceLoader<T>(clazz);
                    LOADER_MAP.put(clazz, loader);
                }
            }
        }
        return loader;
    }

}
