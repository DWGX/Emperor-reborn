package dev.emperor.event.world;

import dev.emperor.event.api.events.callables.EventCancellable;

public class EventSafeWalk
extends EventCancellable {
    public EventSafeWalk(boolean safeWalking) {
        this.setCancelled(safeWalking);
    }
}

