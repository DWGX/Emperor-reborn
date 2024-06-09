package dev.robin.event.rendering;

import dev.robin.event.api.events.Event;
import net.minecraft.client.gui.ScaledResolution;

public class EventRender2D
implements Event {
    private float partialTicks;
    private ScaledResolution scaledResolution;

    public EventRender2D(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public ScaledResolution getScaledResolution() {
        return this.scaledResolution;
    }
}

