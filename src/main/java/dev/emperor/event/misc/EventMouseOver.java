package dev.emperor.event.misc;

import dev.emperor.event.api.events.Event;

public class EventMouseOver
implements Event {
    private double range;

    public double getRange() {
        return this.range;
    }

    public void setRange(double range) {
        this.range = range;
    }
}

