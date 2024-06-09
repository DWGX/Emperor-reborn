package dev.robin.utils.client;

import java.util.Random;

public class StopWatch {
    private long millis;

    public void setMillis(long millis) {
        this.millis = millis;
    }

    public static long randomDelay(int minDelay, int maxDelay) {
        return StopWatch.nextInt(minDelay, maxDelay);
    }

    public static int nextInt(int startInclusive, int endExclusive) {
        return endExclusive - startInclusive <= 0 ? startInclusive : startInclusive + new Random().nextInt(endExclusive - startInclusive);
    }

    public StopWatch() {
        this.reset();
    }

    public boolean finished(long delay) {
        return System.currentTimeMillis() - delay >= this.millis;
    }

    public void reset() {
        this.millis = System.currentTimeMillis();
    }

    public long getMillis() {
        return this.millis;
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - this.millis;
    }
}

