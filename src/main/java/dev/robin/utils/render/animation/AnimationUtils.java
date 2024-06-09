package dev.robin.utils.render.animation;

public class AnimationUtils {
    public static float clamp(float number, float min, float max) {
        return number < min ? min : Math.min(number, max);
    }

    public static double animate(double target, double current, double speed) {
        boolean larger;
        if (current == target) {
            return current;
        }
        boolean bl = larger = target > current;
        if (speed < 0.0) {
            speed = 0.0;
        } else if (speed > 1.0) {
            speed = 1.0;
        }
        double dif = Math.max(target, current) - Math.min(target, current);
        double factor = dif * speed;
        if (factor < 0.1) {
            factor = 0.1;
        }
        if (larger) {
            if ((current += factor) >= target) {
                current = target;
            }
        } else if ((current -= factor) <= target) {
            current = target;
        }
        return current;
    }
}

