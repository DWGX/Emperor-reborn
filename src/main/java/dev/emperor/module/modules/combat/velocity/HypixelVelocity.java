package dev.emperor.module.modules.combat.velocity;

import dev.emperor.event.EventTarget;
import dev.emperor.event.attack.EventAttack;
import dev.emperor.event.world.EventPacketReceive;
import dev.emperor.event.world.EventPacketSend;
import dev.emperor.event.world.EventTick;
import dev.emperor.event.world.EventUpdate;
import dev.emperor.event.world.EventWorldLoad;
import dev.emperor.module.Category;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class HypixelVelocity
extends VelocityMode {
    public HypixelVelocity() {
        super("Hypixel", Category.Combat);
    }

    @Override
    public String getTag() {
        return "Watchdog";
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
            if (this.mc.thePlayer.onGround || (double)packetEntityVelocity.getMotionY() / 8000.0 < 0.2 || (double)packetEntityVelocity.getMotionY() / 8000.0 > 0.41995) {
                this.mc.thePlayer.motionY = (double)packetEntityVelocity.getMotionY() / 8000.0;
            }
        }
    }

    @Override
    public void onUpdate(EventUpdate event) {
    }

    @Override
    public void onTick(EventTick event) {
    }
}

