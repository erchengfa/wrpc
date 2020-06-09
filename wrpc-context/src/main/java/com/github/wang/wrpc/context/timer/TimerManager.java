package com.github.wang.wrpc.context.timer;

import java.util.Timer;
import java.util.TimerTask;


public class TimerManager {

    private static Timer timer = new Timer();

    public static void registerTimerTask(TimerTask task,long delay, long period){
        timer.scheduleAtFixedRate(task, delay, period);
    }

}
