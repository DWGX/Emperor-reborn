package dev.emperor.event.world;

import dev.emperor.event.api.events.callables.EventCancellable;

public class EventPlace
extends EventCancellable {
    private boolean shouldRightClick;
    private int slot;

    public EventPlace(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return this.slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public boolean isShouldRightClick() {
        return this.shouldRightClick;
    }

    public void setShouldRightClick(boolean shouldRightClick) {
        this.shouldRightClick = shouldRightClick;
    }
}

