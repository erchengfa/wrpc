package com.github.wang.wrpc.common.exception;

public class RPCRuntimeException extends RuntimeException {
    private int code;
    private String msg;

    public RPCRuntimeException(){

    }

    public RPCRuntimeException(Throwable cause) {
        super(cause);
    }

    public RPCRuntimeException(String msg, Throwable cause) {
        super(cause);
        this.code = 500;
        this.msg = msg;
    }
    public RPCRuntimeException(int code, String msg, Throwable cause) {
        super(cause);
        this.code = code;
        this.msg = msg;
    }

    public RPCRuntimeException(String msg){
        super(msg);
    }
    public RPCRuntimeException(int code, String msg){
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}
