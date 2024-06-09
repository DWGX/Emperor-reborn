package dev.robin.event.misc;

import dev.robin.event.api.events.callables.EventCancellable;

public class EventChat
extends EventCancellable {
    private String message;

    public EventChat(String message) {
        this.message = message;
        this.setType((byte)0);
    }

    private void setType(byte b2) {
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

