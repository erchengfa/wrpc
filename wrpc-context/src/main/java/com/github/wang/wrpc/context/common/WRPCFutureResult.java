package com.github.wang.wrpc.context.common;

import com.github.wang.wrpc.common.exception.RPCRuntimeException;
import com.github.wang.wrpc.context.remoting.DefaultFuture;
import lombok.Data;

/**
 * @author : wang
 * @date : 2020/1/16
 */
@Data
public class WRPCFutureResult implements WRPCResult {

    private DefaultFuture defaultFuture;

    private boolean back;

    public WRPCFutureResult(DefaultFuture defaultFuture){
        this.back = true;
        this.defaultFuture = defaultFuture;
    }
    public WRPCFutureResult(){
        this.back = false;
    }


    @Override
    public Object getResult() {
        if (back){
            Response response = defaultFuture.getResponse();
            if (response.getBody() instanceof RPCRuntimeException){
                throw (RPCRuntimeException)response.getBody();
            }
            return response.getBody();
        }else {
            return null;
        }

    }
}
