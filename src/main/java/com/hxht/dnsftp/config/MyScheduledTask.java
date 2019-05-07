package com.hxht.dnsftp.config;

import com.hxht.dnsftp.service.PullFile;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.ScheduledMethodRunnable;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;

/**
 * date  2019/4/18-16:04
 * Description:
 * 运行的结果：
 */
public class MyScheduledTask extends ThreadPoolTaskScheduler {

    private static final Logger log = LoggerFactory.getLogger(MyScheduledTask.class);
    volatile public Map<Runnable, ScheduledFuture<?>> scheduledTasks = new HashMap<Runnable, ScheduledFuture<?>>();

    public void myCancelTask(Class clazz) {
        log.info( "MyScheduledTask--"+clazz+"取消了本次定时任务");
        Runnable run = null;
        ScheduledFuture future = null;
        for (Map.Entry<Runnable, ScheduledFuture<?>> entry : scheduledTasks.entrySet()) {
            if (((ScheduledMethodRunnable) entry.getKey()).getTarget().getClass().isAssignableFrom(clazz)) {
                log.info("MyScheduledTask--进入了for循环,并成功找到了run");
                run = entry.getKey();
                future = scheduledTasks.get(run);
            }
        }

        future.cancel(true);
        scheduledTasks.remove(run);
        ScheduledFuture<?> fut = this.scheduleAtFixedRate(run, DateUtils.addMinutes(new Date(), 2), 2 * 60 * 1000);
        scheduledTasks.put(run, fut);
        log.info( "MyScheduledTask--PullFile.class取消本次定时任务成功");
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Date startTime, long period) {
        log.info("MyScheduledTask  开始执行：scheduleAtFixedRate方法");
        ScheduledFuture<?> future = super.scheduleAtFixedRate(task, startTime, period);
        ScheduledMethodRunnable runnable = (ScheduledMethodRunnable) task;
        boolean flag = ((ScheduledMethodRunnable) task).getTarget().getClass().isAssignableFrom(PullFile.class);
        if (flag) {
            scheduledTasks.put(runnable, future);
            log.info("MyScheduledTask   类名为："+((ScheduledMethodRunnable) task).getTarget().getClass() + "重新开启了定时任务");
        }
        return future;
    }

}
