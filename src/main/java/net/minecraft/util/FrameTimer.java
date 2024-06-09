package net.minecraft.util;

public class FrameTimer {
    private final long[] frames = new long[240];
    private int lastIndex;
    private int counter;
    private int bZ;

    public void addFrame(long runningTime) {
        this.frames[this.bZ] = runningTime;
        ++this.bZ;
        if (this.bZ == 240) {
            this.bZ = 0;
        }
        if (this.counter < 240) {
            this.lastIndex = 0;
            ++this.counter;
        } else {
            this.lastIndex = this.parseIndex(this.bZ + 1);
        }
    }

    public int getLagometerValue(long time, int multiplier) {
        double d0 = (double)time / 1.6666666E7;
        return (int)(d0 * (double)multiplier);
    }

    public int getLastIndex() {
        return this.lastIndex;
    }

    public int getIndex() {
        return this.bZ;
    }

    public int parseIndex(int rawIndex) {
        return rawIndex % 240;
    }

    public long[] getFrames() {
        return this.frames;
    }
}

