package dev.robin.event.world;

import dev.robin.event.api.events.callables.EventCancellable;

public class EventSafeWalk
extends EventCancellable {
    public EventSafeWalk(boolean safeWalking) {
        this.setCancelled(safeWalking);
    }
}

