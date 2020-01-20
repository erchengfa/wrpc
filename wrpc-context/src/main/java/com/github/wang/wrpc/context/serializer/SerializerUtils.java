package com.github.wang.wrpc.context.serializer;

import com.github.wang.wrpc.common.ext.ServiceLoader;
import com.github.wang.wrpc.common.ext.ServiceLoaderFactory;
import com.github.wang.wrpc.context.common.SerializationConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : wang
 * @date : 2020/1/17
 */
public class SerializerUtils {

    public static Map<Byte,Serializer> serializerMap = new HashMap<Byte,Serializer>();

    public static Map<String,Byte> serializerIdMap = new HashMap<String,Byte>();

    static {
        ServiceLoader<Serializer> serializerLoader = ServiceLoaderFactory.getExtensionLoader(Serializer.class);
        List<Serializer> instances = serializerLoader.getInstances();
        for (Serializer serializer:instances){
            serializerMap.put(serializer.getId(),serializer);
        }

        serializerIdMap.put("kryo", SerializationConstants.KRYO_SERIALIZATION_ID);

    }

    public static Serializer getSerializer(Byte id){
        return serializerMap.get(id);
    }

    public static byte getSerializerId(String serializetion){
        return serializerIdMap.get(serializetion);
    }

}
