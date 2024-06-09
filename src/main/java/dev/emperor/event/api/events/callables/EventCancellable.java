package dev.emperor.event.api.events.callables;

import dev.emperor.event.api.events.Cancellable;
import dev.emperor.event.api.events.Event;

public abstract class EventCancellable
implements Event,
Cancellable {
    private boolean cancelled;

    protected EventCancellable() {
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean state) {
        this.cancelled = state;
    }

    @Override
    public void setCancelled() {
        this.cancelled = true;
    }
}

