package dev.emperor.event.api.events.callables;

import dev.emperor.event.api.events.Event;
import dev.emperor.event.api.events.Typed;

public abstract class EventTyped
implements Event,
Typed {
    private final byte type;

    protected EventTyped(byte eventType) {
        this.type = eventType;
    }

    @Override
    public byte getType() {
        return this.type;
    }
}

