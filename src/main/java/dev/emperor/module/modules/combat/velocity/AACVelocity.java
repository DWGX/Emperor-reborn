package dev.emperor.module.modules.combat.velocity;

import dev.emperor.event.EventTarget;
import dev.emperor.event.attack.EventAttack;
import dev.emperor.event.world.EventPacketReceive;
import dev.emperor.event.world.EventPacketSend;
import dev.emperor.event.world.EventTick;
import dev.emperor.event.world.EventUpdate;
import dev.emperor.event.world.EventWorldLoad;
import dev.emperor.module.Category;
import dev.emperor.module.modules.combat.Velocity;
import dev.emperor.utils.client.PacketUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public class AACVelocity
extends VelocityMode {
    boolean shouldCancel;

    public AACVelocity() {
        super("AAC", Category.Combat);
    }

    @Override
    public String getTag() {
        return ((velMode)((Object)Velocity.aacModes.getValue())).name();
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
    public void onUpdate(EventUpdate event) {
    }

    @Override
    public void onTick(EventTick event) {
    }

    @Override
    @EventTarget
    public void onPacketReceive(EventPacketReceive e) {
        if (this.mc.thePlayer == null) {
            return;
        }
        Packet<?> packet = e.getPacket();
        if (e.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packetEntityVelocity = (S12PacketEntityVelocity)e.getPacket();
            if (packetEntityVelocity.getEntityID() != this.mc.thePlayer.getEntityId()) {
                return;
            }
            if (Velocity.aacModes.getValue() == velMode.AAC5_2_0) {
                PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, Double.MAX_VALUE, this.mc.thePlayer.posZ, true));
                e.setCancelled();
            }
        }
        if (Velocity.aacModes.getValue() == velMode.AAC5 && this.shouldCancel && packet instanceof S08PacketPlayerPosLook) {
            PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, 1.5E30, this.mc.thePlayer.posZ, true));
            PacketUtil.send(new C03PacketPlayer.C05PacketPlayerLook(((S08PacketPlayerPosLook)packet).getYaw(), ((S08PacketPlayerPosLook)packet).getPitch(), this.mc.thePlayer.onGround));
            this.shouldCancel = false;
        }
        if (e.getPacket() instanceof S27PacketExplosion) {
            if (Velocity.aacModes.getValue() == velMode.AAC5) {
                e.setCancelled();
                this.shouldCancel = true;
            }
            if (Velocity.aacModes.getValue() == velMode.AAC5_2_0) {
                PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, Double.MAX_VALUE, this.mc.thePlayer.posZ, true));
                e.setCancelled();
            }
        }
    }

    public static enum velMode {
        AAC5_2_0,
        AAC5;

    }
}

