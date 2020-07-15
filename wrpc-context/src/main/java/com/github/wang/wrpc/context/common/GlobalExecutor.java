package com.github.wang.wrpc.context.common;

import com.github.wang.wrpc.common.utils.ThreadPoolUtils;

import java.util.concurrent.*;

public class GlobalExecutor {

    //rpc invoker 是否活跃状态检查时间间隔
    private static final long ALIVE_RPC_INVOKER_CHECK_TIME = 1000 * 30;

    private static ScheduledExecutorService invokerChecktimer =
            new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);

                    t.setDaemon(true);
                    t.setName("com.github.wang.wrpc.context.common.GlobalExecutor.timer");

                    return t;
                }
            });

    private static ScheduledExecutorService futureTimeoutTimer =
            new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);

                    t.setDaemon(true);
                    t.setName("com.github.wang.wrpc.context.common.GlobalExecutor.timer");

                    return t;
                }
            });

    private static ThreadPoolExecutor threadPoolExecutor = ThreadPoolUtils.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), ThreadPoolUtils.buildQueue(1024),
            Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());//

    public static void registerTaskToFutureTimeoutTimer(Runnable runnable) {
        futureTimeoutTimer.scheduleAtFixedRate(runnable, 0, 1000 * 60 , TimeUnit.MILLISECONDS);
    }

    public static void registerTaskToTimer(Runnable runnable) {
        invokerChecktimer.scheduleAtFixedRate(runnable, 0, ALIVE_RPC_INVOKER_CHECK_TIME , TimeUnit.MILLISECONDS);
    }


    public static void registerTaskToPool(Runnable runnable) {
        threadPoolExecutor.submit(runnable);
    }

}
