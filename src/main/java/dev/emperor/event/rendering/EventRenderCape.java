package dev.emperor.event.rendering;

import dev.emperor.event.api.events.callables.EventCancellable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class EventRenderCape
extends EventCancellable {
    private final EntityPlayer player;
    private ResourceLocation capeLocation;

    public EventRenderCape(ResourceLocation capeLocation, EntityPlayer player) {
        this.capeLocation = capeLocation;
        this.player = player;
    }

    public ResourceLocation getLocation() {
        return this.capeLocation;
    }

    public void setLocation(ResourceLocation location) {
        this.capeLocation = location;
    }

    public EntityPlayer getPlayer() {
        return this.player;
    }
}

