package dev.robin.event.world;

import dev.robin.event.api.events.Event;
import net.minecraft.entity.Entity;

public class EventLivingUpdate
implements Event {
    public Entity entity;

    public EventLivingUpdate(Entity targetEntity) {
        this.entity = targetEntity;
    }

    public Entity getEntity() {
        return this.entity;
    }
}

