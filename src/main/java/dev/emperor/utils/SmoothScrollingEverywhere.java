package dev.emperor.utils;

import java.util.function.Function;
import net.minecraft.util.MathHelper;

public class SmoothScrollingEverywhere {
    private static Function<Double, Double> easingMethod = v -> v;

    public static Function<Double, Double> getEasingMethod() {
        return easingMethod;
    }

    public static long getScrollDuration() {
        return 600L;
    }

    public static float getScrollStep() {
        return 40.0f;
    }

    public static float getBounceBackMultiplier() {
        return 0.24f;
    }

    public static boolean isUnlimitFps() {
        return true;
    }

    public static float handleScrollingPosition(float[] target, float scroll, float maxScroll, float delta, double start, double duration) {
        if (SmoothScrollingEverywhere.getBounceBackMultiplier() >= 0.0f) {
            target[0] = SmoothScrollingEverywhere.clamp(target[0], maxScroll);
            if (target[0] < 0.0f) {
                target[0] = target[0] - target[0] * (1.0f - SmoothScrollingEverywhere.getBounceBackMultiplier()) * delta / 3.0f;
            } else if (target[0] > maxScroll) {
                target[0] = (target[0] - maxScroll) * (1.0f - (1.0f - SmoothScrollingEverywhere.getBounceBackMultiplier()) * delta / 3.0f) + maxScroll;
            }
        } else {
            target[0] = SmoothScrollingEverywhere.clamp(target[0], maxScroll, 0.0f);
        }
        if (!Precision.almostEquals(scroll, target[0], 0.001f)) {
            return SmoothScrollingEverywhere.expoEase(scroll, target[0], Math.min(((double)System.currentTimeMillis() - start) / duration * (double)delta * 3.0, 1.0));
        }
        return target[0];
    }

    public static float expoEase(float start, float end, double amount) {
        return start + (end - start) * SmoothScrollingEverywhere.getEasingMethod().apply(amount).floatValue();
    }

    public static double clamp(double v, double maxScroll) {
        return SmoothScrollingEverywhere.clamp(v, maxScroll, 300.0);
    }

    public static double clamp(double v, double maxScroll, double clampExtension) {
        return MathHelper.clamp_double(v, -clampExtension, maxScroll + clampExtension);
    }

    public static float clamp(float v, float maxScroll) {
        return SmoothScrollingEverywhere.clamp(v, maxScroll, 300.0f);
    }

    public static float clamp(float v, float maxScroll, float clampExtension) {
        return MathHelper.clamp_float(v, -clampExtension, maxScroll + clampExtension);
    }

    private static class Precision {
        public static final float FLOAT_EPSILON = 0.001f;
        public static final double DOUBLE_EPSILON = 1.0E-7;

        private Precision() {
        }

        public static boolean almostEquals(float value1, float value2, float acceptableDifference) {
            return Math.abs(value1 - value2) <= acceptableDifference;
        }

        public static boolean almostEquals(double value1, double value2, double acceptableDifference) {
            return Math.abs(value1 - value2) <= acceptableDifference;
        }
    }
}

