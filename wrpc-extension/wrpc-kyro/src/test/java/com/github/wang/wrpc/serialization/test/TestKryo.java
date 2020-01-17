package com.github.wang.wrpc.serialization.test;

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
        User user = new User();
        user.setUserName("hello");
        user.setAge(18);
        byte[] bytes = serializer.serialize("hello");

        Object obj = serializer.deserialize(bytes);
        System.out.println(obj);
    }

}
