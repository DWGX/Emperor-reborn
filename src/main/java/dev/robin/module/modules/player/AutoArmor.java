package dev.robin.module.modules.player;

import dev.robin.event.EventTarget;
import dev.robin.event.world.EventTick;
import dev.robin.module.Category;
import dev.robin.module.Module;
import dev.robin.module.values.BoolValue;
import dev.robin.module.values.ModeValue;
import dev.robin.module.values.NumberValue;
import dev.robin.utils.client.TimeUtil;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C16PacketClientStatus;

public class AutoArmor
extends Module {
    public static NumberValue DELAY = new NumberValue("Delay", 1.0, 0.0, 10.0, 1.0);
    public static ModeValue<EMode> MODE = new ModeValue("Mode", (Enum[])EMode.values(), (Enum)EMode.Basic);
    private final BoolValue drop = new BoolValue("Drop", true);
    private final TimeUtil timer = new TimeUtil();

    public AutoArmor() {
        super("AutoArmor", Category.Player);
    }

    @EventTarget
    public void onEvent(EventTick event) {
        this.setSuffix(MODE.getValue());
        long delay = ((Double)DELAY.getValue()).longValue() * 50L;
        if (MODE.getValue() == EMode.OpenInv && !(AutoArmor.mc.currentScreen instanceof GuiInventory)) {
            return;
        }
        if ((AutoArmor.mc.currentScreen == null || AutoArmor.mc.currentScreen instanceof GuiInventory || AutoArmor.mc.currentScreen instanceof GuiChat) && this.timer.hasReached(delay)) {
            this.getBestArmor();
        }
    }

    public void getBestArmor() {
        for (int type = 1; type < 5; ++type) {
            if (AutoArmor.mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) {
                ItemStack is = AutoArmor.mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
                if (AutoArmor.isBestArmor(is, type)) continue;
                if (MODE.getValue() == EMode.FakeInv) {
                    C16PacketClientStatus p = new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT);
                    AutoArmor.mc.thePlayer.sendQueue.addToSendQueue(p);
                }
                if (((Boolean)this.drop.getValue()).booleanValue()) {
                    this.drop(4 + type);
                }
            }
            for (int i = 9; i < 45; ++i) {
                ItemStack is;
                if (!AutoArmor.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() || !AutoArmor.isBestArmor(is = AutoArmor.mc.thePlayer.inventoryContainer.getSlot(i).getStack(), type) || !(AutoArmor.getProtection(is) > 0.0f)) continue;
                this.shiftClick(i);
                this.timer.reset();
                if (((Double)DELAY.getValue()).longValue() <= 0L) continue;
                return;
            }
        }
    }

    public static boolean isBestArmor(ItemStack stack, int type) {
        String strType = "";
        switch (type) {
            case 1: {
                strType = "helmet";
                break;
            }
            case 2: {
                strType = "chestplate";
                break;
            }
            case 3: {
                strType = "leggings";
                break;
            }
            case 4: {
                strType = "boots";
            }
        }
        if (!stack.getUnlocalizedName().contains(strType)) {
            return false;
        }
        float protection = AutoArmor.getProtection(stack);
        if (((ItemArmor)stack.getItem()).getArmorMaterial() == ItemArmor.ArmorMaterial.CHAIN) {
            return false;
        }
        for (int i = 5; i < 45; ++i) {
            ItemStack is;
            if (!AutoArmor.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() || !(AutoArmor.getProtection(is = AutoArmor.mc.thePlayer.inventoryContainer.getSlot(i).getStack()) > protection) || !is.getUnlocalizedName().contains(strType)) continue;
            return false;
        }
        return true;
    }

    public void shiftClick(int slot) {
        AutoArmor.mc.playerController.windowClick(AutoArmor.mc.thePlayer.inventoryContainer.windowId, slot, 0, 1, AutoArmor.mc.thePlayer);
    }

    public void drop(int slot) {
        AutoArmor.mc.playerController.windowClick(AutoArmor.mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, AutoArmor.mc.thePlayer);
    }

    public static float getProtection(ItemStack stack) {
        float prot = 0.0f;
        if (stack.getItem() instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor)stack.getItem();
            prot = (float)((double)prot + ((double)armor.damageReduceAmount + (double)((100 - armor.damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack)) * 0.0075));
            prot = (float)((double)prot + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack) / 100.0);
            prot = (float)((double)prot + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack) / 100.0);
            prot = (float)((double)prot + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) / 100.0);
            prot = (float)((double)prot + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 50.0);
            prot = (float)((double)prot + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId, stack) / 100.0);
        }
        return prot;
    }

    public static enum EMode {
        Basic,
        OpenInv,
        FakeInv;

    }
}

