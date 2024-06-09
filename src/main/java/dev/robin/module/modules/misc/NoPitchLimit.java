package dev.robin.module.modules.misc;

import dev.robin.event.EventTarget;
import dev.robin.event.world.EventPacketSend;
import dev.robin.module.Category;
import dev.robin.module.Module;
import dev.robin.module.values.BoolValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

public class NoPitchLimit
extends Module {
    private final BoolValue serverSideValue = new BoolValue("ServerSide", true);

    public NoPitchLimit() {
        super("NoPitchLimit", Category.Misc);
    }

    @EventTarget
    public void onPacketSend(EventPacketSend e) {
        if (((Boolean)this.serverSideValue.getValue()).booleanValue()) {
            return;
        }
        Packet packet = e.packet;
        if (packet instanceof C03PacketPlayer) {
            ((C03PacketPlayer)packet).pitch = Math.max(Math.min(((C03PacketPlayer)packet).pitch, 90.0f), -90.0f);
        }
    }
}

