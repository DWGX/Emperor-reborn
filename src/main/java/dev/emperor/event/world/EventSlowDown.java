package dev.emperor.event.world;

import dev.emperor.event.api.events.callables.EventCancellable;

public class EventSlowDown
extends EventCancellable {
    private final Type type;
    private float strafeMultiplier;
    private float forwardMultiplier;

    public EventSlowDown(Type type, float strafeMultiplier, float forwardMultiplier) {
        this.type = type;
        this.strafeMultiplier = strafeMultiplier;
        this.forwardMultiplier = forwardMultiplier;
    }

    public float getStrafeMultiplier() {
        return this.strafeMultiplier;
    }

    public float getForwardMultiplier() {
        return this.forwardMultiplier;
    }

    public void setStrafeMultiplier(float strafeMultiplier) {
        this.strafeMultiplier = strafeMultiplier;
    }

    public void setForwardMultiplier(float forwardMultiplier) {
        this.forwardMultiplier = forwardMultiplier;
    }

    public Type getType() {
        return this.type;
    }

    public static enum Type {
        Item,
        Sprinting,
        SoulSand,
        Water;

    }
}

