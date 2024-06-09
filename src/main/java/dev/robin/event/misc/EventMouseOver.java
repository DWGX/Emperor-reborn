package dev.robin.event.misc;

import dev.robin.event.api.events.Event;

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

