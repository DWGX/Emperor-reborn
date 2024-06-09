package dev.emperor.event.misc;

import dev.emperor.event.api.events.callables.EventCancellable;
import lombok.Getter;

@Getter
public class EventSendChatMessage extends EventCancellable {
    String msg;

    public EventSendChatMessage(String msg) {
        this.msg = msg;
    }

}
