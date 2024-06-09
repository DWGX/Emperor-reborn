package dev.robin.utils.render.animation.impl;

import dev.robin.utils.render.animation.Animation;
import dev.robin.utils.render.animation.Direction;

public class EaseBackIn
extends Animation {
    private final float easeAmount;

    public EaseBackIn(int ms, double endPoint, float easeAmount) {
        super(ms, endPoint);
        this.easeAmount = easeAmount;
    }

    public EaseBackIn(int ms, double endPoint, float easeAmount, Direction direction) {
        super(ms, endPoint, direction);
        this.easeAmount = easeAmount;
    }

    @Override
    protected boolean correctOutput() {
        return true;
    }

    @Override
    protected double getEquation(double x2) {
        float shrink = this.easeAmount + 1.0f;
        return Math.max(0.0, 1.0 + (double)shrink * Math.pow(x2 - 1.0, 3.0) + (double)this.easeAmount * Math.pow(x2 - 1.0, 2.0));
    }
}

