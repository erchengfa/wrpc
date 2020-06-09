package com.github.wang.wrpc.context.common;

import com.github.wang.wrpc.context.config.RpcDefaultConfig;
import lombok.Data;


@Data
public class Response {

    private long id;

    private boolean heartbeat;

    private byte serializerId = RpcDefaultConfig.SERIALIZATION_ID;;

    private Object body;

    public Response(long id) {
        this.id = id;
    }

    public Response(long id,byte serializerId) {
        this.id = id;
        this.serializerId = serializerId;
    }

}
