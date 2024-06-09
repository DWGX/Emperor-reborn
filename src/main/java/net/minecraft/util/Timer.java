package net.minecraft.util;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

public class Timer {
    float ticksPerSecond;
    private double lastHRTime;
    public int elapsedTicks;
    @Getter
    public float renderPartialTicks;
    public float timerSpeed = 1.0f;
    public float elapsedPartialTicks;
    public long lastSyncSysClock;
    private long lastSyncHRClock;
    private long counter;
    private double timeSyncAdjustment = 1.0;

    public Timer(float tps) {
        this.ticksPerSecond = tps;
        this.lastSyncSysClock = Minecraft.getSystemTime();
        this.lastSyncHRClock = System.nanoTime() / 1000000L;
    }

    public void updateTimer() {
        long i = Minecraft.getSystemTime();
        long j2 = i - this.lastSyncSysClock;
        long k2 = System.nanoTime() / 1000000L;
        double d0 = (double)k2 / 1000.0;
        if (j2 <= 1000L && j2 >= 0L) {
            this.counter += j2;
            if (this.counter > 1000L) {
                long l2 = k2 - this.lastSyncHRClock;
                double d1 = (double)this.counter / (double)l2;
                this.timeSyncAdjustment += (d1 - this.timeSyncAdjustment) * (double)0.2f;
                this.lastSyncHRClock = k2;
                this.counter = 0L;
            }
            if (this.counter < 0L) {
                this.lastSyncHRClock = k2;
            }
        } else {
            this.lastHRTime = d0;
        }
        this.lastSyncSysClock = i;
        double d2 = (d0 - this.lastHRTime) * this.timeSyncAdjustment;
        this.lastHRTime = d0;
        d2 = MathHelper.clamp_double(d2, 0.0, 1.0);
        this.elapsedPartialTicks = (float)((double)this.elapsedPartialTicks + d2 * (double)this.timerSpeed * (double)this.ticksPerSecond);
        this.elapsedTicks = (int)this.elapsedPartialTicks;
        this.elapsedPartialTicks -= (float)this.elapsedTicks;
        if (this.elapsedTicks > 10) {
            this.elapsedTicks = 10;
        }
        this.renderPartialTicks = this.elapsedPartialTicks;
    }
}

