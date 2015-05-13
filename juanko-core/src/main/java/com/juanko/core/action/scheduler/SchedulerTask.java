package com.juanko.core.action.scheduler;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author gaston
 */
public class SchedulerTask extends Timer {

    @Override
    public void schedule(TimerTask task, Date firstTime, long period) {
        super.schedule(task, firstTime, period);
    }

    @Override
    public void schedule(TimerTask task, long delay) {
        super.schedule(task, delay); 
    }

}
