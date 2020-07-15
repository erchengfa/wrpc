package com.github.wang.wrpc.context.remoting;


import com.github.wang.wrpc.common.exception.RPCRuntimeException;
import com.github.wang.wrpc.context.common.GlobalExecutor;
import com.github.wang.wrpc.context.common.Request;
import com.github.wang.wrpc.context.common.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


@Slf4j
public class DefaultFuture extends CompletableFuture<Object> {

    private static final Map<Long, DefaultFuture> FUTURES = new ConcurrentHashMap<>();

    private Request request;

    private long timeout;

    private long requestTime;

    private long responseTime;

    private long id;

    static {
        GlobalExecutor.registerTaskToFutureTimeoutTimer(()->{
            log.debug("check timeout FUTURES：{}", FUTURES);
            for (DefaultFuture defaultFuture: FUTURES.values()){
                if (defaultFuture.isTimeout()){
                    log.debug("FUTURES remove:{} ",defaultFuture);
                    FUTURES.remove(defaultFuture.request.getId());
                }
            }
        });
    }

    private DefaultFuture(Request request, long timeout) {
        this.requestTime = System.currentTimeMillis();
        this.request = request;
        this.timeout = timeout;
        this.id = request.getId();
        FUTURES.put(id, this);
    }

    public static void received(Response response) {
        DefaultFuture future = FUTURES.remove(response.getId());
        if (future != null) {
            future.doReceived(response);
        }
    }

    private void doReceived(Response res) {
        if (res == null) {
            throw new IllegalStateException("response cannot be null");
        }
        this.responseTime = System.currentTimeMillis();
        this.complete(res);
    }

    public static DefaultFuture newFuture(Request request, long timeout) {
        final DefaultFuture future = new DefaultFuture(request, timeout);
        return future;
    }

    public Response getResponse() {
        try {
            return (Response) this.get(timeout, TimeUnit.MILLISECONDS);
        } catch (Throwable e) {
            FUTURES.remove(this.request.getId());
            log.error("request timeout:{}", this.request,e);
            throw new RPCRuntimeException(String.format("request timeout : %s", this.request), e);
        }
    }

    public boolean isTimeout(){
        long currentTime = System.currentTimeMillis();
        if ((currentTime - this.requestTime) > timeout){
          return true;
        }
        return false;
    }


}
