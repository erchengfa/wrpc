package com.github.wang.wrpc.common.utils;


import com.github.wang.wrpc.common.exception.RPCRuntimeException;


public final class ExceptionUtils {

    public static RPCRuntimeException buildRuntime(String configKey, String configValue) {
        String msg = "The value of config " + configKey + " [" + configValue + "] is illegal, please check it";
        return new RPCRuntimeException(msg);
    }

    public static RPCRuntimeException buildRuntime(String configKey, String configValue, String message) {
        String msg = "The value of config " + configKey + " [" + configValue + "] is illegal, " + message;
        return new RPCRuntimeException(msg);
    }


    /**
     * 返回堆栈信息（e.printStackTrace()的内容）
     *
     * @param e Throwable
     * @return 异常堆栈信息
     */
    public static String toString(Throwable e) {
        StackTraceElement[] traces = e.getStackTrace();
        StringBuilder sb = new StringBuilder(1024);
        sb.append(e.toString()).append("\n");
        if (traces != null) {
            for (StackTraceElement trace : traces) {
                sb.append("\tat ").append(trace).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * 返回消息+简短堆栈信息（e.printStackTrace()的内容）
     *
     * @param e          Throwable
     * @param stackLevel 堆栈层级
     * @return 异常堆栈信息
     */
    public static String toShortString(Throwable e, int stackLevel) {
        StackTraceElement[] traces = e.getStackTrace();
        StringBuilder sb = new StringBuilder(1024);
        sb.append(e.toString()).append("\t");
        if (traces != null) {
            for (int i = 0; i < traces.length; i++) {
                if (i < stackLevel) {
                    sb.append("\tat ").append(traces[i]).append("\t");
                } else {
                    break;
                }
            }
        }
        return sb.toString();
    }
}
