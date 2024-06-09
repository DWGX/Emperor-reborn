package dev.emperor.utils.render.animation.impl;

import dev.emperor.utils.render.animation.Animation;
import dev.emperor.utils.render.animation.Direction;

public class ElasticAnimation
extends Animation {
    float easeAmount;
    float smooth;
    boolean reallyElastic;

    public ElasticAnimation(int ms, double endPoint, float elasticity, float smooth, boolean moreElasticity) {
        super(ms, endPoint);
        this.easeAmount = elasticity;
        this.smooth = smooth;
        this.reallyElastic = moreElasticity;
    }

    public ElasticAnimation(int ms, double endPoint, float elasticity, float smooth, boolean moreElasticity, Direction direction) {
        super(ms, endPoint, direction);
        this.easeAmount = elasticity;
        this.smooth = smooth;
        this.reallyElastic = moreElasticity;
    }

    @Override
    protected double getEquation(double x2) {
        x2 = Math.pow(x2, this.smooth);
        double elasticity = this.easeAmount * 0.1f;
        return Math.pow(2.0, -10.0 * (this.reallyElastic ? Math.sqrt(x2) : x2)) * Math.sin((x2 - elasticity / 4.0) * (Math.PI * 2 / elasticity)) + 1.0;
    }
}

