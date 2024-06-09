package dev.emperor.utils.concurrent;

import top.fl0wowp4rty.phantomshield.annotations.Native;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Custom handler for tasks that cannot be executed by a ThreadPoolExecutor.
 *
 * @author DiaoLing
 * @since 12/31/2023
 */
@Native
public class CustomRejectedExecutionHandler implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        var executorStats = String.format(
                "Pool Size: %d, Active Threads: %d, Completed Tasks: %d, Total Tasks: %d, Queue Size: %d",
                executor.getPoolSize(),
                executor.getActiveCount(),
                executor.getCompletedTaskCount(),
                executor.getTaskCount(),
                executor.getQueue().size()
        );

    }
}