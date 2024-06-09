package dev.robin.utils.component;

import dev.robin.Client;
import dev.robin.event.EventTarget;
import dev.robin.event.world.EventPacketSend;
import dev.robin.module.modules.player.Blink;
import dev.robin.module.modules.player.Blink1;
import dev.robin.utils.client.PacketUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;


public class BlinkComponent {
    private final Minecraft mc = Minecraft.getMinecraft();
    private static final Queue<Packet<?>> storedPackets = new ConcurrentLinkedQueue();
    private static final Set<Class<? extends Packet<?>>> alwaysStorePackets = new CopyOnWriteArraySet();
    private static final Set<Class<? extends Packet<?>>> exemptPackets = new CopyOnWriteArraySet();
    private static final Set<Class<? extends Packet<?>>> onlyCancelPackets = new CopyOnWriteArraySet();
    private static boolean blinking = false;
    private static boolean storeAllPackets = false;
    public static void toggle(boolean storeAll) {
        if (blinking) {
            stop();
        } else {
            start(storeAll);
        }

    }

    public static void start(boolean storeAll) {
        blinking = true;
        storeAllPackets = storeAll;
    }

    public static void stop() {
        blinking = false;
        storeAllPackets = false;
        releaseStoredPackets();
        alwaysStorePackets.clear();
        exemptPackets.clear();
        onlyCancelPackets.clear();
    }

    public void addAlwaysStorePacket(Class<? extends Packet<?>> packetClass) {
        alwaysStorePackets.add(packetClass);
    }

    public static void addExemptPacket(Class<? extends Packet<?>> packetClass) {
        exemptPackets.add(packetClass);
    }

    public void addOnlyCancelPacket(Class<? extends Packet<?>> packetClass) {
        onlyCancelPackets.add(packetClass);
    }
    @EventTarget
    public void onPacketSend(EventPacketSend event) {
        if (blinking) {
            if (mc.thePlayer.isDead || mc.isSingleplayer() || !mc.getNetHandler().isDoneLoadingTerrain()) {
                stop();
            }

            Packet<?> packet = event.getPacket();
            if (onlyCancelPackets.contains(packet.getClass())) {
                event.setCancelled(true);
            } else if ((storeAllPackets || alwaysStorePackets.contains(packet.getClass())) && !exemptPackets.contains(packet.getClass()) && !event.isCancelled()) {
                storedPackets.add(packet);
                event.setCancelled(true);
            }
        }
    }
    private static void releaseStoredPackets() {
        while(!storedPackets.isEmpty()) {
            Packet<?> packet = (Packet)storedPackets.poll();
            PacketUtil.sendPacketNoEvent(packet);
        }

    }

    public static void releaseStoredPackets(int count) {
        for(int released = 0; !storedPackets.isEmpty() && released < count; ++released) {
            Packet<?> packet = (Packet)storedPackets.poll();
            if ((Client.instance.moduleManager.getModule(Blink1.class)).getState() && packet instanceof C03PacketPlayer) {
                C03PacketPlayer wrapper = (C03PacketPlayer)packet;
                ((Blink1)Client.instance.moduleManager.getModule(Blink1.class)).updateEntityPosition(wrapper.x, wrapper.y, wrapper.z, wrapper.getYaw(), wrapper.getPitch());
            }
            PacketUtil.sendPacketNoEvent(packet);
        }

    }
}
