package dev.robin.utils.system;

import java.util.ArrayList;

public class MemoryUtils {
    public static void optimizeMemory(long cleanUpDelay) {
        try {
            System.gc();
            System.runFinalization();
            ArrayList<Thread> threads = new ArrayList<Thread>();
            int availableProcessors = Runtime.getRuntime().availableProcessors();
            for (int i = 0; i < availableProcessors; ++i) {
                Thread thread = new Thread(() -> {
                    try {
                        Thread.sleep(cleanUpDelay);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                threads.add(thread);
                thread.start();
            }
            for (Thread thread : threads) {
                thread.join();
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void memoryCleanup() {
        System.gc();
    }
}

