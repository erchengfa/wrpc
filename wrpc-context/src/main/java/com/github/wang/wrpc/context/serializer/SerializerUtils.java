package com.github.wang.wrpc.context.serializer;

import com.github.wang.wrpc.common.ext.ServiceLoader;
import com.github.wang.wrpc.common.ext.ServiceLoaderFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : wang
 * @date : 2020/1/17
 */
public class SerializerUtils {

    public static Map<Byte,Serializer> serializerMap = new HashMap<Byte,Serializer>();

    static {
        ServiceLoader<Serializer> serializerLoader = ServiceLoaderFactory.getExtensionLoader(Serializer.class);
        List<Serializer> instances = serializerLoader.getInstances();
        for (Serializer serializer:instances){
            serializerMap.put(serializer.getContentTypeId(),serializer);
        }
    }

    public static Serializer getSerializer(byte id){
        return serializerMap.get(id);
    }

}
