package dev.robin.event.rendering;

import dev.robin.event.api.events.callables.EventCancellable;
import net.minecraft.entity.Entity;

public class EventRenderNameTag
extends EventCancellable {
    private static Entity target;

    public EventRenderNameTag(Entity entity) {
        target = entity;
    }

    public Entity getTarget() {
        return target;
    }
}

