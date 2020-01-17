package com.github.wang.wrpc.serialization.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.github.wang.wrpc.context.common.SerializationConstants;
import com.github.wang.wrpc.context.serializer.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author : wang
 * @date : 2019/12/27
 */
public class kryoSerializer implements Serializer {

    private static AbstractKryoFactory kryoFactory = new ThreadLocalKryoFactory();


    @Override
    public byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream baos = null;
        Output output = null;
        try {
            baos = new ByteArrayOutputStream();
            output = new Output(baos);
            getKryo().writeClassAndObject(output, obj);
            return output.toBytes();
        } finally {
            if (baos != null){
                baos.flush();
                baos.close();
            }
            if (output != null){
                output.flush();
                output.close();
            }
        }
    }

    /**
     * 获取kryo
     *
     * @return
     */
    private Kryo getKryo() {
        return kryoFactory.getKryo();
    }

    @Override
    public <T> T deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(inputStream);
        return (T)getKryo().readClassAndObject(input);
    }

    @Override
    public byte getContentTypeId() {
        return SerializationConstants.KRYO_SERIALIZATION_ID;
    }

}