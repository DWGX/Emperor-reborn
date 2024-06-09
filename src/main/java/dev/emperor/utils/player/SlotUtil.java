package dev.emperor.utils.player;

import dev.emperor.Client;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public final class SlotUtil {
    public static final List<Block> blacklist = Arrays.asList(Blocks.enchanting_table, Blocks.chest, Blocks.ender_chest, Blocks.trapped_chest, Blocks.anvil, Blocks.sand, Blocks.web, Blocks.torch, Blocks.crafting_table, Blocks.furnace, Blocks.waterlily, Blocks.dispenser, Blocks.stone_pressure_plate, Blocks.wooden_pressure_plate, Blocks.noteblock, Blocks.dropper, Blocks.tnt, Blocks.standing_banner, Blocks.wall_banner, Blocks.redstone_torch);
    public static final List<Block> interactList = Arrays.asList(Blocks.enchanting_table, Blocks.chest, Blocks.ender_chest, Blocks.trapped_chest, Blocks.anvil, Blocks.crafting_table, Blocks.furnace, Blocks.dispenser, Blocks.iron_door, Blocks.oak_door, Blocks.noteblock, Blocks.dropper);

    public static int findBlock() {
        for (int i = 36; i < 45; ++i) {
            Block block;
            ItemStack item = Client.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (item == null || !(item.getItem() instanceof ItemBlock) || item.stackSize <= 0 || !(block = ((ItemBlock)item.getItem()).getBlock()).isFullBlock() && !(block instanceof BlockGlass) && !(block instanceof BlockStainedGlass) && !(block instanceof BlockTNT) || blacklist.contains(block)) continue;
            return i - 36;
        }
        return -1;
    }

    public static void setSlot(int slot) {
        SlotUtil.setSlot(slot, true);
    }

    public static void setSlot(int slot, boolean render) {
        if (slot < 0 || slot > 8) {
            return;
        }
        Client.mc.thePlayer.inventory.alternativeCurrentItem = slot;
        Client.mc.thePlayer.inventory.alternativeSlot = true;
    }

    public static ItemStack getItemStack() {
        return Client.mc.thePlayer == null || Client.mc.thePlayer.inventoryContainer == null ? null : Client.mc.thePlayer.inventoryContainer.getSlot(SlotUtil.getItemIndex() + 36).getStack();
    }

    public static int getItemIndex() {
        InventoryPlayer inventoryPlayer = Client.mc.thePlayer.inventory;
        return inventoryPlayer.currentItem;
    }

    public static int findSword() {
        int bestDurability = -1;
        float bestDamage = -1.0f;
        int bestSlot = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = Client.mc.thePlayer.inventory.getStackInSlot(i);
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

    public static int findItem(Item item) {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = Client.mc.thePlayer.inventory.getStackInSlot(i);
            if (!(itemStack == null ? item == null : itemStack.getItem() == item)) continue;
            return i;
        }
        return -1;
    }

    public static int findBlock(Block block) {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = Client.mc.thePlayer.inventory.getStackInSlot(i);
            if (!(itemStack == null ? block == null : itemStack.getItem() instanceof ItemBlock && ((ItemBlock)itemStack.getItem()).getBlock() == block)) continue;
            return i;
        }
        return -1;
    }

    public static int findTool(BlockPos blockPos) {
        float bestSpeed = 1.0f;
        int bestSlot = -1;
        IBlockState blockState = Client.mc.theWorld.getBlockState(blockPos);
        for (int i = 0; i < 9; ++i) {
            float speed;
            ItemStack itemStack = Client.mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack == null || !((speed = itemStack.getStrVsBlock(blockState.getBlock())) > bestSpeed)) continue;
            bestSpeed = speed;
            bestSlot = i;
        }
        return bestSlot;
    }

    public static ItemStack getCurrentItemInSlot(int slot) {
        return slot < 9 && slot >= 0 ? Client.mc.thePlayer.inventory.mainInventory[slot] : null;
    }

    public static float getStrVsBlock(Block blockIn, int slot) {
        float f = 1.0f;
        if (Client.mc.thePlayer.inventory.mainInventory[slot] != null) {
            f *= Client.mc.thePlayer.inventory.mainInventory[slot].getStrVsBlock(blockIn);
        }
        return f;
    }

    public static float getPlayerRelativeBlockHardness(EntityPlayer playerIn, World worldIn, BlockPos pos, int slot) {
        Block block = Client.mc.theWorld.getBlockState(pos).getBlock();
        float f = block.getBlockHardness(worldIn, pos);
        return f < 0.0f ? 0.0f : (!SlotUtil.canHeldItemHarvest(block, slot) ? SlotUtil.getToolDigEfficiency(block, slot) / f / 100.0f : SlotUtil.getToolDigEfficiency(block, slot) / f / 30.0f);
    }

    public static boolean canHeldItemHarvest(Block blockIn, int slot) {
        if (blockIn.getMaterial().isToolNotRequired()) {
            return true;
        }
        ItemStack itemstack = Client.mc.thePlayer.inventory.getStackInSlot(slot);
        return itemstack != null && itemstack.canHarvestBlock(blockIn);
    }

    public static float getToolDigEfficiency(Block blockIn, int slot) {
        float f = SlotUtil.getStrVsBlock(blockIn, slot);
        if (f > 1.0f) {
            int i = EnchantmentHelper.getEfficiencyModifier(Client.mc.thePlayer);
            ItemStack itemstack = SlotUtil.getCurrentItemInSlot(slot);
            if (i > 0 && itemstack != null) {
                f += (float)(i * i + 1);
            }
        }
        if (Client.mc.thePlayer.isPotionActive(Potion.digSpeed)) {
            f *= 1.0f + (float)(Client.mc.thePlayer.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2f;
        }
        if (Client.mc.thePlayer.isPotionActive(Potion.digSlowdown)) {
            float f1;
            switch (Client.mc.thePlayer.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) {
                case 0: {
                    f1 = 0.3f;
                    break;
                }
                case 1: {
                    f1 = 0.09f;
                    break;
                }
                case 2: {
                    f1 = 0.0027f;
                    break;
                }
                default: {
                    f1 = 8.1E-4f;
                }
            }
            f *= f1;
        }
        if (Client.mc.thePlayer.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(Client.mc.thePlayer)) {
            f /= 5.0f;
        }
        if (!Client.mc.thePlayer.onGround) {
            f /= 5.0f;
        }
        return f;
    }

    private SlotUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

