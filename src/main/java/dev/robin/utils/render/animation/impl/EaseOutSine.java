package dev.robin.utils.render.animation.impl;

import dev.robin.utils.render.animation.Animation;
import dev.robin.utils.render.animation.Direction;

public class EaseOutSine
extends Animation {
    public EaseOutSine(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public EaseOutSine(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }

    @Override
    protected boolean correctOutput() {
        return true;
    }

    @Override
    protected double getEquation(double x2) {
        return Math.sin(x2 * 1.5707963267948966);
    }
}

