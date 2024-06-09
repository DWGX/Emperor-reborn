package dev.emperor.event.rendering;

import dev.emperor.event.api.events.Event;

public class EventRender3D
implements Event {
    private static float ticks;

    public EventRender3D(float Ticks) {
        ticks = Ticks;
    }

    public float getPartialTicks() {
        return ticks;
    }
}

