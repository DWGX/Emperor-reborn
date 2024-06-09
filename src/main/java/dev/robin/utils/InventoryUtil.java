package dev.robin.utils;

import com.google.common.collect.Multimap;
import dev.robin.utils.client.PacketUtil;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public final class InventoryUtil {
    public static final int INCLUDE_ARMOR_BEGIN = 5;
    public static final int EXCLUDE_ARMOR_BEGIN = 9;
    public static final int ONLY_HOT_BAR_BEGIN = 36;
    public static final int END = 45;

    private InventoryUtil() {
    }

    public static int findSlotMatching(EntityPlayerSP player, Predicate<ItemStack> cond) {
        for (int i = 44; i >= 9; --i) {
            ItemStack stack = player.inventoryContainer.getSlot(i).getStack();
            if (!cond.test(stack)) continue;
            return i;
        }
        return -1;
    }

    public static boolean hasFreeSlots(EntityPlayerSP player) {
        for (int i = 9; i < 45; ++i) {
            if (player.inventoryContainer.getSlot(i).getHasStack()) continue;
            return true;
        }
        return false;
    }

    public static boolean isValidStack(EntityPlayerSP player, ItemStack stack) {
        if (stack == null) {
            return false;
        }
        Item item = stack.getItem();
        if (item instanceof ItemSword) {
            return InventoryUtil.isBestSword(player, stack);
        }
        if (item instanceof ItemArmor) {
            return InventoryUtil.isBestArmor(player, stack);
        }
        if (item instanceof ItemTool) {
            return InventoryUtil.isBestTool(player, stack);
        }
        if (item instanceof ItemBow) {
            return InventoryUtil.isBestBow(player, stack);
        }
        if (item instanceof ItemFood) {
            return InventoryUtil.isGoodFood(stack);
        }
        if (item instanceof ItemBlock) {
            return InventoryUtil.isStackValidToPlace(stack);
        }
        if (item instanceof ItemPotion) {
            return InventoryUtil.isBuffPotion(stack);
        }
        return InventoryUtil.isGoodItem(item);
    }

    public static void swap(int slot, int switchSlot) {
        Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().thePlayer.inventoryContainer.windowId, slot, switchSlot, 2, Minecraft.getMinecraft().thePlayer);
    }

    public static boolean isGoodItem(Item item) {
        return item instanceof ItemEnderPearl || item == Items.arrow || item == Items.lava_bucket || item == Items.water_bucket;
    }

    public static boolean isBestSword(EntityPlayerSP player, ItemStack itemStack) {
        double damage = 0.0;
        ItemStack bestStack = null;
        for (int i = 9; i < 45; ++i) {
            double newDamage;
            ItemStack stack = player.inventoryContainer.getSlot(i).getStack();
            if (stack == null || !(stack.getItem() instanceof ItemSword) || !((newDamage = InventoryUtil.getItemDamage(stack)) > damage)) continue;
            damage = newDamage;
            bestStack = stack;
        }
        return bestStack == itemStack || InventoryUtil.getItemDamage(itemStack) > damage;
    }

    public static boolean isBestArmor(EntityPlayerSP player, ItemStack itemStack) {
        ItemArmor itemArmor = (ItemArmor)itemStack.getItem();
        double reduction = 0.0;
        ItemStack bestStack = null;
        for (int i = 5; i < 45; ++i) {
            double newReduction;
            ItemStack stack = player.inventoryContainer.getSlot(i).getStack();
            if (stack == null || !(stack.getItem() instanceof ItemArmor) || stack.getItem().getUnlocalizedName().equalsIgnoreCase("item.helmetChain") || stack.getItem().getUnlocalizedName().equalsIgnoreCase("item.leggingsChain")) continue;
            ItemArmor stackArmor = (ItemArmor)stack.getItem();
            if (stackArmor.armorType != itemArmor.armorType || !((newReduction = InventoryUtil.getDamageReduction(stack)) > reduction)) continue;
            reduction = newReduction;
            bestStack = stack;
        }
        return bestStack == itemStack || InventoryUtil.getDamageReduction(itemStack) > reduction;
    }

    public static int getToolType(ItemStack stack) {
        ItemTool tool = (ItemTool)stack.getItem();
        if (tool instanceof ItemPickaxe) {
            return 0;
        }
        if (tool instanceof ItemAxe) {
            return 1;
        }
        if (tool instanceof ItemSpade) {
            return 2;
        }
        return -1;
    }

    public static boolean isBestTool(EntityPlayerSP player, ItemStack itemStack) {
        int type = InventoryUtil.getToolType(itemStack);
        Tool bestTool = new Tool(-1, -1.0, null);
        for (int i = 9; i < 45; ++i) {
            double efficiency;
            ItemStack stack = player.inventoryContainer.getSlot(i).getStack();
            if (stack == null || !(stack.getItem() instanceof ItemTool) || type != InventoryUtil.getToolType(stack) || !((efficiency = (double)InventoryUtil.getToolEfficiency(stack)) > bestTool.getEfficiency())) continue;
            bestTool = new Tool(i, efficiency, stack);
        }
        return bestTool.getStack() == itemStack || (double)InventoryUtil.getToolEfficiency(itemStack) > bestTool.getEfficiency();
    }

    public static boolean isBestBow(EntityPlayerSP player, ItemStack itemStack) {
        double bestBowDmg = -1.0;
        ItemStack bestBow = null;
        for (int i = 9; i < 45; ++i) {
            double damage;
            ItemStack stack = player.inventoryContainer.getSlot(i).getStack();
            if (stack == null || !(stack.getItem() instanceof ItemBow) || !((damage = InventoryUtil.getBowDamage(stack)) > bestBowDmg)) continue;
            bestBow = stack;
            bestBowDmg = damage;
        }
        return itemStack == bestBow || InventoryUtil.getBowDamage(itemStack) > bestBowDmg;
    }

    public static double getDamageReduction(ItemStack stack) {
        double reduction = 0.0;
        ItemArmor armor = (ItemArmor)stack.getItem();
        reduction += (double)armor.damageReduceAmount;
        if (stack.isItemEnchanted()) {
            reduction += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 0.25;
        }
        return reduction;
    }

    public static boolean isBuffPotion(ItemStack stack) {
        ItemPotion potion = (ItemPotion)stack.getItem();
        List<PotionEffect> effects = potion.getEffects(stack);
        for (PotionEffect effect : effects) {
            if (!Potion.potionTypes[effect.getPotionID()].isBadEffect()) continue;
            return false;
        }
        return true;
    }

    public static double getBowDamage(ItemStack stack) {
        double damage = 0.0;
        if (stack.getItem() instanceof ItemBow && stack.isItemEnchanted()) {
            damage += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
        }
        return damage;
    }

    public static boolean isGoodFood(ItemStack stack) {
        ItemFood food = (ItemFood)stack.getItem();
        if (food instanceof ItemAppleGold) {
            return true;
        }
        return food.getHealAmount(stack) >= 4 && food.getSaturationModifier(stack) >= 0.3f;
    }

    public static float getToolEfficiency(ItemStack itemStack) {
        ItemTool tool = (ItemTool)itemStack.getItem();
        float efficiency = tool.getToolMaterial().getEfficiencyOnProperMaterial();
        int lvl = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack);
        if (efficiency > 1.0f && lvl > 0) {
            efficiency += (float)(lvl * lvl + 1);
        }
        return efficiency;
    }

    public static double getItemDamage(ItemStack stack) {
        double damage = 0.0;
        Multimap<String, AttributeModifier> attributeModifierMap = stack.getAttributeModifiers();
        for (String attributeName : attributeModifierMap.keySet()) {
            if (!attributeName.equals("generic.attackDamage")) continue;
            Iterator attributeModifiers = attributeModifierMap.get(((Object)attributeName).toString()).iterator();
            if (!attributeModifiers.hasNext()) break;
            damage += ((AttributeModifier)attributeModifiers.next()).getAmount();
            break;
        }
        if (stack.isItemEnchanted()) {
            damage += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack);
            damage += (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25;
        }
        return damage;
    }

    public static void windowClick(Minecraft mc, int windowId, int slotId, int mouseButtonClicked, ClickType mode2) {
        PacketUtil.sendPacketNoEvent(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
        mc.playerController.windowClick(windowId, slotId, mouseButtonClicked, mode2.ordinal(), mc.thePlayer);
        PacketUtil.sendPacketNoEvent(new C0DPacketCloseWindow(mc.thePlayer.inventoryContainer.windowId));
    }

    public static void windowClick(Minecraft mc, int slotId, int mouseButtonClicked, ClickType mode2) {
        PacketUtil.sendPacketNoEvent(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slotId, mouseButtonClicked, mode2.ordinal(), mc.thePlayer);
        PacketUtil.sendPacketNoEvent(new C0DPacketCloseWindow(mc.thePlayer.inventoryContainer.windowId));
    }

    public static boolean isStackValidToPlace(ItemStack stack) {
        return stack.stackSize >= 1 && InventoryUtil.validateBlock(Block.getBlockFromItem(stack.getItem()), BlockAction.PLACE);
    }

    public static boolean validateBlock(Block block, BlockAction action) {
        if (block instanceof BlockContainer) {
            return false;
        }
        Material material = block.getMaterial();
        switch (action) {
            case PLACE: {
                return !(block instanceof BlockFalling) && block.isFullBlock() && block.isFullCube();
            }
            case REPLACE: {
                return material.isReplaceable();
            }
            case PLACE_ON: {
                return block.isFullBlock() && block.isFullCube();
            }
        }
        return true;
    }

    private static class Tool {
        private final int slot;
        private final double efficiency;
        private final ItemStack stack;

        public Tool(int slot, double efficiency, ItemStack stack) {
            this.slot = slot;
            this.efficiency = efficiency;
            this.stack = stack;
        }

        public int getSlot() {
            return this.slot;
        }

        public double getEfficiency() {
            return this.efficiency;
        }

        public ItemStack getStack() {
            return this.stack;
        }
    }

    public static enum ClickType {
        CLICK,
        SHIFT_CLICK,
        SWAP_WITH_HOT_BAR_SLOT,
        PLACEHOLDER,
        DROP_ITEM;

    }

    public static enum BlockAction {
        PLACE,
        REPLACE,
        PLACE_ON;

    }
}

