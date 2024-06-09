package dev.emperor.event.world;

import dev.emperor.event.api.events.callables.EventCancellable;

public class EventJump
extends EventCancellable {
    public float yaw;

    public EventJump(float yaw) {
        this.yaw = yaw;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}

