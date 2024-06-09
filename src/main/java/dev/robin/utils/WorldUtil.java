package dev.robin.utils;

import dev.robin.utils.BlockInfo;
import dev.robin.utils.misc.MinecraftInstance;
import dev.robin.utils.player.MoveUtil;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class WorldUtil
implements MinecraftInstance {
    public static BlockInfo getBlockUnder(double y2, int maxRange) {
        return WorldUtil.getBlockInfo(WorldUtil.mc.thePlayer.posX, y2 - 1.0, WorldUtil.mc.thePlayer.posZ, maxRange);
    }

    public static BlockInfo getBlockInfo(final double x, final double y, final double z, final int maxRange) {
        final BlockPos pos = new BlockPos(x, y, z);
        final EnumFacing playerDirectionFacing = getHorizontalFacing(MoveUtil.getPlayerDirection()).getOpposite();
        final ArrayList<EnumFacing> facingValues = new ArrayList<EnumFacing>();
        facingValues.add(playerDirectionFacing);
        for (final EnumFacing facing : EnumFacing.values()) {
            if (facing != playerDirectionFacing && facing != EnumFacing.UP) {
                facingValues.add(facing);
            }
        }
        final CopyOnWriteArrayList<BlockPos> aaa = new CopyOnWriteArrayList<BlockPos>();
        aaa.add(pos);
        for (int i = 0; i < maxRange; ++i) {
            final ArrayList<BlockPos> ccc = new ArrayList<BlockPos>(aaa);
            if (!aaa.isEmpty()) {
                for (final BlockPos bbbb : aaa) {
                    for (final EnumFacing facing2 : facingValues) {
                        final BlockPos n = bbbb.offset(facing2);
                        if (!isAirOrLiquid(n)) {
                            return new BlockInfo(n, facing2.getOpposite());
                        }
                        aaa.add(n);
                    }
                }
            }
            for (final BlockPos dddd : ccc) {
                aaa.remove(dddd);
            }
            ccc.clear();
        }
        return null;
    }
    public static Vec3 getVec3(BlockPos pos, EnumFacing facing, boolean randomised) {
        Vec3 vec3 = new Vec3(pos);
        double amount1 = 0.5;
        double amount2 = 0.5;
        if (randomised) {
            amount1 = 0.45 + Math.random() * 0.1;
            amount2 = 0.45 + Math.random() * 0.1;
        }
        if (facing == EnumFacing.UP) {
            vec3 = vec3.addVector(amount1, 1.0, amount2);
        } else if (facing == EnumFacing.DOWN) {
            vec3 = vec3.addVector(amount1, 0.0, amount2);
        } else if (facing == EnumFacing.EAST) {
            vec3 = vec3.addVector(1.0, amount1, amount2);
        } else if (facing == EnumFacing.WEST) {
            vec3 = vec3.addVector(0.0, amount1, amount2);
        } else if (facing == EnumFacing.NORTH) {
            vec3 = vec3.addVector(amount1, amount2, 0.0);
        } else if (facing == EnumFacing.SOUTH) {
            vec3 = vec3.addVector(amount1, amount2, 1.0);
        }
        return vec3;
    }

    public static EnumFacing getHorizontalFacing(float yaw) {
        return EnumFacing.getHorizontal(MathHelper.floor_double((double)(yaw * 4.0f / 360.0f) + 0.5) & 3);
    }

    public static boolean isAir(BlockPos pos) {
        Block block = WorldUtil.mc.theWorld.getBlockState(pos).getBlock();
        return block instanceof BlockAir;
    }

    public static boolean isAirOrLiquid(BlockPos pos) {
        Block block = WorldUtil.mc.theWorld.getBlockState(pos).getBlock();
        return block instanceof BlockAir || block instanceof BlockLiquid;
    }

    public static MovingObjectPosition raytrace(float yaw, float pitch) {
        float partialTicks = WorldUtil.mc.timer.renderPartialTicks;
        float blockReachDistance = WorldUtil.mc.playerController.getBlockReachDistance();
        Vec3 vec3 = WorldUtil.mc.thePlayer.getPositionEyes(partialTicks);
        Vec3 vec31 = WorldUtil.mc.thePlayer.getVectorForRotation(pitch, yaw);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * (double)blockReachDistance, vec31.yCoord * (double)blockReachDistance, vec31.zCoord * (double)blockReachDistance);
        return WorldUtil.mc.theWorld.rayTraceBlocks(vec3, vec32, false, false, true);
    }

    public static MovingObjectPosition raytraceLegit(float yaw, float pitch, float lastYaw, float lastPitch) {
        float partialTicks = WorldUtil.mc.timer.renderPartialTicks;
        float blockReachDistance = WorldUtil.mc.playerController.getBlockReachDistance();
        Vec3 vec3 = WorldUtil.mc.thePlayer.getPositionEyes(partialTicks);
        float f = lastPitch + (pitch - lastPitch) * partialTicks;
        float f1 = lastYaw + (yaw - lastYaw) * partialTicks;
        Vec3 vec31 = WorldUtil.mc.thePlayer.getVectorForRotation(f, f1);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * (double)blockReachDistance, vec31.yCoord * (double)blockReachDistance, vec31.zCoord * (double)blockReachDistance);
        return WorldUtil.mc.theWorld.rayTraceBlocks(vec3, vec32, false, false, true);
    }

    public static boolean isBlockUnder() {
        for (int y2 = (int)WorldUtil.mc.thePlayer.posY; y2 >= 0; --y2) {
            if (WorldUtil.mc.theWorld.getBlockState(new BlockPos(WorldUtil.mc.thePlayer.posX, (double)y2, WorldUtil.mc.thePlayer.posZ)).getBlock() instanceof BlockAir) continue;
            return true;
        }
        return false;
    }

    public static boolean isBlockUnder(int distance) {
        for (int y2 = (int)WorldUtil.mc.thePlayer.posY; y2 >= (int)WorldUtil.mc.thePlayer.posY - distance; --y2) {
            if (WorldUtil.mc.theWorld.getBlockState(new BlockPos(WorldUtil.mc.thePlayer.posX, (double)y2, WorldUtil.mc.thePlayer.posZ)).getBlock() instanceof BlockAir) continue;
            return true;
        }
        return false;
    }

    public static boolean negativeExpand(double negativeExpandValue) {
        return WorldUtil.mc.theWorld.getBlockState(new BlockPos(WorldUtil.mc.thePlayer.posX + negativeExpandValue, WorldUtil.mc.thePlayer.posY - 1.0, WorldUtil.mc.thePlayer.posZ + negativeExpandValue)).getBlock() instanceof BlockAir && WorldUtil.mc.theWorld.getBlockState(new BlockPos(WorldUtil.mc.thePlayer.posX - negativeExpandValue, WorldUtil.mc.thePlayer.posY - 1.0, WorldUtil.mc.thePlayer.posZ - negativeExpandValue)).getBlock() instanceof BlockAir && WorldUtil.mc.theWorld.getBlockState(new BlockPos(WorldUtil.mc.thePlayer.posX - negativeExpandValue, WorldUtil.mc.thePlayer.posY - 1.0, WorldUtil.mc.thePlayer.posZ)).getBlock() instanceof BlockAir && WorldUtil.mc.theWorld.getBlockState(new BlockPos(WorldUtil.mc.thePlayer.posX + negativeExpandValue, WorldUtil.mc.thePlayer.posY - 1.0, WorldUtil.mc.thePlayer.posZ)).getBlock() instanceof BlockAir && WorldUtil.mc.theWorld.getBlockState(new BlockPos(WorldUtil.mc.thePlayer.posX, WorldUtil.mc.thePlayer.posY - 1.0, WorldUtil.mc.thePlayer.posZ + negativeExpandValue)).getBlock() instanceof BlockAir && WorldUtil.mc.theWorld.getBlockState(new BlockPos(WorldUtil.mc.thePlayer.posX, WorldUtil.mc.thePlayer.posY - 1.0, WorldUtil.mc.thePlayer.posZ - negativeExpandValue)).getBlock() instanceof BlockAir;
    }
}

