package dev.emperor.utils.component;

import dev.emperor.Client;
import dev.emperor.event.EventTarget;
import dev.emperor.event.world.EventPacketReceive;
import dev.emperor.event.world.EventPacketSend;
import dev.emperor.event.world.EventUpdate;
import dev.emperor.event.world.EventWorldLoad;
import dev.emperor.utils.client.PacketUtil;
import dev.emperor.utils.client.TimeUtil;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

public final class PingSpoofComponent {
    public static final ConcurrentLinkedQueue<PacketUtil.TimedPacket> incomingPackets = new ConcurrentLinkedQueue();
    public static final ConcurrentLinkedQueue<PacketUtil.TimedPacket> outgoingPackets = new ConcurrentLinkedQueue();
    private static final TimeUtil stopWatch = new TimeUtil();
    public static boolean spoofing;
    public static int delay;
    public static boolean normal;
    public static boolean teleport;
    public static boolean velocity;
    public static boolean world;
    public static boolean entity;
    public static boolean client;

    @EventTarget(value=0)
    public void onUpdate(EventUpdate event) {
        for (PacketUtil.TimedPacket packet : incomingPackets) {
            if (System.currentTimeMillis() <= packet.getTime() + (long)(spoofing ? delay : 0)) continue;
            try {
                PacketUtil.receivePacketNoEvent(packet.getPacket());
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            incomingPackets.remove(packet);
        }
        for (PacketUtil.TimedPacket packet : outgoingPackets) {
            if (System.currentTimeMillis() <= packet.getTime() + (long)(spoofing ? delay : 0)) continue;
            try {
                PacketUtil.sendPacketNoEvent(packet.getPacket());
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            outgoingPackets.remove(packet);
        }
        if (stopWatch.delay(60.0f) || Client.mc.thePlayer.ticksExisted <= 20 || !Client.mc.getNetHandler().doneLoadingTerrain) {
            spoofing = false;
            for (PacketUtil.TimedPacket packet : incomingPackets) {
                PacketUtil.receivePacketNoEvent(packet.getPacket());
                incomingPackets.remove(packet);
            }
            for (PacketUtil.TimedPacket packet : outgoingPackets) {
                PacketUtil.sendPacketNoEvent(packet.getPacket());
                outgoingPackets.remove(packet);
            }
        }
    }

    @EventTarget(value=0)
    public void onWorldLoad(EventWorldLoad event) {
        incomingPackets.clear();
        stopWatch.reset();
        spoofing = false;
    }

    public static void dispatch() {
        for (PacketUtil.TimedPacket packet : incomingPackets) {
            try {
                PacketUtil.receivePacketNoEvent(packet.getPacket());
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            incomingPackets.remove(packet);
        }
        for (PacketUtil.TimedPacket packet : outgoingPackets) {
            PacketUtil.sendPacketNoEvent(packet.getPacket());
            outgoingPackets.remove(packet);
        }
    }

    public static void setSpoofing(int delay, boolean normal, boolean teleport, boolean velocity, boolean world, boolean entity) {
        spoofing = true;
        PingSpoofComponent.delay = delay;
        PingSpoofComponent.normal = normal;
        PingSpoofComponent.teleport = teleport;
        PingSpoofComponent.velocity = velocity;
        PingSpoofComponent.world = world;
        PingSpoofComponent.entity = entity;
        client = false;
        stopWatch.reset();
    }

    public static void setSpoofing(int delay, boolean normal, boolean teleport, boolean velocity, boolean world, boolean entity, boolean client) {
        spoofing = true;
        PingSpoofComponent.delay = delay;
        PingSpoofComponent.normal = normal;
        PingSpoofComponent.teleport = teleport;
        PingSpoofComponent.velocity = velocity;
        PingSpoofComponent.world = world;
        PingSpoofComponent.entity = entity;
        PingSpoofComponent.client = client;
        stopWatch.reset();
    }

    @EventTarget(value=0)
    public void onPacketSend(EventPacketSend event) {
        if (!client || !spoofing) {
            return;
        }
        Packet packet = event.getPacket();
        if (packet instanceof C03PacketPlayer || packet instanceof C16PacketClientStatus || packet instanceof C0DPacketCloseWindow || packet instanceof C0EPacketClickWindow || packet instanceof C0BPacketEntityAction || packet instanceof C02PacketUseEntity || packet instanceof C0APacketAnimation || packet instanceof C09PacketHeldItemChange || packet instanceof C18PacketSpectate || packet instanceof C19PacketResourcePackStatus || packet instanceof C17PacketCustomPayload || packet instanceof C15PacketClientSettings || packet instanceof C14PacketTabComplete || packet instanceof C07PacketPlayerDigging || packet instanceof C08PacketPlayerBlockPlacement) {
            outgoingPackets.add(new PacketUtil.TimedPacket(packet, System.currentTimeMillis()));
            event.setCancelled(true);
        }
    }

    @EventTarget(value=0)
    public void onPacketReceive(EventPacketReceive event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof S01PacketJoinGame) {
            incomingPackets.clear();
            stopWatch.reset();
            spoofing = false;
        }
        if (spoofing && Client.mc.getNetHandler().doneLoadingTerrain && ((packet instanceof S32PacketConfirmTransaction || packet instanceof S00PacketKeepAlive) && normal || (packet instanceof S08PacketPlayerPosLook || packet instanceof S09PacketHeldItemChange) && teleport || (packet instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity)packet).getEntityID() == Client.mc.thePlayer.getEntityId() || packet instanceof S27PacketExplosion) && velocity || (packet instanceof S26PacketMapChunkBulk || packet instanceof S21PacketChunkData || packet instanceof S23PacketBlockChange || packet instanceof S22PacketMultiBlockChange) && world || (packet instanceof S13PacketDestroyEntities || packet instanceof S14PacketEntity || packet instanceof S18PacketEntityTeleport || packet instanceof S20PacketEntityProperties || packet instanceof S19PacketEntityHeadLook) && entity)) {
            incomingPackets.add(new PacketUtil.TimedPacket(packet, System.currentTimeMillis()));
            event.setCancelled(true);
        }
    }

    static {
        client = true;
    }
}

