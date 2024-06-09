package dev.robin.module.modules.world;

import dev.robin.Client;
import dev.robin.event.EventTarget;
import dev.robin.event.rendering.EventRender3D;
import dev.robin.event.world.EventMotion;
import dev.robin.event.world.EventPlace;
import dev.robin.event.world.EventStrafe;
import dev.robin.event.world.EventTick;
import dev.robin.event.world.EventUpdate;
import dev.robin.module.Category;
import dev.robin.module.Module;
import dev.robin.module.values.BoolValue;
import dev.robin.utils.DebugUtil;
import dev.robin.utils.InventoryUtil;
import dev.robin.utils.Rotation;
import dev.robin.utils.RotationComponent;
import dev.robin.utils.client.MathUtil;
import dev.robin.utils.player.BlockUtil;
import dev.robin.utils.player.MoveUtil;
import dev.robin.utils.player.PlaceInfo;
import dev.robin.utils.player.PlaceRotation;
import dev.robin.utils.player.RotationUtil;
import dev.robin.utils.render.RenderUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.compatibility.util.vector.Vector2f;

public class Scaffold
extends Module {
    public static final List<Block> invalidBlocks = Arrays.asList(Blocks.enchanting_table, Blocks.furnace, Blocks.carpet, Blocks.crafting_table, Blocks.trapped_chest, Blocks.chest, Blocks.dispenser, Blocks.air, Blocks.water, Blocks.lava, Blocks.flowing_water, Blocks.flowing_lava, Blocks.snow_layer, Blocks.torch, Blocks.anvil, Blocks.jukebox, Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.noteblock, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.wooden_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_slab, Blocks.wooden_slab, Blocks.stone_slab2, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.yellow_flower, Blocks.red_flower, Blocks.anvil, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.cactus, Blocks.ladder, Blocks.web, Blocks.tnt);
    private static final BoolValue keepYValue = new BoolValue("Keep Y", false);
    public static Scaffold INSTANCE;
    public static double keepYCoord;
    public final BoolValue swing = new BoolValue("Swing", true);
    public final BoolValue sprintValue = new BoolValue("Sprint", false);
    public final BoolValue eagle = new BoolValue("Eagle", false);
    public final BoolValue safeValue = new BoolValue("Safe walk", false);
    public final BoolValue telly = new BoolValue("Telly", true);
    public final BoolValue upValue = new BoolValue("Up", false, () -> (Boolean)this.telly.getValue() != false && (Boolean)keepYValue.getValue() == false);
    public final BoolValue esp = new BoolValue("ESP", true);
    public boolean tip = false;
    public int vl = 0;
    protected Random rand = new Random();
    boolean idk = false;
    int idkTick = 0;
    int towerTick = 0;
    private int direction;
    private int slot;
    private BlockPos data;
    private boolean canTellyPlace;
    private int prevItem = 0;
    private EnumFacing enumFacing;

    public Scaffold() {
        super("Scaffold", Category.World);
    }

    public static double getYLevel() {
        if (!((Boolean)keepYValue.getValue()).booleanValue()) {
            return Scaffold.mc.thePlayer.posY - 1.0;
        }
        return !MoveUtil.isMoving() ? Scaffold.mc.thePlayer.posY - 1.0 : keepYCoord;
    }

    public static Vec3 getVec3(BlockPos pos, EnumFacing face) {
        double x2 = (double)pos.getX() + 0.5;
        double y2 = (double)pos.getY() + 0.5;
        double z = (double)pos.getZ() + 0.5;
        if (face == EnumFacing.UP || face == EnumFacing.DOWN) {
            x2 += MathUtil.getRandomInRange(0.3, -0.3);
            z += MathUtil.getRandomInRange(0.3, -0.3);
        } else {
            y2 += 0.08;
        }
        if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
            z += MathUtil.getRandomInRange(0.3, -0.3);
        }
        if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
            x2 += MathUtil.getRandomInRange(0.3, -0.3);
        }
        return new Vec3(x2, y2, z);
    }

    @Override
    public void onEnable() {
        this.idkTick = 5;
        if (Scaffold.mc.thePlayer == null) {
            return;
        }
        this.prevItem = Scaffold.mc.thePlayer.inventory.currentItem;
        Scaffold.mc.thePlayer.setSprinting((Boolean)this.sprintValue.getValue() != false || !this.canTellyPlace);
        Scaffold.mc.gameSettings.keyBindSprint.pressed = (Boolean)this.sprintValue.getValue() != false || !this.canTellyPlace;
        this.canTellyPlace = false;
        this.tip = false;
        this.data = null;
        this.slot = -1;
    }

    @Override
    public void onDisable() {
        if (Scaffold.mc.thePlayer == null) {
            return;
        }
        KeyBinding.setKeyBindState(Scaffold.mc.gameSettings.keyBindSneak.getKeyCode(), false);
        Scaffold.mc.thePlayer.inventory.currentItem = this.prevItem;
        Client.instance.slotSpoofManager.stopSpoofing();
    }

    @EventTarget
    public void onRender3D(EventRender3D event) {
        if (this.data == null) {
            return;
        }
        for (int i = 0; i < 2; ++i) {
            BlockPos blockPos = this.data;
            PlaceInfo placeInfo = PlaceInfo.get(blockPos);
            if (!BlockUtil.isValidBock(blockPos) || placeInfo == null || !((Boolean)this.esp.getValue()).booleanValue()) continue;
            RenderUtil.drawBlockBox(blockPos, new Color(255, 0, 0, 70), false);
            break;
        }
    }

    @EventTarget
    public void onUpdate(EventMotion event) {
        if (this.idkTick > 0) {
            --this.idkTick;
        }
        if (event.isPre() && ((Boolean)this.eagle.getValue()).booleanValue()) {
            if (Eagle.getBlockUnderPlayer(Scaffold.mc.thePlayer) instanceof BlockAir) {
                if (Scaffold.mc.thePlayer.onGround) {
                    KeyBinding.setKeyBindState(Scaffold.mc.gameSettings.keyBindSneak.getKeyCode(), true);
                }
            } else if (Scaffold.mc.thePlayer.onGround) {
                KeyBinding.setKeyBindState(Scaffold.mc.gameSettings.keyBindSneak.getKeyCode(), false);
            }
        }
    }

    @EventTarget
    public void onStrafe(EventStrafe event) {
        if ((this.upValue.getValue().booleanValue() || ((Boolean)keepYValue.getValue()).booleanValue()) && Scaffold.mc.thePlayer.onGround && MoveUtil.isMoving() && !Scaffold.mc.gameSettings.keyBindJump.isKeyDown()) {
            Scaffold.mc.thePlayer.jump();
        }
    }

    @EventTarget
    private void onTick(EventTick event) {
        if (Scaffold.mc.thePlayer == null) {
            return;
        }
        if (this.slot < 0) {
            return;
        }
        if (!((Boolean)this.telly.getValue()).booleanValue()) {
            this.canTellyPlace = true;
        }
    }

    @EventTarget
    private void onPlace(EventPlace event) {
        this.slot = this.getBlockSlot();
        if (this.slot < 0) {
            return;
        }
        if (!this.telly.getValue().booleanValue()) {
            Scaffold.mc.thePlayer.setSprinting((Boolean)this.sprintValue.getValue());
            Scaffold.mc.gameSettings.keyBindSprint.pressed = false;
        }
        event.setCancelled(true);
        if (Scaffold.mc.thePlayer == null) {
            return;
        }
        this.place();
        mc.sendClickBlockToController(Scaffold.mc.currentScreen == null && Scaffold.mc.gameSettings.keyBindAttack.isKeyDown() && Scaffold.mc.inGameHasFocus);
    }

    @EventTarget
    private void onUpdate(EventUpdate event) {
        if (((Boolean)this.telly.getValue()).booleanValue()) {
            if (Scaffold.mc.gameSettings.keyBindJump.pressed) {
                this.upValue.setValue(true);
                keepYValue.setValue(false);
            } else {
                this.upValue.setValue(false);
                keepYValue.setValue(true);
            }
        }
        if (Scaffold.mc.thePlayer.onGround) {
            keepYCoord = Math.floor(Scaffold.mc.thePlayer.posY - 1.0);
        }
        this.slot = this.getBlockSlot();
        if (this.slot < 0) {
            return;
        }
        Scaffold.mc.thePlayer.inventory.currentItem = this.slot;
        Client.instance.slotSpoofManager.startSpoofing(this.prevItem);
        this.findBlock();
        if (((Boolean)this.telly.getValue()).booleanValue()) {
            if (this.canTellyPlace && !Scaffold.mc.thePlayer.onGround && MoveUtil.isMoving()) {
                Scaffold.mc.thePlayer.setSprinting(false);
            }
            boolean bl = this.canTellyPlace = Scaffold.mc.thePlayer.offGroundTicks >= (((Boolean)this.upValue.getValue()).booleanValue() ? (Scaffold.mc.thePlayer.ticksExisted % 16 == 0 ? 2 : 1) : 3);
        }
        if (!this.canTellyPlace) {
            return;
        }
        if (this.data != null) {
            float yaw = RotationUtil.getRotationBlock(this.data)[0];
            float pitch = RotationUtil.getRotationBlock(this.data)[1];
            RotationComponent.setRotations(new Vector2f(yaw, pitch), 180.0f, true);
        }
    }

    @EventTarget
    private void onMotion(EventMotion event) {
        if (event.isPre()) {
            if (this.slot < 0) {
                return;
            }
            if (this.getBlockCount() <= 0) {
                int spoofSlot = this.getBestSpoofSlot();
                this.getBlock(spoofSlot);
            }
            if (this.slot < 0) {
                return;
            }
            Scaffold.mc.thePlayer.inventoryContainer.getSlot(this.slot + 36).getStack();
        }
    }

    private void place() {
        if (!this.canTellyPlace) {
            return;
        }
        this.slot = this.getBlockSlot();
        if (this.slot < 0) {
            return;
        }
        if (this.slot < 0) {
            return;
        }
        if (this.data != null) {
            EnumFacing enumFacing;
            EnumFacing enumFacing2 = enumFacing = keepYValue.getValue() ? this.enumFacing : this.getPlaceSide(this.data);
            if (enumFacing == null) {
                return;
            }
            if (Scaffold.mc.playerController.onPlayerRightClick(Scaffold.mc.thePlayer, Scaffold.mc.theWorld, Scaffold.mc.thePlayer.getCurrentEquippedItem(), this.data, enumFacing, Scaffold.getVec3(this.data, enumFacing))) {
                if (this.swing.getValue().booleanValue()) {
                    Scaffold.mc.thePlayer.swingItem();
                } else {
                    Scaffold.mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                }
            }
        }
    }

    private void findBlock() {
        if (MoveUtil.isMoving() && ((Boolean)keepYValue.getValue()).booleanValue()) {
            boolean shouldGoDown = false;
            BlockPos blockPosition = new BlockPos(Scaffold.mc.thePlayer.posX, Scaffold.getYLevel(), Scaffold.mc.thePlayer.posZ);
            if (BlockUtil.isValidBock(blockPosition) || this.search(blockPosition, !shouldGoDown)) {
                return;
            }
            for (int x2 = -1; x2 <= 1; ++x2) {
                for (int z = -1; z <= 1; ++z) {
                    if (!this.search(blockPosition.add(x2, 0, z), !shouldGoDown)) continue;
                    return;
                }
            }
        } else {
            this.data = this.getBlockPos();
        }
    }

    private double calcStepSize(double range) {
        double accuracy = 6.0;
        accuracy += accuracy % 2.0;
        return Math.max(range / accuracy, 0.01);
    }

    private boolean search(BlockPos blockPosition, boolean checks) {
        Vec3 eyesPos = new Vec3(Scaffold.mc.thePlayer.posX, Scaffold.mc.thePlayer.getEntityBoundingBox().minY + (double)Scaffold.mc.thePlayer.getEyeHeight(), Scaffold.mc.thePlayer.posZ);
        PlaceRotation placeRotation = null;
        double xzRV = 0.5;
        double yRV = 0.5;
        double xzSSV = this.calcStepSize(xzRV);
        double ySSV = this.calcStepSize(xzRV);
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbor = blockPosition.offset(side);
            if (!BlockUtil.isValidBock(neighbor)) continue;
            Vec3 dirVec = new Vec3(side.getDirectionVec());
            for (double xSearch = 0.5 - xzRV / 2.0; xSearch <= 0.5 + xzRV / 2.0; xSearch += xzSSV) {
                for (double ySearch = 0.5 - yRV / 2.0; ySearch <= 0.5 + yRV / 2.0; ySearch += ySSV) {
                    for (double zSearch = 0.5 - xzRV / 2.0; zSearch <= 0.5 + xzRV / 2.0; zSearch += xzSSV) {
                        Vec3 posVec = new Vec3(blockPosition).addVector(xSearch, ySearch, zSearch);
                        double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
                        Vec3 hitVec = posVec.add(new Vec3(dirVec.xCoord * 0.5, dirVec.yCoord * 0.5, dirVec.zCoord * 0.5));
                        if (checks && (eyesPos.squareDistanceTo(hitVec) > 18.0 || distanceSqPosVec > eyesPos.squareDistanceTo(posVec.add(dirVec)) || Scaffold.mc.theWorld.rayTraceBlocks(eyesPos, hitVec, false, true, false) != null)) continue;
                        double diffX = hitVec.xCoord - eyesPos.xCoord;
                        double diffY = hitVec.yCoord - eyesPos.yCoord;
                        double diffZ = hitVec.zCoord - eyesPos.zCoord;
                        double diffXZ = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
                        Rotation rotation = new Rotation(MathHelper.wrapAngleTo180_float((float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f), MathHelper.wrapAngleTo180_float((float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)))));
                        Vec3 rotationVector = new Vec3(RotationUtil.getVectorForRotation((Vector2f)rotation.toVec2f()).xCoord, RotationUtil.getVectorForRotation((Vector2f)rotation.toVec2f()).yCoord, RotationUtil.getVectorForRotation((Vector2f)rotation.toVec2f()).zCoord);
                        Vec3 vector = eyesPos.addVector(rotationVector.xCoord * 4.0, rotationVector.yCoord * 4.0, rotationVector.zCoord * 4.0);
                        MovingObjectPosition obj = Scaffold.mc.theWorld.rayTraceBlocks(eyesPos, vector, false, false, true);
                        if (obj.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || !obj.getBlockPos().equals(neighbor) || placeRotation != null && !(RotationComponent.getRotationDifference(rotation) < RotationComponent.getRotationDifference(placeRotation.getRotation()))) continue;
                        placeRotation = new PlaceRotation(new PlaceInfo(neighbor, side.getOpposite(), hitVec), rotation);
                    }
                }
            }
        }
        if (placeRotation == null) {
            return false;
        }
        this.data = placeRotation.getPlaceInfo().getBlockPos();
        this.enumFacing = placeRotation.getPlaceInfo().getEnumFacing();
        return true;
    }

    private EnumFacing getPlaceSide(BlockPos blockPos) {
        Vec3 vec3;
        BlockPos bp;
        ArrayList<Vec3> positions = new ArrayList<Vec3>();
        HashMap<Vec3, EnumFacing> hashMap = new HashMap<Vec3, EnumFacing>();
        BlockPos playerPos = new BlockPos(Scaffold.mc.thePlayer.posX, Scaffold.mc.thePlayer.posY, Scaffold.mc.thePlayer.posZ);
        if (BlockUtil.isAirBlock(blockPos.add(0, 1, 0)) && !blockPos.add(0, 1, 0).equals(playerPos) && !Scaffold.mc.thePlayer.onGround) {
            bp = blockPos.add(0, 1, 0);
            vec3 = this.getBestHitFeet(bp);
            positions.add(vec3);
            hashMap.put(vec3, EnumFacing.UP);
        }
        if (BlockUtil.isAirBlock(blockPos.add(1, 0, 0)) && !blockPos.add(1, 0, 0).equals(playerPos)) {
            bp = blockPos.add(1, 0, 0);
            vec3 = this.getBestHitFeet(bp);
            positions.add(vec3);
            hashMap.put(vec3, EnumFacing.EAST);
        }
        if (BlockUtil.isAirBlock(blockPos.add(-1, 0, 0)) && !blockPos.add(-1, 0, 0).equals(playerPos)) {
            bp = blockPos.add(-1, 0, 0);
            vec3 = this.getBestHitFeet(bp);
            positions.add(vec3);
            hashMap.put(vec3, EnumFacing.WEST);
        }
        if (BlockUtil.isAirBlock(blockPos.add(0, 0, 1)) && !blockPos.add(0, 0, 1).equals(playerPos)) {
            bp = blockPos.add(0, 0, 1);
            vec3 = this.getBestHitFeet(bp);
            positions.add(vec3);
            hashMap.put(vec3, EnumFacing.SOUTH);
        }
        if (BlockUtil.isAirBlock(blockPos.add(0, 0, -1)) && !blockPos.add(0, 0, -1).equals(playerPos)) {
            bp = blockPos.add(0, 0, -1);
            vec3 = this.getBestHitFeet(bp);
            positions.add(vec3);
            hashMap.put(vec3, EnumFacing.NORTH);
        }
        positions.sort(Comparator.comparingDouble(vec3x -> Scaffold.mc.thePlayer.getDistance(vec3x.xCoord, vec3x.yCoord, vec3x.zCoord)));
        if (!positions.isEmpty()) {
            vec3 = this.getBestHitFeet(this.data);
            if (Scaffold.mc.thePlayer.getDistance(vec3.xCoord, vec3.yCoord, vec3.zCoord) >= Scaffold.mc.thePlayer.getDistance(((Vec3)positions.get((int)0)).xCoord, ((Vec3)positions.get((int)0)).yCoord, ((Vec3)positions.get((int)0)).zCoord)) {
                return (EnumFacing)hashMap.get(positions.get(0));
            }
        }
        return null;
    }

    private Vec3 getBestHitFeet(BlockPos blockPos) {
        Block block = Scaffold.mc.theWorld.getBlockState(blockPos).getBlock();
        double ex = MathHelper.clamp_double(Scaffold.mc.thePlayer.posX, blockPos.getX(), (double)blockPos.getX() + block.getBlockBoundsMaxX());
        double ey = MathHelper.clamp_double((Boolean)keepYValue.getValue() != false ? Scaffold.getYLevel() : Scaffold.mc.thePlayer.posY, blockPos.getY(), (double)blockPos.getY() + block.getBlockBoundsMaxY());
        double ez = MathHelper.clamp_double(Scaffold.mc.thePlayer.posZ, blockPos.getZ(), (double)blockPos.getZ() + block.getBlockBoundsMaxZ());
        return new Vec3(ex, ey, ez);
    }

    private BlockPos getBlockPos() {
        BlockPos playerPos = new BlockPos(Scaffold.mc.thePlayer.posX, Scaffold.getYLevel(), Scaffold.mc.thePlayer.posZ);
        ArrayList<Vec3> positions = new ArrayList<Vec3>();
        HashMap<Vec3, BlockPos> hashMap = new HashMap<Vec3, BlockPos>();
        for (int x2 = playerPos.getX() - 5; x2 <= playerPos.getX() + 5; ++x2) {
            for (int y2 = playerPos.getY() - 1; y2 <= playerPos.getY(); ++y2) {
                for (int z = playerPos.getZ() - 5; z <= playerPos.getZ() + 5; ++z) {
                    if (!BlockUtil.isValidBock(new BlockPos(x2, y2, z))) continue;
                    BlockPos blockPos = new BlockPos(x2, y2, z);
                    Block block = Scaffold.mc.theWorld.getBlockState(blockPos).getBlock();
                    double ex = MathHelper.clamp_double(Scaffold.mc.thePlayer.posX, blockPos.getX(), (double)blockPos.getX() + block.getBlockBoundsMaxX());
                    double ey = MathHelper.clamp_double((Boolean)keepYValue.getValue() != false ? Scaffold.getYLevel() : Scaffold.mc.thePlayer.posY, blockPos.getY(), (double)blockPos.getY() + block.getBlockBoundsMaxY());
                    double ez = MathHelper.clamp_double(Scaffold.mc.thePlayer.posZ, blockPos.getZ(), (double)blockPos.getZ() + block.getBlockBoundsMaxZ());
                    Vec3 vec3 = new Vec3(ex, ey, ez);
                    positions.add(vec3);
                    hashMap.put(vec3, blockPos);
                }
            }
        }
        if (!positions.isEmpty()) {
            positions.sort(Comparator.comparingDouble(this::getBestBlock));
            return (BlockPos)hashMap.get(positions.get(0));
        }
        return null;
    }

    private double getBestBlock(Vec3 vec3) {
        return Scaffold.mc.thePlayer.getDistanceSq(vec3.xCoord, vec3.yCoord, vec3.zCoord);
    }

    public int getBlockSlot() {
        for (int i = 0; i < 9; ++i) {
            if (!Scaffold.mc.thePlayer.inventoryContainer.getSlot(i + 36).getHasStack() || !(Scaffold.mc.thePlayer.inventoryContainer.getSlot(i + 36).getStack().getItem() instanceof ItemBlock)) continue;
            return i;
        }
        return -1;
    }

    public int getBlockCount() {
        int n = 0;
        for (int i = 36; i < 45; ++i) {
            if (!Scaffold.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) continue;
            ItemStack stack = Scaffold.mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            Item item = stack.getItem();
            if (!(stack.getItem() instanceof ItemBlock) || !this.isValid(item)) continue;
            n += stack.stackSize;
        }
        return n;
    }

    public boolean isValid(Item item) {
        return item instanceof ItemBlock && !invalidBlocks.contains(((ItemBlock)item).getBlock());
    }

    private float getYaw() {
        if (Scaffold.mc.gameSettings.keyBindBack.isKeyDown()) {
            return Scaffold.mc.thePlayer.rotationYaw;
        }
        if (Scaffold.mc.gameSettings.keyBindLeft.isKeyDown()) {
            return Scaffold.mc.thePlayer.rotationYaw + 90.0f;
        }
        if (Scaffold.mc.gameSettings.keyBindRight.isKeyDown()) {
            return Scaffold.mc.thePlayer.rotationYaw - 90.0f;
        }
        return Scaffold.mc.thePlayer.rotationYaw - 180.0f;
    }

    private void getBlock(int switchSlot) {
        for (int i = 9; i < 45; ++i) {
            ItemBlock block;
            ItemStack is;
            if (!Scaffold.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() || Scaffold.mc.currentScreen != null && !(Scaffold.mc.currentScreen instanceof GuiInventory) || !((is = Scaffold.mc.thePlayer.inventoryContainer.getSlot(i).getStack()).getItem() instanceof ItemBlock) || !this.isValid(block = (ItemBlock)is.getItem())) continue;
            if (36 + switchSlot == i) break;
            InventoryUtil.swap(i, switchSlot);
            break;
        }
    }

    int getBestSpoofSlot() {
        int spoofSlot = 5;
        for (int i = 36; i < 45; ++i) {
            if (Scaffold.mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) continue;
            spoofSlot = i - 36;
            break;
        }
        return spoofSlot;
    }

    public int getSlot() {
        return this.slot;
    }
}

