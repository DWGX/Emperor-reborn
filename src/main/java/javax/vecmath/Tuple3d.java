package javax.vecmath;

import java.io.Serializable;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.VecMathUtil;

public abstract class Tuple3d
implements Serializable,
Cloneable {
    static final long serialVersionUID = 5542096614926168415L;
    public double x;
    public double y;
    public double z;

    public Tuple3d(double x2, double y2, double z) {
        this.x = x2;
        this.y = y2;
        this.z = z;
    }

    public Tuple3d(double[] t2) {
        this.x = t2[0];
        this.y = t2[1];
        this.z = t2[2];
    }

    public Tuple3d(Tuple3d t1) {
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
    }

    public Tuple3d(Tuple3f t1) {
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
    }

    public Tuple3d() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
    }

    public final void set(double x2, double y2, double z) {
        this.x = x2;
        this.y = y2;
        this.z = z;
    }

    public final void set(double[] t2) {
        this.x = t2[0];
        this.y = t2[1];
        this.z = t2[2];
    }

    public final void set(Tuple3d t1) {
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
    }

    public final void set(Tuple3f t1) {
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
    }

    public final void get(double[] t2) {
        t2[0] = this.x;
        t2[1] = this.y;
        t2[2] = this.z;
    }

    public final void get(Tuple3d t2) {
        t2.x = this.x;
        t2.y = this.y;
        t2.z = this.z;
    }

    public final void add(Tuple3d t1, Tuple3d t2) {
        this.x = t1.x + t2.x;
        this.y = t1.y + t2.y;
        this.z = t1.z + t2.z;
    }

    public final void add(Tuple3d t1) {
        this.x += t1.x;
        this.y += t1.y;
        this.z += t1.z;
    }

    public final void sub(Tuple3d t1, Tuple3d t2) {
        this.x = t1.x - t2.x;
        this.y = t1.y - t2.y;
        this.z = t1.z - t2.z;
    }

    public final void sub(Tuple3d t1) {
        this.x -= t1.x;
        this.y -= t1.y;
        this.z -= t1.z;
    }

    public final void negate(Tuple3d t1) {
        this.x = -t1.x;
        this.y = -t1.y;
        this.z = -t1.z;
    }

    public final void negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
    }

    public final void scale(double s2, Tuple3d t1) {
        this.x = s2 * t1.x;
        this.y = s2 * t1.y;
        this.z = s2 * t1.z;
    }

    public final void scale(double s2) {
        this.x *= s2;
        this.y *= s2;
        this.z *= s2;
    }

    public final void scaleAdd(double s2, Tuple3d t1, Tuple3d t2) {
        this.x = s2 * t1.x + t2.x;
        this.y = s2 * t1.y + t2.y;
        this.z = s2 * t1.z + t2.z;
    }

    public final void scaleAdd(double s2, Tuple3f t1) {
        this.scaleAdd(s2, new Point3d(t1));
    }

    public final void scaleAdd(double s2, Tuple3d t1) {
        this.x = s2 * this.x + t1.x;
        this.y = s2 * this.y + t1.y;
        this.z = s2 * this.z + t1.z;
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

    public int hashCode() {
        long bits = 1L;
        bits = VecMathUtil.hashDoubleBits(bits, this.x);
        bits = VecMathUtil.hashDoubleBits(bits, this.y);
        bits = VecMathUtil.hashDoubleBits(bits, this.z);
        return VecMathUtil.hashFinish(bits);
    }

    public boolean equals(Tuple3d t1) {
        try {
            return this.x == t1.x && this.y == t1.y && this.z == t1.z;
        }
        catch (NullPointerException e2) {
            return false;
        }
    }

    public boolean equals(Object t1) {
        try {
            Tuple3d t2 = (Tuple3d)t1;
            return this.x == t2.x && this.y == t2.y && this.z == t2.z;
        }
        catch (ClassCastException e1) {
            return false;
        }
        catch (NullPointerException e2) {
            return false;
        }
    }

    public boolean epsilonEquals(Tuple3d t1, double epsilon) {
        double diff = this.x - t1.x;
        if (Double.isNaN(diff)) {
            return false;
        }
        double d2 = diff < 0.0 ? -diff : diff;
        if (d2 > epsilon) {
            return false;
        }
        diff = this.y - t1.y;
        if (Double.isNaN(diff)) {
            return false;
        }
        double d3 = diff < 0.0 ? -diff : diff;
        if (d3 > epsilon) {
            return false;
        }
        diff = this.z - t1.z;
        if (Double.isNaN(diff)) {
            return false;
        }
        double d4 = diff < 0.0 ? -diff : diff;
        return !(d4 > epsilon);
    }

    public final void clamp(float min, float max, Tuple3d t2) {
        this.clamp((double)min, (double)max, t2);
    }

    public final void clamp(double min, double max, Tuple3d t2) {
        this.x = t2.x > max ? max : (t2.x < min ? min : t2.x);
        this.y = t2.y > max ? max : (t2.y < min ? min : t2.y);
        this.z = t2.z > max ? max : (t2.z < min ? min : t2.z);
    }

    public final void clampMin(float min, Tuple3d t2) {
        this.clampMin((double)min, t2);
    }

    public final void clampMin(double min, Tuple3d t2) {
        this.x = t2.x < min ? min : t2.x;
        this.y = t2.y < min ? min : t2.y;
        this.z = t2.z < min ? min : t2.z;
    }

    public final void clampMax(float max, Tuple3d t2) {
        this.clampMax((double)max, t2);
    }

    public final void clampMax(double max, Tuple3d t2) {
        this.x = t2.x > max ? max : t2.x;
        this.y = t2.y > max ? max : t2.y;
        this.z = t2.z > max ? max : t2.z;
    }

    public final void absolute(Tuple3d t2) {
        this.x = Math.abs(t2.x);
        this.y = Math.abs(t2.y);
        this.z = Math.abs(t2.z);
    }

    public final void clamp(float min, float max) {
        this.clamp((double)min, (double)max);
    }

    public final void clamp(double min, double max) {
        if (this.x > max) {
            this.x = max;
        } else if (this.x < min) {
            this.x = min;
        }
        if (this.y > max) {
            this.y = max;
        } else if (this.y < min) {
            this.y = min;
        }
        if (this.z > max) {
            this.z = max;
        } else if (this.z < min) {
            this.z = min;
        }
    }

    public final void clampMin(float min) {
        this.clampMin((double)min);
    }

    public final void clampMin(double min) {
        if (this.x < min) {
            this.x = min;
        }
        if (this.y < min) {
            this.y = min;
        }
        if (this.z < min) {
            this.z = min;
        }
    }

    public final void clampMax(float max) {
        this.clampMax((double)max);
    }

    public final void clampMax(double max) {
        if (this.x > max) {
            this.x = max;
        }
        if (this.y > max) {
            this.y = max;
        }
        if (this.z > max) {
            this.z = max;
        }
    }

    public final void absolute() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        this.z = Math.abs(this.z);
    }

    public final void interpolate(Tuple3d t1, Tuple3d t2, float alpha) {
        this.interpolate(t1, t2, (double)alpha);
    }

    public final void interpolate(Tuple3d t1, Tuple3d t2, double alpha) {
        this.x = (1.0 - alpha) * t1.x + alpha * t2.x;
        this.y = (1.0 - alpha) * t1.y + alpha * t2.y;
        this.z = (1.0 - alpha) * t1.z + alpha * t2.z;
    }

    public final void interpolate(Tuple3d t1, float alpha) {
        this.interpolate(t1, (double)alpha);
    }

    public final void interpolate(Tuple3d t1, double alpha) {
        this.x = (1.0 - alpha) * this.x + alpha * t1.x;
        this.y = (1.0 - alpha) * this.y + alpha * t1.y;
        this.z = (1.0 - alpha) * this.z + alpha * t1.z;
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    public final double getX() {
        return this.x;
    }

    public final void setX(double x2) {
        this.x = x2;
    }

    public final double getY() {
        return this.y;
    }

    public final void setY(double y2) {
        this.y = y2;
    }

    public final double getZ() {
        return this.z;
    }

    public final void setZ(double z) {
        this.z = z;
    }
}

