package dev.robin.module.modules.player;

import dev.robin.event.EventTarget;
import dev.robin.event.world.EventMotion;
import dev.robin.event.world.EventPacketReceive;
import dev.robin.event.world.EventPacketSend;
import dev.robin.module.Category;
import dev.robin.module.Module;
import dev.robin.module.values.ModeValue;
import dev.robin.module.values.NumberValue;
import dev.robin.utils.HYTUtils;
import dev.robin.utils.InventoryUtil;
import dev.robin.utils.client.TimeUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.server.S2DPacketOpenWindow;

public class InvCleaner
extends Module {
    private final ModeValue<MODE> mode = new ModeValue("Mode", (Enum[])MODE.values(), (Enum)MODE.Spoof);
    private final NumberValue delay = new NumberValue("Delay", 5.0, 0.0, 300.0, 10.0);
    private final NumberValue armorDelay = new NumberValue("Armor Delay", 20.0, 0.0, 300.0, 10.0);
    public final NumberValue slotWeapon = new NumberValue("Weapon Slot", 1.0, 1.0, 9.0, 1.0);
    public final NumberValue slotPick = new NumberValue("Pickaxe Slot", 2.0, 1.0, 9.0, 1.0);
    public final NumberValue slotAxe = new NumberValue("Axe Slot", 3.0, 1.0, 9.0, 1.0);
    public final NumberValue slotGapple = new NumberValue("Gapple Slot", 4.0, 1.0, 9.0, 1.0);
    public final NumberValue slotShovel = new NumberValue("Shovel Slot", 5.0, 1.0, 9.0, 1.0);
    public final NumberValue slotBow = new NumberValue("Bow Slot", 6.0, 1.0, 9.0, 1.0);
    public final NumberValue slotBlock = new NumberValue("Block Slot", 7.0, 1.0, 9.0, 1.0);
    public final NumberValue slotPearl = new NumberValue("Pearl Slot", 8.0, 1.0, 9.0, 1.0);
    public final String[] serverItems = new String[]{"\u9009\u62e9\u6e38\u620f", "\u52a0\u5165\u6e38\u620f", "\u804c\u4e1a\u9009\u62e9\u83dc\u5355", "\u79bb\u5f00\u5bf9\u5c40", "\u518d\u6765\u4e00\u5c40", "selector", "tracking compass", "(right click)", "tienda ", "perfil", "salir", "shop", "collectibles", "game", "profil", "lobby", "show all", "hub", "friends only", "cofre", "(click", "teleport", "play", "exit", "hide all", "jeux", "gadget", " (activ", "emote", "amis", "bountique", "choisir", "choose "};
    private final int[] bestArmorPieces = new int[4];
    private final List<Integer> trash = new ArrayList<Integer>();
    private final int[] bestToolSlots = new int[3];
    private final List<Integer> gappleStackSlots = new ArrayList<Integer>();
    private int bestSwordSlot;
    private int bestPearlSlot;
    private int bestBowSlot;
    private boolean serverOpen;
    private boolean clientOpen;
    private int ticksSinceLastClick;
    private boolean nextTickCloseInventory;
    private TimeUtil timer = new TimeUtil();

    public InvCleaner() {
        super("InvManager", Category.World);
    }

    @EventTarget
    private void onPacket(EventPacketReceive event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof S2DPacketOpenWindow) {
            this.clientOpen = false;
            this.serverOpen = false;
        }
    }

    @EventTarget
    private void onPacketSend(EventPacketSend event) {
        Packet packet = event.getPacket();
        if (packet instanceof C16PacketClientStatus) {
            C16PacketClientStatus clientStatus = (C16PacketClientStatus)packet;
            if (clientStatus.getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
                this.clientOpen = true;
                this.serverOpen = true;
            }
        } else if (packet instanceof C0DPacketCloseWindow) {
            C0DPacketCloseWindow packetCloseWindow = (C0DPacketCloseWindow)packet;
            if (packetCloseWindow.windowId == InvCleaner.mc.thePlayer.inventoryContainer.windowId) {
                this.clientOpen = false;
                this.serverOpen = false;
            }
        } else if (packet instanceof C0EPacketClickWindow && !InvCleaner.mc.thePlayer.isUsingItem()) {
            this.ticksSinceLastClick = 0;
        }
    }

    private boolean dropItem(List<Integer> listOfSlots) {
        if (!listOfSlots.isEmpty()) {
            int slot = listOfSlots.remove(0);
            InventoryUtil.windowClick(mc, slot, 1, InventoryUtil.ClickType.DROP_ITEM);
            return true;
        }
        return false;
    }

    @EventTarget
    private void onMotion(EventMotion event) {
        if (HYTUtils.isInLobby()) {
            return;
        }
        if (event.isPost() && !InvCleaner.mc.thePlayer.isSpectator() && !InvCleaner.mc.thePlayer.isUsingItem() && (InvCleaner.mc.currentScreen == null || InvCleaner.mc.currentScreen instanceof GuiChat || InvCleaner.mc.currentScreen instanceof GuiInventory || InvCleaner.mc.currentScreen instanceof GuiIngameMenu)) {
            ++this.ticksSinceLastClick;
            if (this.clientOpen || InvCleaner.mc.currentScreen == null && !((MODE)((Object)this.mode.getValue())).name().equals("OpenInv")) {
                boolean busy;
                this.clear();
                for (int slot = 5; slot < 45; ++slot) {
                    ItemStack stack = InvCleaner.mc.thePlayer.inventoryContainer.getSlot(slot).getStack();
                    if (stack == null) continue;
                    if (stack.getItem() instanceof ItemSword && InventoryUtil.isBestSword(InvCleaner.mc.thePlayer, stack)) {
                        this.bestSwordSlot = slot;
                        continue;
                    }
                    if (stack.getItem() instanceof ItemTool && InventoryUtil.isBestTool(InvCleaner.mc.thePlayer, stack)) {
                        int toolType = InventoryUtil.getToolType(stack);
                        if (toolType == -1 || slot == this.bestToolSlots[toolType]) continue;
                        this.bestToolSlots[toolType] = slot;
                        continue;
                    }
                    if (stack.getItem() instanceof ItemArmor && InventoryUtil.isBestArmor(InvCleaner.mc.thePlayer, stack)) {
                        ItemArmor armor = (ItemArmor)stack.getItem();
                        int pieceSlot = this.bestArmorPieces[armor.armorType];
                        if (pieceSlot != -1 && slot == pieceSlot) continue;
                        this.bestArmorPieces[armor.armorType] = slot;
                        continue;
                    }
                    if (stack.getItem() instanceof ItemBow && InventoryUtil.isBestBow(InvCleaner.mc.thePlayer, stack)) {
                        if (slot == this.bestBowSlot) continue;
                        this.bestBowSlot = slot;
                        continue;
                    }
                    if (stack.getItem() instanceof ItemAppleGold) {
                        this.gappleStackSlots.add(slot);
                        continue;
                    }
                    if (stack.getItem() instanceof ItemEnderPearl) {
                        this.bestPearlSlot = slot;
                        continue;
                    }
                    if (this.trash.contains(slot) || InvCleaner.isValidStack(stack)) continue;
                    if (Arrays.stream(this.serverItems).anyMatch(stack.getDisplayName()::contains) || stack.getItem() instanceof ItemSkull) continue;
                    int swords2 = 0;
                    for (int i = 9; i < 45; ++i) {
                        ItemStack is = InvCleaner.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                        if (is == null || !(is.getItem() instanceof ItemSword)) continue;
                        ++swords2;
                    }
                    if (InvCleaner.mc.thePlayer.inventoryContainer.getSlot(slot).getStack().getItem() instanceof ItemSword && swords2 < 2) continue;
                    this.trash.add(slot);
                }
                boolean bl = busy = !this.trash.isEmpty() || this.equipArmor(false) || this.sortItems(false);
                if (!busy) {
                    if (this.nextTickCloseInventory) {
                        this.close();
                        this.nextTickCloseInventory = false;
                    } else {
                        this.nextTickCloseInventory = true;
                    }
                    return;
                }
                boolean waitUntilNextTick = !this.serverOpen;
                this.open();
                if (this.nextTickCloseInventory) {
                    this.nextTickCloseInventory = false;
                }
                if (waitUntilNextTick) {
                    return;
                }
                if (this.timer.delay(((Double)this.armorDelay.getValue()).floatValue()) && this.equipArmor(true)) {
                    return;
                }
                if (this.dropItem(this.trash)) {
                    return;
                }
                this.sortItems(true);
            }
        }
    }

    private boolean sortItems(boolean moveItems) {
        block14: {
            int mostBlocksSlot;
            int goodBlockSlot;
            block15: {
                int goodSwordSlot = ((Double)this.slotWeapon.getValue()).intValue() + 35;
                if (this.bestSwordSlot != -1 && this.bestSwordSlot != goodSwordSlot) {
                    if (moveItems) {
                        this.putItemInSlot(goodSwordSlot, this.bestSwordSlot);
                        this.bestSwordSlot = goodSwordSlot;
                    }
                    return true;
                }
                int goodBowSlot = ((Double)this.slotBow.getValue()).intValue() + 35;
                if (this.bestBowSlot != -1 && this.bestBowSlot != goodBowSlot) {
                    if (moveItems) {
                        this.putItemInSlot(goodBowSlot, this.bestBowSlot);
                        this.bestBowSlot = goodBowSlot;
                    }
                    return true;
                }
                int goodGappleSlot = ((Double)this.slotGapple.getValue()).intValue() + 35;
                if (!this.gappleStackSlots.isEmpty()) {
                    this.gappleStackSlots.sort(Comparator.comparingInt(slot -> InvCleaner.mc.thePlayer.inventoryContainer.getSlot((int)slot.intValue()).getStack().stackSize));
                    int bestGappleSlot = this.gappleStackSlots.get(0);
                    if (bestGappleSlot != goodGappleSlot) {
                        if (moveItems) {
                            this.putItemInSlot(goodGappleSlot, bestGappleSlot);
                            this.gappleStackSlots.set(0, goodGappleSlot);
                        }
                        return true;
                    }
                }
                int[] toolSlots = new int[]{((Double)this.slotPick.getValue()).intValue() + 35, ((Double)this.slotAxe.getValue()).intValue() + 35, ((Double)this.slotShovel.getValue()).intValue() + 35};
                for (int toolSlot : this.bestToolSlots) {
                    int type;
                    if (toolSlot == -1 || (type = InventoryUtil.getToolType(InvCleaner.mc.thePlayer.inventoryContainer.getSlot(toolSlot).getStack())) == -1 || toolSlot == toolSlots[type]) continue;
                    if (moveItems) {
                        this.putToolsInSlot(type, toolSlots);
                    }
                    return true;
                }
                goodBlockSlot = ((Double)this.slotBlock.getValue()).intValue() + 35;
                mostBlocksSlot = this.getMostBlocks();
                if (mostBlocksSlot == -1 || mostBlocksSlot == goodBlockSlot) break block14;
                Slot dss = InvCleaner.mc.thePlayer.inventoryContainer.getSlot(goodBlockSlot);
                ItemStack dsis = dss.getStack();
                if (dsis == null || !(dsis.getItem() instanceof ItemBlock) || dsis.stackSize < InvCleaner.mc.thePlayer.inventoryContainer.getSlot((int)mostBlocksSlot).getStack().stackSize) break block15;
                if (Arrays.stream(this.serverItems).noneMatch(dsis.getDisplayName().toLowerCase()::contains)) break block14;
            }
            this.putItemInSlot(goodBlockSlot, mostBlocksSlot);
        }
        int goodPearlSlot = ((Double)this.slotPearl.getValue()).intValue() + 35;
        if (this.bestPearlSlot != -1 && this.bestPearlSlot != goodPearlSlot) {
            if (moveItems) {
                this.putItemInSlot(goodPearlSlot, this.bestPearlSlot);
                this.bestPearlSlot = goodPearlSlot;
            }
            return true;
        }
        return false;
    }

    public int getMostBlocks() {
        int stack = 0;
        int biggestSlot = -1;
        for (int i = 9; i < 45; ++i) {
            Slot slot = InvCleaner.mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack is = slot.getStack();
            if (is == null || !(is.getItem() instanceof ItemBlock) || is.stackSize <= stack) continue;
            if (!Arrays.stream(this.serverItems).noneMatch(is.getDisplayName().toLowerCase()::contains)) continue;
            stack = is.stackSize;
            biggestSlot = i;
        }
        return biggestSlot;
    }

    private boolean equipArmor(boolean moveItems) {
        for (int i = 0; i < this.bestArmorPieces.length; ++i) {
            int armorPieceSlot;
            ItemStack stack;
            int piece = this.bestArmorPieces[i];
            if (piece == -1 || (stack = InvCleaner.mc.thePlayer.inventoryContainer.getSlot(armorPieceSlot = i + 5).getStack()) != null) continue;
            if (moveItems) {
                InventoryUtil.windowClick(mc, piece, 0, InventoryUtil.ClickType.SHIFT_CLICK);
            }
            this.timer.reset();
            return true;
        }
        return false;
    }

    private void putItemInSlot(int slot, int slotIn) {
        InventoryUtil.windowClick(mc, slotIn, slot - 36, InventoryUtil.ClickType.SWAP_WITH_HOT_BAR_SLOT);
    }

    private void putToolsInSlot(int tool, int[] toolSlots) {
        int toolSlot = toolSlots[tool];
        InventoryUtil.windowClick(mc, this.bestToolSlots[tool], toolSlot - 36, InventoryUtil.ClickType.SWAP_WITH_HOT_BAR_SLOT);
        this.bestToolSlots[tool] = toolSlot;
    }

    private static boolean isValidStack(ItemStack stack) {
        if (stack.getItem() instanceof ItemBlock && InventoryUtil.isStackValidToPlace(stack)) {
            return true;
        }
        if (stack.getItem() instanceof ItemPotion && InventoryUtil.isBuffPotion(stack)) {
            return true;
        }
        if (stack.getItem() instanceof ItemFood && InventoryUtil.isGoodFood(stack)) {
            return true;
        }
        return InventoryUtil.isGoodItem(stack.getItem());
    }

    @Override
    public void onEnable() {
        this.ticksSinceLastClick = 0;
        this.serverOpen = this.clientOpen = InvCleaner.mc.currentScreen instanceof GuiInventory;
    }

    @Override
    public void onDisable() {
        this.close();
        this.clear();
    }

    private void open() {
        if (!this.clientOpen && !this.serverOpen) {
            InvCleaner.mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
            this.serverOpen = true;
        }
    }

    private void close() {
        if (!this.clientOpen && this.serverOpen) {
            InvCleaner.mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(InvCleaner.mc.thePlayer.inventoryContainer.windowId));
            this.serverOpen = false;
        }
    }

    private void clear() {
        this.trash.clear();
        this.bestBowSlot = -1;
        this.bestSwordSlot = -1;
        this.gappleStackSlots.clear();
        Arrays.fill(this.bestArmorPieces, -1);
        Arrays.fill(this.bestToolSlots, -1);
    }

    public static enum MODE {
        OpenInv,
        Spoof;

    }
}

