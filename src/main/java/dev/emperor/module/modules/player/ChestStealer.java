package dev.emperor.module.modules.player;

import dev.emperor.event.EventTarget;
import dev.emperor.event.world.EventUpdate;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.values.BoolValue;
import dev.emperor.module.values.NumberValue;
import dev.emperor.utils.HYTUtils;
import dev.emperor.utils.client.TimeUtil;
import dev.emperor.utils.item.ItemUtils;
import java.util.Random;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MathHelper;

public class ChestStealer
extends Module {
    private final BoolValue chest = new BoolValue("Chest", true);
    private final BoolValue furnace = new BoolValue("Furnace", true);
    private final BoolValue brewingStand = new BoolValue("BrewingStand", true);
    public static final TimeUtil timer = new TimeUtil();
    public static boolean isChest = false;
    public static TimeUtil openChestTimer = new TimeUtil();
    private final NumberValue delay = new NumberValue("StealDelay", 0.0, 0.0, 1000.0, 10.0);
    private final BoolValue trash = new BoolValue("PickTrash", true);
    public final BoolValue silentValue = new BoolValue("Silent", true);
    private int nextDelay = 0;

    public ChestStealer() {
        super("ChestStealer", Category.Player);
    }

    @EventTarget
    public void onEventUpdate(EventUpdate event) {
        int i;
        Container container;
        if (HYTUtils.isInLobby()) {
            return;
        }
        if (ChestStealer.mc.thePlayer.openContainer == null) {
            return;
        }
        if (ChestStealer.mc.thePlayer.openContainer instanceof ContainerFurnace && ((Boolean)this.furnace.getValue()).booleanValue()) {
            container = (ContainerFurnace)ChestStealer.mc.thePlayer.openContainer;
            if (this.isFurnaceEmpty((ContainerFurnace)container) && openChestTimer.delay(100.0f) && timer.delay(100.0f)) {
                ChestStealer.mc.thePlayer.closeScreen();
                return;
            }
            for (i = 0; i < ((ContainerFurnace)container).tileFurnace.getSizeInventory(); ++i) {
                if (((ContainerFurnace)container).tileFurnace.getStackInSlot(i) == null || !timer.delay(this.nextDelay)) continue;
                this.nextDelay = (int)((Double)this.delay.getValue() * MathHelper.getRandomDoubleInRange(0.75, 1.25));
                if (new Random().nextInt(100) > 80) continue;
                ChestStealer.mc.playerController.windowClick(((ContainerFurnace)container).windowId, i, 0, 1, ChestStealer.mc.thePlayer);
                timer.reset();
            }
        }
        if (ChestStealer.mc.thePlayer.openContainer instanceof ContainerBrewingStand && ((Boolean)this.brewingStand.getValue()).booleanValue()) {
            container = (ContainerBrewingStand)ChestStealer.mc.thePlayer.openContainer;
            if (this.isBrewingStandEmpty((ContainerBrewingStand)container) && openChestTimer.delay(100.0f) && timer.delay(100.0f)) {
                ChestStealer.mc.thePlayer.closeScreen();
                return;
            }
            for (i = 0; i < ((ContainerBrewingStand)container).tileBrewingStand.getSizeInventory(); ++i) {
                if (((ContainerBrewingStand)container).tileBrewingStand.getStackInSlot(i) == null || !timer.delay(this.nextDelay)) continue;
                this.nextDelay = (int)((Double)this.delay.getValue() * MathHelper.getRandomDoubleInRange(0.75, 1.25));
                if (new Random().nextInt(100) > 80) continue;
                ChestStealer.mc.playerController.windowClick(((ContainerBrewingStand)container).windowId, i, 0, 1, ChestStealer.mc.thePlayer);
                timer.reset();
            }
        }
        if (ChestStealer.mc.thePlayer.openContainer instanceof ContainerChest && ((Boolean)this.chest.getValue()).booleanValue() && isChest) {
            container = (ContainerChest)ChestStealer.mc.thePlayer.openContainer;
            if (this.isChestEmpty((ContainerChest)container) && openChestTimer.delay(100.0f) && timer.delay(100.0f)) {
                ChestStealer.mc.thePlayer.closeScreen();
                return;
            }
            for (i = 0; i < ((ContainerChest)container).getLowerChestInventory().getSizeInventory(); ++i) {
                if (((ContainerChest)container).getLowerChestInventory().getStackInSlot(i) == null || !timer.delay(this.nextDelay) || !this.isItemUseful((ContainerChest)container, i) && !((Boolean)this.trash.getValue()).booleanValue()) continue;
                this.nextDelay = (int)((Double)this.delay.getValue() * MathHelper.getRandomDoubleInRange(0.75, 1.25));
                if (new Random().nextInt(100) > 80) continue;
                ChestStealer.mc.playerController.windowClick(((ContainerChest)container).windowId, i, 0, 1, ChestStealer.mc.thePlayer);
                timer.reset();
            }
        }
    }

    private boolean isChestEmpty(ContainerChest c) {
        for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
            if (c.getLowerChestInventory().getStackInSlot(i) == null || !this.isItemUseful(c, i) && !((Boolean)this.trash.getValue()).booleanValue()) continue;
            return false;
        }
        return true;
    }

    private boolean isFurnaceEmpty(ContainerFurnace c) {
        for (int i = 0; i < c.tileFurnace.getSizeInventory(); ++i) {
            if (c.tileFurnace.getStackInSlot(i) == null) continue;
            return false;
        }
        return true;
    }

    private boolean isBrewingStandEmpty(ContainerBrewingStand c) {
        for (int i = 0; i < c.tileBrewingStand.getSizeInventory(); ++i) {
            if (c.tileBrewingStand.getStackInSlot(i) == null) continue;
            return false;
        }
        return true;
    }

    private boolean isItemUseful(ContainerChest c, int i) {
        ItemStack itemStack = c.getLowerChestInventory().getStackInSlot(i);
        Item item = itemStack.getItem();
        if (item instanceof ItemAxe || item instanceof ItemPickaxe) {
            return true;
        }
        if (item instanceof ItemFood) {
            return true;
        }
        if (item instanceof ItemBow || item == Items.arrow) {
            return true;
        }
        if (item instanceof ItemPotion && !ItemUtils.isPotionNegative(itemStack)) {
            return true;
        }
        if (item instanceof ItemSword && ItemUtils.isBestSword(c, itemStack)) {
            return true;
        }
        if (item instanceof ItemArmor && ItemUtils.isBestArmor(c, itemStack)) {
            return true;
        }
        if (item instanceof ItemBlock) {
            return true;
        }
        return item instanceof ItemEnderPearl;
    }
}

