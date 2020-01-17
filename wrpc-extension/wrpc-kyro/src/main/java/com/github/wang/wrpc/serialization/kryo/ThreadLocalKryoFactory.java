package com.github.wang.wrpc.serialization.kryo;

import com.esotericsoftware.kryo.Kryo;

/**
 * @author : wang
 * @date : 2019/12/27
 */
public class ThreadLocalKryoFactory extends AbstractKryoFactory {

    private final ThreadLocal<Kryo> holder = new ThreadLocal<Kryo>() {
        @Override
        protected Kryo initialValue() {
            return create();
        }
    };

    @Override
    public Kryo getKryo() {
        return holder.get();
    }
}
