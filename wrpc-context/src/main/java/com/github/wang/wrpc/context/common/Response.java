package com.github.wang.wrpc.context.common;

import lombok.Data;

/**
 * @author : wang
 * @date : 2019/12/25
 */
@Data
public class Response {

    private long id;

    private boolean heartbeat;

    private Object body;

    public Response(long id) {
        this.id = id;
    }

}
