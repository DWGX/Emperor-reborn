package dev.emperor.utils.vec;

public class Vector3d {
    private final double x;
    private final double y;
    private final double z;

    public Vector3d(double x2, double y2, double z) {
        this.x = x2;
        this.y = y2;
        this.z = z;
    }

    public Vector3d add(double x2, double y2, double z) {
        return new Vector3d(this.x + x2, this.y + y2, this.z + z);
    }

    public Vector3d add(Vector3d vector) {
        return this.add(vector.x, vector.y, vector.z);
    }

    public Vector3d subtract(double x2, double y2, double z) {
        return this.add(-x2, -y2, -z);
    }

    public Vector3d subtract(Vector3d vector) {
        return this.add(-vector.x, -vector.y, -vector.z);
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public Vector3d multiply(double v) {
        return new Vector3d(this.x * v, this.y * v, this.z * v);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Vector3d)) {
            return false;
        }
        Vector3d vector = (Vector3d)obj;
        return Math.floor(this.x) == Math.floor(vector.x) && Math.floor(this.y) == Math.floor(vector.y) && Math.floor(this.z) == Math.floor(vector.z);
    }
}

