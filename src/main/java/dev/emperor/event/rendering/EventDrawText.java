package dev.emperor.event.rendering;

import dev.emperor.event.api.events.Event;

public class EventDrawText
implements Event {
    public String text;

    public EventDrawText(String text) {
        this.text = text;
    }
}

