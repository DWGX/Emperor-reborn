package dev.robin.event.rendering;

import dev.robin.event.api.events.Event;

public class EventDrawText
implements Event {
    public String text;

    public EventDrawText(String text) {
        this.text = text;
    }
}

