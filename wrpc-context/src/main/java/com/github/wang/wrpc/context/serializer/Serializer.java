package com.github.wang.wrpc.context.serializer;


import com.github.wang.wrpc.common.ext.Spi;

import java.io.IOException;

/**
 * @author : wang
 * @date : 2019/12/27
 */
@Spi
public interface Serializer {

    /**
     * 序列化
     * @param t
     * @return
     * @throws IOException
     */
    byte[] serialize(Object t) throws IOException;

    /**
     * 反序列化
     * @param bytes
     * @param <T>
     * @return
     * @throws IOException
     */
    <T> T deserialize(byte[] bytes) throws IOException;

    /**
     * 获取序列化类型
     * @return
     */
    byte getContentTypeId();

}