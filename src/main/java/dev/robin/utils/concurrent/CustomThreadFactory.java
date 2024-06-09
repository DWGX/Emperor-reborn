package dev.robin.utils.concurrent;

import org.jetbrains.annotations.NotNull;
import top.fl0wowp4rty.phantomshield.annotations.Native;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Custom thread factory.
 *
 * @author DiaoLing
 * @since 12/31/2023
 */
@Native
public class CustomThreadFactory implements ThreadFactory {
    private final AtomicInteger threadId = new AtomicInteger(0);

    @Override
    public Thread newThread(@NotNull Runnable r) {
        var threadName = dev.robin.Client.NAME + "-Thread-" + threadId.getAndIncrement();

        var thread = new Thread(r, threadName);
        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler((t, e) -> {
        });

        thread.setPriority(Thread.NORM_PRIORITY);
        return thread;
    }
}