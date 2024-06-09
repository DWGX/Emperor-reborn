package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityEnderChest;

public class InventoryEnderChest
extends InventoryBasic {
    private TileEntityEnderChest associatedChest;

    public InventoryEnderChest() {
        super("container.enderchest", false, 27);
    }

    public void setChestTileEntity(TileEntityEnderChest chestTileEntity) {
        this.associatedChest = chestTileEntity;
    }

    public void loadInventoryFromNBT(NBTTagList p_70486_1_) {
        for (int i = 0; i < this.getSizeInventory(); ++i) {
            this.setInventorySlotContents(i, null);
        }
        for (int k2 = 0; k2 < p_70486_1_.tagCount(); ++k2) {
            NBTTagCompound nbttagcompound = p_70486_1_.getCompoundTagAt(k2);
            int j2 = nbttagcompound.getByte("Slot") & 0xFF;
            if (j2 < 0 || j2 >= this.getSizeInventory()) continue;
            this.setInventorySlotContents(j2, ItemStack.loadItemStackFromNBT(nbttagcompound));
        }
    }

    public NBTTagList saveInventoryToNBT() {
        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < this.getSizeInventory(); ++i) {
            ItemStack itemstack = this.getStackInSlot(i);
            if (itemstack == null) continue;
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setByte("Slot", (byte)i);
            itemstack.writeToNBT(nbttagcompound);
            nbttaglist.appendTag(nbttagcompound);
        }
        return nbttaglist;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.associatedChest != null && !this.associatedChest.canBeUsed(player) ? false : super.isUseableByPlayer(player);
    }

    @Override
    public void openInventory(EntityPlayer player) {
        if (this.associatedChest != null) {
            this.associatedChest.openChest();
        }
        super.openInventory(player);
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        if (this.associatedChest != null) {
            this.associatedChest.closeChest();
        }
        super.closeInventory(player);
        this.associatedChest = null;
    }
}

