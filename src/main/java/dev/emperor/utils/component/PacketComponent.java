package dev.emperor.utils.component;

import dev.emperor.Client;
import dev.emperor.utils.TimerUtil;
import dev.emperor.utils.client.PacketUtil;
import net.minecraft.network.Packet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PacketComponent {
    public static Queue<Packet<?>> incomingClientPackets = new ConcurrentLinkedQueue();
    public static Queue<Packet<?>> outgoingServerPackets = new ConcurrentLinkedQueue();
    public static final Map<String, Queue<Packet<?>>> customQueues = new HashMap();
    public static Packet<?> lastReleasedIncomingPacket;
    public static Packet<?> lastReleasedOutgoingPacket;
    public static TimerUtil timer = new TimerUtil();

    public void clear() {
        incomingClientPackets.clear();
        outgoingServerPackets.clear();
        lastReleasedIncomingPacket = null;
        lastReleasedOutgoingPacket = null;
    }

    public static void release() {
        if (!incomingClientPackets.isEmpty()) {
            releaseIncoming();
        }

        if (!outgoingServerPackets.isEmpty()) {
            releaseOutgoing();
        }

    }

    public static void releaseIncoming() {
        Client.instance.getExecutor().submit(() -> {
            Packet packet;
            while((packet = (Packet)incomingClientPackets.poll()) != null) {
                PacketUtil.sendPacketNoEvent(packet);
                lastReleasedIncomingPacket = packet;
            }

        });
    }

    public static void releaseOutgoing() {
        Client.instance.getExecutor().submit(() -> {
            Packet packet;
            while((packet = (Packet)outgoingServerPackets.poll()) != null) {
                PacketUtil.receivePacketNoEvent(packet);
                lastReleasedOutgoingPacket = packet;
            }

        });
    }

    public static void releaseIncoming(int count) {
        for(int i = 0; i < count && !incomingClientPackets.isEmpty(); ++i) {
            Packet<?> packet = (Packet)incomingClientPackets.poll();
            if (packet != null) {
                PacketUtil.sendPacketNoEvent(packet);
                lastReleasedIncomingPacket = packet;
            }
        }

    }

    public static void releaseOutgoing(int count) {
        for(int i = 0; i < count && !outgoingServerPackets.isEmpty(); ++i) {
            Packet<?> packet = (Packet)outgoingServerPackets.poll();
            if (packet != null) {
                PacketUtil.receivePacketNoEvent(packet);
                lastReleasedOutgoingPacket = packet;
            }
        }

    }

    public static void releaseIncomingHalf() {
        int packetsToRelease = incomingClientPackets.size() / 2;

        for(int i = 0; i < packetsToRelease; ++i) {
            Packet<?> packet = (Packet)incomingClientPackets.poll();
            if (packet != null) {
                PacketUtil.sendPacketNoEvent(packet);
                lastReleasedIncomingPacket = packet;
            }
        }

    }

    public static void releaseOutgoingHalf() {
        int packetsToRelease = outgoingServerPackets.size() / 2;

        for(int i = 0; i < packetsToRelease; ++i) {
            Packet<?> packet = (Packet)outgoingServerPackets.poll();
            if (packet != null) {
                PacketUtil.receivePacketNoEvent(packet);
                lastReleasedOutgoingPacket = packet;
            }
        }

    }

    public static void releaseIncoming(long duration) {
        long interval = duration / (long)(incomingClientPackets.size() / 2 + 1);

        while(true) {
            do {
                if (incomingClientPackets.isEmpty()) {
                    return;
                }
            } while(!timer.hasTimeElapsed(interval));

            for(int i = 0; i < 2 && !incomingClientPackets.isEmpty(); ++i) {
                Packet<?> packet = (Packet)incomingClientPackets.poll();
                if (packet != null) {
                    PacketUtil.sendPacketNoEvent(packet);
                    lastReleasedIncomingPacket = packet;
                }
            }

            timer.reset();
        }
    }

    public void addClientPacket(Packet<?> packet) {
        incomingClientPackets.add(packet);
    }

    public void addServerPacket(Packet<?> packet) {
        outgoingServerPackets.add(packet);
    }

    public boolean remove(Class<? extends Packet> packetClass) {
        boolean removedFromIncoming = this.remove(packetClass, incomingClientPackets);
        boolean removedFromOutgoing = this.remove(packetClass, outgoingServerPackets);
        return removedFromIncoming || removedFromOutgoing;
    }

    private boolean remove(Class<? extends Packet> packetClass, Queue<?> queue) {
        boolean removed = false;
        Iterator iterator = queue.iterator();

        while(iterator.hasNext()) {
            Object packet = iterator.next();
            if (packetClass.isInstance(packet)) {
                iterator.remove();
                removed = true;
            }
        }

        return removed;
    }

    public static void createQueue(String queueId) {
        customQueues.put(queueId, new ConcurrentLinkedQueue());
    }

    public static void addPacket(String queueId, Packet<?> packet) {
        Queue<Packet<?>> queue = (Queue)customQueues.get(queueId);
        if (queue != null) {
            queue.add(packet);
        }

    }

    public static void releasePackets(String queueId) {
        Queue<Packet<?>> queue = (Queue)customQueues.get(queueId);
        if (queue != null) {
            while(!queue.isEmpty()) {
                Packet<?> packet = (Packet)queue.poll();
                if (packet != null) {
                    PacketUtil.sendPacketNoEvent(packet);
                }
            }
        }

    }

    public static void destroyQueue(String queueId) {
        customQueues.remove(queueId);
    }
}
