package dev.robin.utils.concurrent;

import top.fl0wowp4rty.phantomshield.annotations.Native;
import top.fl0wowp4rty.phantomshield.annotations.obfuscation.CodeVirtualization;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Customized ThreadPoolExecutor with dynamic adjustments and monitoring.
 *
 * @author DiaoLing
 * @since 12/31/2023
 */

@Native
public class CustomThreadPoolExecutor extends ThreadPoolExecutor {
    private final AtomicLong completedTaskCount = new AtomicLong(0);
    private final AtomicLong totalTaskTime = new AtomicLong(0);
    private final ThreadLocal<Long> startTime = new ThreadLocal<>();
    private ScheduledExecutorService scheduledExecutorService;

    public CustomThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);

        scheduleThreadPoolAdjustment(1, TimeUnit.MINUTES);
    }

    @CodeVirtualization("FISH_RED")
    public void adjustThreadPool(int newCorePoolSize, int newMaximumPoolSize) {
        setCorePoolSize(newCorePoolSize);
        setMaximumPoolSize(newMaximumPoolSize);
    }

    @CodeVirtualization("FISH_RED")
    public void scheduleThreadPoolAdjustment(long period, TimeUnit unit) {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            int queueSize = getQueue().size();
            if (queueSize > 50) {
                adjustThreadPool(getCorePoolSize() + 1, getMaximumPoolSize() + 1);
            } else if (queueSize < 10) {
                adjustThreadPool(Math.max(getCorePoolSize() - 1, 1), Math.max(getMaximumPoolSize() - 1, 1));
            }
        }, 0, period, unit);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        startTime.set(System.nanoTime());
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        long taskTime = System.nanoTime() - startTime.get();
        totalTaskTime.addAndGet(taskTime);
        completedTaskCount.incrementAndGet();
        startTime.remove();
    }

    public long getCompletedTaskCount() {
        return completedTaskCount.get();
    }

    public double getAverageTaskTime() {
        return (getCompletedTaskCount() == 0) ? 0 : (double) totalTaskTime.get() / getCompletedTaskCount();
    }

    public void shutdownGracefully() {
        shutdown();
        try {
            if (!awaitTermination(60, TimeUnit.SECONDS)) {
                List<Runnable> droppedTasks = shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
                scheduledExecutorService.shutdownNow();
            }
        }
    }
}