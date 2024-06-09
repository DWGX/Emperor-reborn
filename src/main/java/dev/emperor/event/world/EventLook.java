package dev.emperor.event.world;

import dev.emperor.event.api.events.Event;
import org.lwjgl.compatibility.util.vector.Vector2f;

public class EventLook
implements Event {
    private Vector2f rotation;

    public EventLook(Vector2f rotation) {
        this.rotation = rotation;
    }

    public Vector2f getRotation() {
        return this.rotation;
    }

    public void setRotation(Vector2f rotation) {
        this.rotation = rotation;
    }
}

