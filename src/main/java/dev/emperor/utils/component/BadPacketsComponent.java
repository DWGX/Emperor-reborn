package dev.emperor.utils.component;

import dev.emperor.event.EventTarget;
import dev.emperor.event.world.EventPacketSend;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;

public final class BadPacketsComponent {
    private static boolean slot;
    private static boolean attack;
    private static boolean swing;
    private static boolean block;
    private static boolean inventory;

    public static boolean bad() {
        return BadPacketsComponent.bad(true, true, true, true, true);
    }

    public static boolean bad(boolean slot, boolean attack, boolean swing, boolean block, boolean inventory) {
        return BadPacketsComponent.slot && slot || BadPacketsComponent.attack && attack || BadPacketsComponent.swing && swing || BadPacketsComponent.block && block || BadPacketsComponent.inventory && inventory;
    }

    @EventTarget(value=4)
    public void onPacketSend(EventPacketSend event) {
        Packet packet = event.getPacket();
        if (packet instanceof C09PacketHeldItemChange) {
            slot = true;
        } else if (packet instanceof C0APacketAnimation) {
            swing = true;
        } else if (packet instanceof C02PacketUseEntity) {
            attack = true;
        } else if (packet instanceof C08PacketPlayerBlockPlacement || packet instanceof C07PacketPlayerDigging) {
            block = true;
        } else if (packet instanceof C0EPacketClickWindow || packet instanceof C16PacketClientStatus && ((C16PacketClientStatus)packet).getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT || packet instanceof C0DPacketCloseWindow) {
            inventory = true;
        } else if (packet instanceof C03PacketPlayer) {
            BadPacketsComponent.reset();
        }
    }

    public static void reset() {
        slot = false;
        swing = false;
        attack = false;
        block = false;
        inventory = false;
    }
}

