package dev.emperor.utils.render.animation.impl;

import dev.emperor.utils.render.animation.Animation;
import dev.emperor.utils.render.animation.Direction;

public class SmoothStepAnimation
extends Animation {
    public SmoothStepAnimation(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public SmoothStepAnimation(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }

    @Override
    protected double getEquation(double x2) {
        return -2.0 * Math.pow(x2, 3.0) + 3.0 * Math.pow(x2, 2.0);
    }
}

