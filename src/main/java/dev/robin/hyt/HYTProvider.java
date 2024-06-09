/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 *
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 */
package dev.robin.hyt;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import dev.robin.Client;
import dev.robin.event.EventTarget;
import dev.robin.event.misc.EventPacketCustom;
import dev.robin.hyt.game.GermModProcessor;
import dev.robin.hyt.games.HYTSelector;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

import java.util.Arrays;
import java.util.Objects;

public class HYTProvider {
    static GermModProcessor germModPacket = new GermModProcessor();

    public static void openUI() {
        Minecraft.getMinecraft().displayGuiScreen(new HYTSelector());
    }

    @EventTarget
    public static void onPacket(EventPacketCustom e) {
        S3FPacketCustomPayload packetIn = (S3FPacketCustomPayload) e.getPacket();
        PacketBuffer payload = packetIn.getBufferData();
        String payloadstr = payload.toString(Charsets.UTF_8);
        if (packetIn.getChannelName().equalsIgnoreCase("REGISTER") && payloadstr.startsWith("germmod-netease")) {
            String salutation = Joiner.on('\0')
                    .join(Arrays.asList("FML|HS", "FML", "FML|MP", "Forge", "armourers", "hyt0", "germplugin-netease", "VexView"));
            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            buffer.writeBytes(salutation.getBytes());
            Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacket(new C17PacketCustomPayload("REGISTER", buffer));
        }
        if (packetIn.getChannelName().equals("germplugin-netease")) {
            HYTWrapper.runOnMainThread(() -> germModPacket.process(payload));
        }
    }

    public static void sendOpenParty() {
        Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(3).writeInt(37).writeBoolean(true))));
    }
}

