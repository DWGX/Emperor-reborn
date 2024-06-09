package net.minecraft.pathfinding;

import net.minecraft.pathfinding.PathPoint;

public class Path {
    private PathPoint[] pathPoints = new PathPoint[1024];
    private int count;

    public PathPoint addPoint(PathPoint point) {
        if (point.bZ >= 0) {
            throw new IllegalStateException("OW KNOWS!");
        }
        if (this.count == this.pathPoints.length) {
            PathPoint[] apathpoint = new PathPoint[this.count << 1];
            System.arraycopy(this.pathPoints, 0, apathpoint, 0, this.count);
            this.pathPoints = apathpoint;
        }
        this.pathPoints[this.count] = point;
        point.bZ = this.count;
        this.sortBack(this.count++);
        return point;
    }

    public void clearPath() {
        this.count = 0;
    }

    public PathPoint dequeue() {
        PathPoint pathpoint = this.pathPoints[0];
        this.pathPoints[0] = this.pathPoints[--this.count];
        this.pathPoints[this.count] = null;
        if (this.count > 0) {
            this.sortForward(0);
        }
        pathpoint.bZ = -1;
        return pathpoint;
    }

    public void changeDistance(PathPoint p_75850_1_, float p_75850_2_) {
        float f = p_75850_1_.distanceToTarget;
        p_75850_1_.distanceToTarget = p_75850_2_;
        if (p_75850_2_ < f) {
            this.sortBack(p_75850_1_.bZ);
        } else {
            this.sortForward(p_75850_1_.bZ);
        }
    }

    private void sortBack(int p_75847_1_) {
        PathPoint pathpoint = this.pathPoints[p_75847_1_];
        float f = pathpoint.distanceToTarget;
        while (p_75847_1_ > 0) {
            int i = p_75847_1_ - 1 >> 1;
            PathPoint pathpoint1 = this.pathPoints[i];
            if (f >= pathpoint1.distanceToTarget) break;
            this.pathPoints[p_75847_1_] = pathpoint1;
            pathpoint1.bZ = p_75847_1_;
            p_75847_1_ = i;
        }
        this.pathPoints[p_75847_1_] = pathpoint;
        pathpoint.bZ = p_75847_1_;
    }

    private void sortForward(int p_75846_1_) {
        PathPoint pathpoint = this.pathPoints[p_75846_1_];
        float f = pathpoint.distanceToTarget;
        while (true) {
            float f2;
            PathPoint pathpoint2;
            int i = 1 + (p_75846_1_ << 1);
            int j2 = i + 1;
            if (i >= this.count) break;
            PathPoint pathpoint1 = this.pathPoints[i];
            float f1 = pathpoint1.distanceToTarget;
            if (j2 >= this.count) {
                pathpoint2 = null;
                f2 = Float.POSITIVE_INFINITY;
            } else {
                pathpoint2 = this.pathPoints[j2];
                f2 = pathpoint2.distanceToTarget;
            }
            if (f1 < f2) {
                if (f1 >= f) break;
                this.pathPoints[p_75846_1_] = pathpoint1;
                pathpoint1.bZ = p_75846_1_;
                p_75846_1_ = i;
                continue;
            }
            if (f2 >= f) break;
            this.pathPoints[p_75846_1_] = pathpoint2;
            pathpoint2.bZ = p_75846_1_;
            p_75846_1_ = j2;
        }
        this.pathPoints[p_75846_1_] = pathpoint;
        pathpoint.bZ = p_75846_1_;
    }

    public boolean isPathEmpty() {
        return this.count == 0;
    }
}

