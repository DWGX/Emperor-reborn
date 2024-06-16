package dev.emperor.module.modules.combat;

import dev.emperor.Client;
import dev.emperor.event.EventTarget;
import dev.emperor.event.world.EventPacketProcess;
import dev.emperor.event.world.EventPacketSend;
import dev.emperor.event.world.EventUpdate;
import dev.emperor.event.world.EventWorldLoad;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.utils.ChatUtil;
import dev.emperor.utils.client.PacketUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.List;

/**
 * @author EzDiaoL
 * @since 16.06.2024
 */
public class AutoGapple extends Module {

    public AutoGapple() {
        super("AutoGapple", Category.Combat);
    }

    int i = 0;
    public List<Packet<?>> packets = new ArrayList<>();
    boolean velocityed = true;
    public boolean slow = false;


    @Override
    public void onEnable() {
        slow = true;
        new Thread(() -> {
            try {
                Thread.sleep(200);
                slow = false;
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
        packets.clear();
        i = 0;
        velocityed = false;
    }

    @Override
    public void onDisable() {
        slow = false;
        blink();
        velocityed = false;
    }

    @EventTarget
    public final void onPacket(final EventPacketProcess event) {
        Packet<?> packet = event.getPacket();

        if (!PacketUtil.isEssential(packet) && PacketUtil.isCPacket(packet)) {
            event.setCancelled(true);
            packets.add(packet);
        }

        if (packet instanceof C02PacketUseEntity && (Client.instance.moduleManager.getModule(KillAura.class)).target.hurtTime <= 3 && (Client.instance.moduleManager.getModule(KillAura.class)).target != null) {
            send();
        }

        i = 0;
        for (Packet<?> index : packets) {
            if (index instanceof C03PacketPlayer) {
                i++;
            }
        }
    }

    @EventTarget
    public final void onUpdate(final EventUpdate event) {

        if (i >= 37) {
            i = 0;

            int targetSlot = getItemFromHotbar(322);
            if (targetSlot != -1) {
                if (mc.thePlayer.inventory.currentItem != targetSlot) {
                    mc.getNetHandler().addToSendQueueUnregistered(new C09PacketHeldItemChange(targetSlot));
                    mc.getNetHandler().addToSendQueueUnregistered(new C08PacketPlayerBlockPlacement());
                    mc.getNetHandler().addToSendQueueUnregistered(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    blink();
                    mc.getNetHandler().addToSendQueueUnregistered(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                } else {
                    mc.getNetHandler().addToSendQueueUnregistered(new C08PacketPlayerBlockPlacement());
                    mc.getNetHandler().addToSendQueueUnregistered(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    blink();
                }
            } else {
                blink();
            }
            packets.clear();
            this.setState(false);

        }
        if (mc.thePlayer.isDead) {
            this.setState(false);
        }
    }

    @EventTarget
    public final void onWorld(final EventWorldLoad event) {
        this.setState(false);
    }

    void send() {
        if (packets.isEmpty())
            return;

        Packet<?> packet = packets.get(0);
        packets.remove(0);
        if (packet instanceof C09PacketHeldItemChange || (packet instanceof C07PacketPlayerDigging && ((C07PacketPlayerDigging) packet).getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM)) {
            send();
            return;
        }
        mc.getNetHandler().addToSendQueueUnregistered(packet);
        if (!(packet instanceof C02PacketUseEntity)) {
            send();
        }
    }

    void blink() {
        if (packets.isEmpty())
            return;
        while (!packets.isEmpty()) {
            Packet<?> packet = packets.get(0);
            packets.remove(0);
            if (packet instanceof C09PacketHeldItemChange || (packet instanceof C07PacketPlayerDigging && ((C07PacketPlayerDigging) packet).getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM))
                continue;
            mc.getNetHandler().addToSendQueueUnregistered(packet);
        }
    }

    private int getItemFromHotbar(int id) {
        for (int i = 0; i <= 8; i++) {
            ItemStack a = mc.thePlayer.inventory.mainInventory[i];
            if (a != null) {
                Item item = a.getItem();
                if (Item.getIdFromItem(item) == id) {
                    return i;
                }
            }
        }
        return -1;
    }

}