package dev.emperor.module.modules.player;

import dev.emperor.Client;
import dev.emperor.event.EventTarget;
import dev.emperor.event.world.EventClickBlock;
import dev.emperor.event.world.EventMotion;
import dev.emperor.event.world.EventPacketSend;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.values.BoolValue;
import dev.emperor.utils.client.PacketUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;

public class AutoTool
extends Module {
    private final BoolValue spoofValue = new BoolValue("Spoof", false);
    private int blockBreak;
    private BlockPos blockPos;
    private int oldSlot = -1;
    int slot;
    public boolean render;

    public AutoTool() {
        super("AutoTool", Category.Player);
    }

    @EventTarget
    public void onClick(EventClickBlock event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        if (!((Boolean)this.spoofValue.getValue()).booleanValue()) {
            this.switchSlot(event.getClickedBlock());
        } else {
            this.blockBreak = 3;
            this.blockPos = event.getClickedBlock();
        }
    }

    @EventTarget
    public void onPacketSend(EventPacketSend event) {
        if (((Boolean)this.spoofValue.getValue()).booleanValue()) {
            Packet packet = event.getPacket();
            if (((Boolean)this.spoofValue.getValue()).booleanValue() && packet instanceof C07PacketPlayerDigging && ((C07PacketPlayerDigging)packet).getStatus() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK && this.oldSlot != -1 && AutoTool.mc.thePlayer.inventory.currentItem != this.oldSlot && this.blockBreak > 0) {
                event.setCancelled(true);
                PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(this.oldSlot));
                PacketUtil.sendPacketC0F();
                if (!Client.instance.moduleManager.getModule(SpeedMine.class).getState()) {
                    PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, ((C07PacketPlayerDigging)packet).getPosition(), ((C07PacketPlayerDigging)packet).getFacing()));
                    PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, ((C07PacketPlayerDigging)packet).getPosition(), ((C07PacketPlayerDigging)packet).getFacing()));
                }
                PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(AutoTool.mc.thePlayer.inventory.currentItem));
            }
            if (((Boolean)this.spoofValue.getValue()).booleanValue() && packet instanceof C09PacketHeldItemChange && this.blockBreak > 0 && (((C09PacketHeldItemChange)packet).getSlotId() == this.oldSlot || ((C09PacketHeldItemChange)packet).getSlotId() == AutoTool.mc.thePlayer.inventory.currentItem)) {
                event.setCancelled(true);
            }
            if (AutoTool.mc.objectMouseOver != null && AutoTool.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && AutoTool.mc.gameSettings.keyBindAttack.isKeyDown()) {
                this.blockBreak = 3;
                this.blockPos = AutoTool.mc.objectMouseOver.getBlockPos();
            }
        }
    }

    @EventTarget
    public void onMotion(EventMotion event) {
        if (((Boolean)this.spoofValue.getValue()).booleanValue() && event.isPre()) {
            switch (AutoTool.mc.objectMouseOver.typeOfHit) {
                case BLOCK: {
                    if (this.blockPos != null && this.blockBreak > 0) {
                        this.slot = this.findTool(this.blockPos);
                        break;
                    }
                    this.slot = -1;
                    break;
                }
                case ENTITY: {
                    this.slot = this.findSword();
                    break;
                }
                default: {
                    this.slot = -1;
                }
            }
            if (this.oldSlot != -1) {
                this.setSlot(this.oldSlot);
            } else if (this.slot != -1) {
                this.setSlot(this.slot);
            }
            this.oldSlot = this.slot;
            --this.blockBreak;
        }
    }

    public void setSlot(int slot, boolean render) {
        if (slot < 0 || slot > 8) {
            return;
        }
        AutoTool.mc.thePlayer.inventory.alternativeCurrentItem = slot;
        AutoTool.mc.thePlayer.inventory.alternativeSlot = true;
        render = this.render;
    }

    public void setSlot(int slot) {
        this.setSlot(slot, true);
    }

    public int findSword() {
        int bestDurability = -1;
        float bestDamage = -1.0f;
        int bestSlot = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = AutoTool.mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack == null || !(itemStack.getItem() instanceof ItemSword)) continue;
            ItemSword sword = (ItemSword)itemStack.getItem();
            int sharpnessLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, itemStack);
            float damage = sword.getDamageVsEntity() + (float)sharpnessLevel * 1.25f;
            int durability = sword.getMaxDamage();
            if (bestDamage < damage) {
                bestDamage = damage;
                bestDurability = durability;
                bestSlot = i;
            }
            if (damage != bestDamage || durability <= bestDurability) continue;
            bestDurability = durability;
            bestSlot = i;
        }
        return bestSlot;
    }

    public int findTool(BlockPos blockPos) {
        float bestSpeed = 1.0f;
        int bestSlot = -1;
        IBlockState blockState = AutoTool.mc.theWorld.getBlockState(blockPos);
        for (int i = 0; i < 9; ++i) {
            float speed;
            ItemStack itemStack = AutoTool.mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack == null || !((speed = itemStack.getStrVsBlock(blockState.getBlock())) > bestSpeed)) continue;
            bestSpeed = speed;
            bestSlot = i;
        }
        return bestSlot;
    }

    public void switchSlot(BlockPos blockPos) {
        float bestSpeed = 1.0f;
        int bestSlot = -1;
        Block block = AutoTool.mc.theWorld.getBlockState(blockPos).getBlock();
        for (int i = 0; i <= 8; ++i) {
            float speed;
            ItemStack item = AutoTool.mc.thePlayer.inventory.getStackInSlot(i);
            if (item == null || !((speed = item.getStrVsBlock(block)) > bestSpeed)) continue;
            bestSpeed = speed;
            bestSlot = i;
        }
        if (bestSlot != -1) {
            AutoTool.mc.thePlayer.inventory.currentItem = bestSlot;
        }
    }
}

