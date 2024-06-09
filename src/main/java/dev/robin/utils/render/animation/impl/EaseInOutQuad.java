package dev.robin.utils.render.animation.impl;

import dev.robin.utils.render.animation.Animation;
import dev.robin.utils.render.animation.Direction;

public class EaseInOutQuad
extends Animation {
    public EaseInOutQuad(int ms, double endPoint) {
        super(ms, endPoint);
    }

    public EaseInOutQuad(int ms, double endPoint, Direction direction) {
        super(ms, endPoint, direction);
    }

    @Override
    protected double getEquation(double x2) {
        return x2 < 0.5 ? 2.0 * Math.pow(x2, 2.0) : 1.0 - Math.pow(-2.0 * x2 + 2.0, 2.0) / 2.0;
    }
}

