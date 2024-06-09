package dev.emperor.event.rendering;

import dev.emperor.event.api.events.callables.EventCancellable;
import net.minecraft.entity.Entity;

public class EventRenderArm
extends EventCancellable {
    private final Entity entity;
    private final boolean pre;

    public EventRenderArm(Entity entity, boolean pre) {
        this.entity = entity;
        this.pre = pre;
    }

    public boolean isPre() {
        return this.pre;
    }

    public boolean isPost() {
        return !this.pre;
    }

    public Entity getEntity() {
        return this.entity;
    }
}

