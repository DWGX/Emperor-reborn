package dev.emperor.event.misc;

import dev.emperor.event.api.events.Event;

public class EventSilentMove
implements Event {
    private boolean silent;
    private float yaw;
    private boolean advanced;

    public EventSilentMove(float yaw) {
        this.yaw = yaw;
    }

    public boolean isSilent() {
        return this.silent;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public boolean isAdvanced() {
        return this.advanced;
    }

    public void setAdvanced(boolean advanced) {
        this.advanced = advanced;
    }
}

