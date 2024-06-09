package dev.robin.event.rendering;

import dev.robin.event.api.events.callables.EventCancellable;

public class EventShader
extends EventCancellable {
    private final boolean bloom;

    public EventShader(boolean bloom) {
        this.bloom = bloom;
    }

    public boolean isBloom() {
        return this.bloom;
    }
}

