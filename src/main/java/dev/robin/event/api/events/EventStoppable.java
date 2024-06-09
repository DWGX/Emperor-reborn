package dev.robin.event.api.events;

import dev.robin.event.api.events.Event;

public abstract class EventStoppable
implements Event {
    private boolean stopped;

    protected EventStoppable() {
    }

    public void stop() {
        this.stopped = true;
    }

    public boolean isStopped() {
        return this.stopped;
    }
}

