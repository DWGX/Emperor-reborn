package dev.emperor.utils.render.animation.impl;

import dev.emperor.utils.render.animation.Animation;
import dev.emperor.utils.render.animation.Direction;

public class ContinualAnimation {
    private float output;
    private float endpoint;
    private Animation animation = new SmoothStepAnimation(0, 0.0, Direction.BACKWARDS);

    public void animate(float destination, int ms) {
        this.output = (float)((double)this.endpoint - this.animation.getOutput());
        this.endpoint = destination;
        if (this.output != this.endpoint - destination) {
            this.animation = new SmoothStepAnimation(ms, this.endpoint - this.output, Direction.BACKWARDS);
        }
    }

    public boolean isDone() {
        return this.output == this.endpoint || this.animation.isDone();
    }

    public float getOutput() {
        this.output = (float)((double)this.endpoint - this.animation.getOutput());
        return this.output;
    }

    public Animation getAnimation() {
        return this.animation;
    }
}

