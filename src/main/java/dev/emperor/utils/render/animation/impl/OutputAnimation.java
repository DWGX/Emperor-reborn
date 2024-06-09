package dev.emperor.utils.render.animation.impl;

import dev.emperor.utils.render.animation.AnimationUtils;

public class OutputAnimation {
    private double now;

    public OutputAnimation(int now) {
        this.now = now;
    }

    public void animate(double target, float speed) {
        this.now = AnimationUtils.animate(target, this.now, speed);
    }

    public double getOutput() {
        return this.now;
    }

    public void setNow(int now) {
        this.now = now;
    }
}

