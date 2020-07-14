package com.github.wang.wrpc.context.common;

import com.github.wang.wrpc.common.utils.ThreadPoolUtils;

import java.util.concurrent.*;

public class GlobalExecutor {

    //rpc invoker 是否活跃状态检查时间间隔
    private static final long ALIVE_RPC_INVOKER_CHECK_TIME = 1000 * 30;

    private static ScheduledExecutorService timer =
            new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);

                    t.setDaemon(true);
                    t.setName("com.github.wang.wrpc.context.common.GlobalExecutor.timer");

                    return t;
                }
            });

    private static ThreadPoolExecutor threadPoolExecutor = ThreadPoolUtils.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), ThreadPoolUtils.buildQueue(64),
            Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());//

    public static void registerTaskToTimer(Runnable runnable) {
        timer.scheduleAtFixedRate(runnable, 0, ALIVE_RPC_INVOKER_CHECK_TIME , TimeUnit.MILLISECONDS);
    }

    public static void registerTaskToPool(Runnable runnable) {
        threadPoolExecutor.submit(runnable);
    }

}
