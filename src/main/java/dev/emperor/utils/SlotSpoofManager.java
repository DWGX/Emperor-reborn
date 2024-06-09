package dev.emperor.utils;

import dev.emperor.Client;
import net.minecraft.item.ItemStack;

public class SlotSpoofManager {
    private int spoofedSlot;
    private boolean spoofing;

    public void startSpoofing(int slot) {
        this.spoofing = true;
        this.spoofedSlot = slot;
    }

    public void stopSpoofing() {
        this.spoofing = false;
    }

    public int getSpoofedSlot() {
        return this.spoofing ? this.spoofedSlot : Client.mc.thePlayer.inventory.currentItem;
    }

    public ItemStack getSpoofedStack() {
        return this.spoofing ? Client.mc.thePlayer.inventory.getStackInSlot(this.spoofedSlot) : Client.mc.thePlayer.inventory.getCurrentItem();
    }

    public boolean isSpoofing() {
        return this.spoofing;
    }
}

