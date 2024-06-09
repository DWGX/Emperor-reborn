package dev.robin.event.world;

import dev.robin.event.api.events.callables.EventCancellable;
import net.minecraft.network.Packet;

public class EventPacketSend
extends EventCancellable {
    public Packet packet;

    public EventPacketSend(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return this.packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}

