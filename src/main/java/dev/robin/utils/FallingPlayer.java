package dev.robin.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import javax.annotation.Nullable;

public class FallingPlayer {
    private final Minecraft mc = Minecraft.getMinecraft();
    private double x;
    private double y;
    private double z;
    private double motionX;
    private double motionY;
    private double motionZ;
    private final float yaw;
    private final float strafe;
    private final float forward;

    public FallingPlayer(double x2, double y2, double z, double motionX, double motionY, double motionZ, float yaw, float strafe, float forward) {
        this.x = x2;
        this.y = y2;
        this.z = z;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        this.yaw = yaw;
        this.strafe = strafe;
        this.forward = forward;
    }

    public FallingPlayer(EntityPlayer player) {
        this.x = player.posX;
        this.y = player.posY;
        this.z = player.posZ;
        this.motionX = player.motionX;
        this.motionY = player.motionY;
        this.motionZ = player.motionZ;
        this.yaw = player.rotationYaw;
        this.strafe = player.moveStrafing;
        this.forward = player.moveForward;
    }

    private void calculateForTick(float strafe, float forward) {
        float v = strafe * strafe + forward * forward;
        if (v >= 1.0E-4f) {
            if ((v = (float)Math.sqrt(v)) < 1.0f) {
                v = 1.0f;
            }
            v = this.mc.thePlayer.jumpMovementFactor / v;
            float f1 = (float)Math.sin(this.yaw * (float)Math.PI / 180.0f);
            float f2 = (float)Math.cos(this.yaw * (float)Math.PI / 180.0f);
            this.motionX += (double)((strafe *= v) * f2 - (forward *= v) * f1);
            this.motionZ += (double)(forward * f2 + strafe * f1);
        }
        this.motionY -= 0.08;
        this.motionX *= (double)0.91f;
        this.motionY *= (double)0.98f;
        this.motionZ *= (double)0.91f;
        this.x += this.motionX;
        this.y += this.motionY;
        this.z += this.motionZ;
    }

    public CollisionResult findCollision(int ticks) {
        for (int i = 0; i < ticks; ++i) {
            Vec3 start = new Vec3(this.x, this.y, this.z);
            this.calculateForTick(this.strafe * 0.98f, this.forward * 0.98f);
            Vec3 end = new Vec3(this.x, this.y, this.z);
            float w2 = this.mc.thePlayer.width / 2.0f;
            BlockPos raytracedBlock = this.rayTrace(start, end);
            if (raytracedBlock != null) {
                return new CollisionResult(raytracedBlock, i);
            }
            raytracedBlock = this.rayTrace(start.addVector(w2, 0.0, w2), end);
            if (raytracedBlock != null) {
                return new CollisionResult(raytracedBlock, i);
            }
            raytracedBlock = this.rayTrace(start.addVector(-w2, 0.0, w2), end);
            if (raytracedBlock != null) {
                return new CollisionResult(raytracedBlock, i);
            }
            raytracedBlock = this.rayTrace(start.addVector(w2, 0.0, -w2), end);
            if (raytracedBlock != null) {
                return new CollisionResult(raytracedBlock, i);
            }
            raytracedBlock = this.rayTrace(start.addVector(-w2, 0.0, -w2), end);
            if (raytracedBlock != null) {
                return new CollisionResult(raytracedBlock, i);
            }
            raytracedBlock = this.rayTrace(start.addVector(w2, 0.0, w2 / 2.0f), end);
            if (raytracedBlock != null) {
                return new CollisionResult(raytracedBlock, i);
            }
            raytracedBlock = this.rayTrace(start.addVector(-w2, 0.0, w2 / 2.0f), end);
            if (raytracedBlock != null) {
                return new CollisionResult(raytracedBlock, i);
            }
            raytracedBlock = this.rayTrace(start.addVector(w2 / 2.0f, 0.0, w2), end);
            if (raytracedBlock != null) {
                return new CollisionResult(raytracedBlock, i);
            }
            raytracedBlock = this.rayTrace(start.addVector(w2 / 2.0f, 0.0, -w2), end);
            if (raytracedBlock == null) continue;
            return new CollisionResult(raytracedBlock, i);
        }
        return null;
    }

    @Nullable
    private BlockPos rayTrace(Vec3 start, Vec3 end) {
        MovingObjectPosition result = this.mc.theWorld.rayTraceBlocks(start, end, true);
        if (result != null && result.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && result.sideHit == EnumFacing.UP) {
            return result.getBlockPos();
        }
        return null;
    }

    public static class CollisionResult {
        private final BlockPos pos;
        private final int tick;

        public CollisionResult(BlockPos pos, int tick) {
            this.pos = pos;
            this.tick = tick;
        }

        public BlockPos getPos() {
            return this.pos;
        }

        public int getTick() {
            return this.tick;
        }
    }
}

