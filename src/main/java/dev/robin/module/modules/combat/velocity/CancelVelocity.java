package dev.robin.module.modules.combat.velocity;

import dev.robin.event.EventTarget;
import dev.robin.event.attack.EventAttack;
import dev.robin.event.world.EventPacketReceive;
import dev.robin.event.world.EventPacketSend;
import dev.robin.event.world.EventTick;
import dev.robin.event.world.EventUpdate;
import dev.robin.event.world.EventWorldLoad;
import dev.robin.module.Category;
import dev.robin.module.modules.combat.velocity.VelocityMode;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public class CancelVelocity
extends VelocityMode {
    public CancelVelocity() {
        super("Cancel", Category.Combat);
    }

    @Override
    public String getTag() {
        return "Cancel";
    }

    @Override
    public void onAttack(EventAttack event) {
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onPacketSend(EventPacketSend event) {
    }

    @Override
    public void onWorldLoad(EventWorldLoad event) {
    }

    @Override
    @EventTarget
    public void onPacketReceive(EventPacketReceive e) {
        if (this.mc.thePlayer == null) {
            return;
        }
        if (e.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packetEntityVelocity = (S12PacketEntityVelocity)e.getPacket();
            if (packetEntityVelocity.getEntityID() != this.mc.thePlayer.getEntityId()) {
                return;
            }
            e.setCancelled(true);
        }
        if (e.getPacket() instanceof S27PacketExplosion) {
            e.setCancelled(true);
        }
    }

    @Override
    public void onUpdate(EventUpdate event) {
    }

    @Override
    public void onTick(EventTick event) {
    }
}

