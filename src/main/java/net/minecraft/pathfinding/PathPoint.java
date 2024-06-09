package net.minecraft.pathfinding;

import net.minecraft.util.MathHelper;

public class PathPoint {
    public final int xCoord;
    public final int yCoord;
    public final int zCoord;
    private final int hash;
    int bZ = -1;
    float totalPathDistance;
    float distanceToNext;
    float distanceToTarget;
    PathPoint previous;
    public boolean visited;

    public PathPoint(int x2, int y2, int z) {
        this.xCoord = x2;
        this.yCoord = y2;
        this.zCoord = z;
        this.hash = PathPoint.makeHash(x2, y2, z);
    }

    public static int makeHash(int x2, int y2, int z) {
        return y2 & 0xFF | (x2 & Short.MAX_VALUE) << 8 | (z & Short.MAX_VALUE) << 24 | (x2 < 0 ? Integer.MIN_VALUE : 0) | (z < 0 ? 32768 : 0);
    }

    public float distanceTo(PathPoint pathpointIn) {
        float f = pathpointIn.xCoord - this.xCoord;
        float f1 = pathpointIn.yCoord - this.yCoord;
        float f2 = pathpointIn.zCoord - this.zCoord;
        return MathHelper.sqrt_float(f * f + f1 * f1 + f2 * f2);
    }

    public float distanceToSquared(PathPoint pathpointIn) {
        float f = pathpointIn.xCoord - this.xCoord;
        float f1 = pathpointIn.yCoord - this.yCoord;
        float f2 = pathpointIn.zCoord - this.zCoord;
        return f * f + f1 * f1 + f2 * f2;
    }

    public boolean equals(Object p_equals_1_) {
        if (!(p_equals_1_ instanceof PathPoint)) {
            return false;
        }
        PathPoint pathpoint = (PathPoint)p_equals_1_;
        return this.hash == pathpoint.hash && this.xCoord == pathpoint.xCoord && this.yCoord == pathpoint.yCoord && this.zCoord == pathpoint.zCoord;
    }

    public int hashCode() {
        return this.hash;
    }

    public boolean isAssigned() {
        return this.bZ >= 0;
    }

    public String toString() {
        return this.xCoord + ", " + this.yCoord + ", " + this.zCoord;
    }
}

