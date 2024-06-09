package dev.emperor.event.world;

import dev.emperor.event.api.events.Event;

public class EventStep
implements Event {
    private double stepHeight;
    private final boolean pre;

    public EventStep(double stepHeight, boolean pre) {
        this.stepHeight = stepHeight;
        this.pre = pre;
    }

    public double getStepHeight() {
        return this.stepHeight;
    }

    public void setStepHeight(double stepHeight) {
        this.stepHeight = stepHeight;
    }

    public boolean isPre() {
        return this.pre;
    }

    public boolean isPost() {
        return !this.pre;
    }
}

