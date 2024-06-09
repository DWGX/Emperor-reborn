package dev.robin.event.misc;

import dev.robin.event.api.events.callables.EventCancellable;
import net.minecraft.util.IChatComponent;

public class EventChatReceive
extends EventCancellable {
    public final byte type;
    public IChatComponent message;

    public EventChatReceive(byte type, IChatComponent message) {
        this.type = type;
        this.message = message;
    }
}

