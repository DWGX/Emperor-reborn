package dev.robin.event.misc;

import dev.robin.event.api.events.callables.EventCancellable;
import net.minecraft.util.IChatComponent;

public class EventChatComponent
extends EventCancellable {
    private final IChatComponent chatComponent;

    public EventChatComponent(IChatComponent icc) {
        this.chatComponent = icc;
    }

    public IChatComponent getChatComponent() {
        return this.chatComponent;
    }
}

