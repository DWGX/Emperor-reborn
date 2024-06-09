package dev.robin.utils.render.animation.impl;

import dev.robin.utils.render.animation.Animation;
import dev.robin.utils.render.animation.Direction;

public class DecelerateAnimation
extends Animation {
    public DecelerateAnimation(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public DecelerateAnimation(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }

    @Override
    protected double getEquation(double x2) {
        return 1.0 - (x2 - 1.0) * (x2 - 1.0);
    }
}

