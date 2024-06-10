package dev.emperor.utils.component;

import dev.emperor.Client;
import dev.emperor.event.EventTarget;
import dev.emperor.event.world.EventPacketSend;
import dev.emperor.utils.client.PacketUtil;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import com.diaoling.client.viaversion.vialoadingbase.ViaLoadingBase;

public class InventoryClickFixComponent {
    @EventTarget
    public void onPacketSend(EventPacketSend event) {
        if (ViaLoadingBase.getInstance().getTargetVersion().getVersion() > 47 && (event.getPacket() instanceof C0EPacketClickWindow || event.getPacket() instanceof C0BPacketEntityAction || event.getPacket() instanceof C08PacketPlayerBlockPlacement) && (Client.mc.currentScreen instanceof GuiChest || Client.mc.currentScreen instanceof GuiInventory)) {
            PacketUtil.sendPacketC0F();
        }
    }
}

