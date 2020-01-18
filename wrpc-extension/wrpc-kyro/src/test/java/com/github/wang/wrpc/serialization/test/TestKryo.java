package com.github.wang.wrpc.serialization.test;

import com.github.wang.wrpc.common.exception.RPCRuntimeException;
import com.github.wang.wrpc.context.serializer.Serializer;
import com.github.wang.wrpc.serialization.kryo.kryoSerializer;
import org.junit.Test;

/**
 * @author : wang
 * @date : 2019/12/27
 */
public class TestKryo {

    Serializer serializer = new kryoSerializer();

    @Test
    public void test() throws Throwable {
        RPCRuntimeException rpcRuntimeException = new RPCRuntimeException(//
                String.format("Can't find the corresponding method processing: %s", ""));
        User user = new User();
        user.setUserName("hello");
        user.setAge(18);
        byte[] bytes = serializer.serialize(rpcRuntimeException);

        Object obj = serializer.deserialize(bytes);
        System.out.println(obj);
    }

}
