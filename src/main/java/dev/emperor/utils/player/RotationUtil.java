package dev.emperor.utils.player;

import dev.emperor.event.world.EventMotion;
import dev.emperor.utils.Location;
import dev.emperor.utils.RayCastUtil;
import dev.emperor.utils.RotationComponent;
import dev.emperor.utils.vec.Vector3d;
import java.util.concurrent.ThreadLocalRandom;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.compatibility.util.vector.Vector2f;

@Setter
@Getter
public class RotationUtil {
    public static Minecraft mc = Minecraft.getMinecraft();
    private float yaw;
    private float pitch;

    public RotationUtil(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public static float[] getBlockRotations(double x2, double y2, double z) {
        double var4 = x2 - RotationUtil.mc.thePlayer.posX + 0.5;
        double var6 = z - RotationUtil.mc.thePlayer.posZ + 0.5;
        double var8 = y2 - (RotationUtil.mc.thePlayer.posY + (double)RotationUtil.mc.thePlayer.getEyeHeight() - 1.0);
        double var14 = MathHelper.sqrt_double(var4 * var4 + var6 * var6);
        float var12 = (float)(Math.atan2(var6, var4) * 180.0 / Math.PI) - 90.0f;
        return new float[]{var12, (float)(-Math.atan2(var8, var14) * 180.0 / Math.PI)};
    }

    public static float[] positionRotation(double posX, double posY, double posZ, float[] lastRots, float yawSpeed, float pitchSpeed, boolean random) {
        double x2 = posX - RotationUtil.mc.thePlayer.posX;
        double y2 = posY - (RotationUtil.mc.thePlayer.posY + (double)RotationUtil.mc.thePlayer.getEyeHeight());
        double z = posZ - RotationUtil.mc.thePlayer.posZ;
        float calcYaw = (float)(MathHelper.atan2(z, x2) * 180.0 / Math.PI - 90.0);
        float calcPitch = (float)(-(MathHelper.atan2(y2, MathHelper.sqrt_double(x2 * x2 + z * z)) * 180.0 / Math.PI));
        float yaw = RotationUtil.updateRotation(lastRots[0], calcYaw, yawSpeed);
        float pitch = RotationUtil.updateRotation(lastRots[1], calcPitch, pitchSpeed);
        if (random) {
            yaw += (float)ThreadLocalRandom.current().nextGaussian();
            pitch += (float)ThreadLocalRandom.current().nextGaussian();
        }
        return new float[]{yaw, pitch};
    }

    public static int wrapAngleToDirection(float yaw, int zones) {
        int angle = (int)((double)(yaw + (float)(360 / (2 * zones))) + 0.5) % 360;
        if (angle < 0) {
            angle += 360;
        }
        return angle / (360 / zones);
    }

    public static float getGCD() {
        return (float)(Math.pow((double)RotationUtil.mc.gameSettings.mouseSensitivity * 0.6 + 0.2, 3.0) * 1.2);
    }

    public static Vector2f getRotationFromEyeToPointOffset(Vec3 position, EnumFacing enumFacing) {
        double x2 = position.xCoord + 0.5;
        double y2 = position.yCoord + 0.5;
        double z = position.zCoord + 0.5;
        return RotationUtil.getRot(new Vec3(x2 + (double) enumFacing.getDirectionVec().getX() * 0.5, y2 + (double) enumFacing.getDirectionVec().getY() * 0.5, z + (double) enumFacing.getDirectionVec().getZ() * 0.5));
    }

    public static Vector2f getRot(Vec3 pos) {
        Vec3 vec = new Vec3(RotationUtil.mc.thePlayer.posX, RotationUtil.mc.thePlayer.getEntityBoundingBox().minY + (double)RotationUtil.mc.thePlayer.getEyeHeight(), RotationUtil.mc.thePlayer.posZ);
        double x2 = pos.xCoord - vec.xCoord;
        double y2 = pos.yCoord - vec.yCoord;
        double z = pos.zCoord - vec.zCoord;
        double sqrt = Math.sqrt(x2 * x2 + z * z);
        float yaw = (float)Math.toDegrees(Math.atan2(z, x2)) - 90.0f;
        float pitch = (float)(-Math.toDegrees(Math.atan2(y2, sqrt)));
        return new Vector2f(yaw, Math.min(Math.max(pitch, -90.0f), 90.0f));
    }

    public static float[] getRotationsToPosition(double x2, double y2, double z) {
        double deltaX = x2 - RotationUtil.mc.thePlayer.posX;
        double deltaY = y2 - RotationUtil.mc.thePlayer.posY - (double)RotationUtil.mc.thePlayer.getEyeHeight();
        double deltaZ = z - RotationUtil.mc.thePlayer.posZ;
        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        float yaw = (float)Math.toDegrees(-Math.atan2(deltaX, deltaZ));
        float pitch = (float)Math.toDegrees(-Math.atan2(deltaY, horizontalDistance));
        return new float[]{yaw, pitch};
    }

    public static float[] getRotationsToPosition(double x2, double y2, double z, double targetX, double targetY, double targetZ) {
        double dx = targetX - x2;
        double dy = targetY - y2;
        double dz = targetZ - z;
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float)Math.toDegrees(-Math.atan2(dx, dz));
        float pitch = (float)Math.toDegrees(-Math.atan2(dy, horizontalDistance));
        return new float[]{yaw, pitch};
    }

    public static float[] scaffoldRots(double bx, double by, double bz, float lastYaw, float lastPitch, float yawSpeed, float pitchSpeed, boolean random) {
        double x2 = bx - RotationUtil.mc.thePlayer.posX;
        double y2 = by - (RotationUtil.mc.thePlayer.posY + (double)RotationUtil.mc.thePlayer.getEyeHeight());
        double z = bz - RotationUtil.mc.thePlayer.posZ;
        float calcYaw = (float)(Math.toDegrees(MathHelper.atan2(z, x2)) - 90.0);
        float calcPitch = (float)(-(MathHelper.atan2(y2, MathHelper.sqrt_double(x2 * x2 + z * z)) * 180.0 / Math.PI));
        float pitch = RotationUtil.updateRotation(lastPitch, calcPitch, pitchSpeed + RandomUtils.nextFloat(0.0f, 15.0f));
        float yaw = RotationUtil.updateRotation(lastYaw, calcYaw, yawSpeed + RandomUtils.nextFloat(0.0f, 15.0f));
        if (random) {
            yaw += (float)ThreadLocalRandom.current().nextDouble(-2.0, 2.0);
            pitch += (float)ThreadLocalRandom.current().nextDouble(-0.2, 0.2);
        }
        return new float[]{yaw, pitch};
    }

    public static float[] mouseSens(float yaw, float pitch, float lastYaw, float lastPitch) {
        if ((double)RotationUtil.mc.gameSettings.mouseSensitivity == 0.5) {
            RotationUtil.mc.gameSettings.mouseSensitivity = 0.47887325f;
        }
        if (yaw == lastYaw && pitch == lastPitch) {
            return new float[]{yaw, pitch};
        }
        float f1 = RotationUtil.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        float f2 = f1 * f1 * f1 * 8.0f;
        int deltaX = (int)((6.667 * (double)yaw - 6.667 * (double)lastYaw) / (double)f2);
        int deltaY = (int)((6.667 * (double)pitch - 6.667 * (double)lastPitch) / (double)f2) * -1;
        float f3 = (float)deltaX * f2;
        float f4 = (float)deltaY * f2;
        yaw = (float)((double)lastYaw + (double)f3 * 0.15);
        float f5 = (float)((double)lastPitch - (double)f4 * 0.15);
        pitch = MathHelper.clamp_float(f5, -90.0f, 90.0f);
        return new float[]{yaw, pitch};
    }

    public static float rotateToYaw(float yawSpeed, float currentYaw, float calcYaw) {
        float yaw = RotationUtil.updateRotation(currentYaw, calcYaw, yawSpeed + RandomUtils.nextFloat(0.0f, 15.0f));
        double diffYaw = MathHelper.wrapAngleTo180_float(calcYaw - currentYaw);
        if ((double)(-yawSpeed) > diffYaw || diffYaw > (double)yawSpeed) {
            yaw += (float)((double)RandomUtils.nextFloat(1.0f, 2.0f) * Math.sin((double)RotationUtil.mc.thePlayer.rotationPitch * Math.PI));
        }
        if (yaw == currentYaw) {
            return currentYaw;
        }
        if ((double)RotationUtil.mc.gameSettings.mouseSensitivity == 0.5) {
            RotationUtil.mc.gameSettings.mouseSensitivity = 0.47887325f;
        }
        float f1 = RotationUtil.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        float f2 = f1 * f1 * f1 * 8.0f;
        int deltaX = (int)((6.667 * (double)yaw - 6.666666666666667 * (double)currentYaw) / (double)f2);
        float f3 = (float)deltaX * f2;
        yaw = (float)((double)currentYaw + (double)f3 * 0.15);
        return yaw;
    }

    public static float updateRotation(float current, float calc, float maxDelta) {
        float f = MathHelper.wrapAngleTo180_float(calc - current);
        if (f > maxDelta) {
            f = maxDelta;
        }
        if (f < -maxDelta) {
            f = -maxDelta;
        }
        return current + f;
    }

    public static float rotateToPitch(float pitchSpeed, float currentPitch, float calcPitch) {
        float pitch = RotationUtil.updateRotation(currentPitch, calcPitch, pitchSpeed + RandomUtils.nextFloat(0.0f, 15.0f));
        if (pitch != calcPitch) {
            pitch += (float)((double)RandomUtils.nextFloat(1.0f, 2.0f) * Math.sin((double)RotationUtil.mc.thePlayer.rotationYaw * Math.PI));
        }
        if ((double)RotationUtil.mc.gameSettings.mouseSensitivity == 0.5) {
            RotationUtil.mc.gameSettings.mouseSensitivity = 0.47887325f;
        }
        float f1 = RotationUtil.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        float f2 = f1 * f1 * f1 * 8.0f;
        int deltaY = (int)((6.667 * (double)pitch - 6.666667 * (double)currentPitch) / (double)f2) * -1;
        float f3 = (float)deltaY * f2;
        float f4 = (float)((double)currentPitch - (double)f3 * 0.15);
        pitch = MathHelper.clamp_float(f4, -90.0f, 90.0f);
        return pitch;
    }

    public static double getRotationDifference(Entity entity) {
        Vector2f rotation = RotationUtil.toRotation(RotationUtil.getCenter(entity.getEntityBoundingBox()), true);
        return RotationUtil.getRotationDifference(rotation, new Vector2f(RotationUtil.mc.thePlayer.rotationYaw, RotationUtil.mc.thePlayer.rotationPitch));
    }

    public static double getRotationDifference(Vector2f a, Vector2f b2) {
        return Math.hypot(RotationComponent.getAngleDifference(a.getX(), b2.getX()), a.getY() - b2.getY());
    }

    public static Vec3 getCenter(AxisAlignedBB bb) {
        return new Vec3(bb.minX + (bb.maxX - bb.minX) * 0.5, bb.minY + (bb.maxY - bb.minY) * 0.5, bb.minZ + (bb.maxZ - bb.minZ) * 0.5);
    }

    public static Vector2f toRotation(Vec3 vec, boolean predict) {
        Vec3 eyesPos = new Vec3(RotationUtil.mc.thePlayer.posX, RotationUtil.mc.thePlayer.getEntityBoundingBox().minY + (double)RotationUtil.mc.thePlayer.getEyeHeight(), RotationUtil.mc.thePlayer.posZ);
        if (predict) {
            eyesPos.addVector(RotationUtil.mc.thePlayer.motionX, RotationUtil.mc.thePlayer.motionY, RotationUtil.mc.thePlayer.motionZ);
        }
        double diffX = vec.xCoord - eyesPos.xCoord;
        double diffY = vec.yCoord - eyesPos.yCoord;
        double diffZ = vec.zCoord - eyesPos.zCoord;
        return new Vector2f(MathHelper.wrapAngleTo180_float((float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f), MathHelper.wrapAngleTo180_float((float)(-Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ))))));
    }

    public static float[] getRotationsNeeded(Entity target) {
        double yDist = target.posY - RotationUtil.mc.thePlayer.posY;
        Vec3 pos = yDist >= 1.7 ? new Vec3(target.posX, target.posY, target.posZ) : (yDist <= -1.7 ? new Vec3(target.posX, target.posY + (double)target.getEyeHeight(), target.posZ) : new Vec3(target.posX, target.posY + (double)(target.getEyeHeight() / 2.0f), target.posZ));
        Vec3 vec = new Vec3(RotationUtil.mc.thePlayer.posX, RotationUtil.mc.thePlayer.getEntityBoundingBox().minY + (double)RotationUtil.mc.thePlayer.getEyeHeight(), RotationUtil.mc.thePlayer.posZ);
        double x2 = pos.xCoord - vec.xCoord;
        double y2 = pos.yCoord - vec.yCoord;
        double z = pos.zCoord - vec.zCoord;
        double sqrt = Math.sqrt(x2 * x2 + z * z);
        float yaw = (float)Math.toDegrees(Math.atan2(z, x2)) - 90.0f;
        float pitch = (float)(-Math.toDegrees(Math.atan2(y2, sqrt)));
        return new float[]{yaw, Math.min(Math.max(pitch, -90.0f), 90.0f)};
    }

    public static float[] getHVHRotation(Entity entity, double maxRange) {
        if (entity == null) {
            return null;
        }
        double diffX = entity.posX - RotationUtil.mc.thePlayer.posX;
        double diffZ = entity.posZ - RotationUtil.mc.thePlayer.posZ;
        Vec3 BestPos = RotationUtil.getNearestPointBB(RotationUtil.mc.thePlayer.getPositionEyes(1.0f), entity.getEntityBoundingBox());
        Location myEyePos = new Location(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY + (double)RotationUtil.mc.thePlayer.getEyeHeight(), Minecraft.getMinecraft().thePlayer.posZ);
        double diffY = BestPos.yCoord - myEyePos.getY();
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-(Math.atan2(diffY, dist) * 180.0 / Math.PI));
        return new float[]{yaw, pitch};
    }

    public static float[] getRotationsNeededBlock(double x2, double y2, double z) {
        double diffX = x2 + 0.5 - Minecraft.getMinecraft().thePlayer.posX;
        double diffZ = z + 0.5 - Minecraft.getMinecraft().thePlayer.posZ;
        double diffY = y2 + 0.5 - (Minecraft.getMinecraft().thePlayer.posY + (double)Minecraft.getMinecraft().thePlayer.getEyeHeight());
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-Math.atan2(diffY, dist) * 180.0 / Math.PI);
        return new float[]{Minecraft.getMinecraft().thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - Minecraft.getMinecraft().thePlayer.rotationYaw), Minecraft.getMinecraft().thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - Minecraft.getMinecraft().thePlayer.rotationPitch)};
    }

    public static Vector2f getRotations(double posX, double posY, double posZ) {
        EntityPlayerSP player = RotationUtil.mc.thePlayer;
        double x2 = posX - player.posX;
        double y2 = posY - (player.posY + (double)player.getEyeHeight());
        double z = posZ - player.posZ;
        double dist = MathHelper.sqrt_double(x2 * x2 + z * z);
        float yaw = (float)(Math.atan2(z, x2) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-(Math.atan2(y2, dist) * 180.0 / Math.PI));
        return new Vector2f(yaw, pitch);
    }

    public static Vector2f getRotations(BlockPos block, EnumFacing face) {
        double x2 = (double)block.getX() + 0.5 - RotationUtil.mc.thePlayer.posX + (double)face.getFrontOffsetX() / 2.0;
        double z = (double)block.getZ() + 0.5 - RotationUtil.mc.thePlayer.posZ + (double)face.getFrontOffsetZ() / 2.0;
        double y2 = (double)block.getY() + 0.5;
        double d1 = RotationUtil.mc.thePlayer.posY + (double)RotationUtil.mc.thePlayer.getEyeHeight() - y2;
        double d3 = MathHelper.sqrt_double(x2 * x2 + z * z);
        float yaw = (float)(Math.atan2(z, x2) * 180.0 / Math.PI) - 82.0f;
        float pitch = (float)(Math.atan2(d1, d3) * 180.0 / Math.PI);
        if (yaw < 0.0f) {
            yaw += 360.0f;
        }
        return new Vector2f(yaw, pitch);
    }

    public static Vector2f getRotationsNonLivingEntity(Entity entity) {
        return RotationUtil.getRotations(entity.posX, entity.posY + (entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) * 0.5, entity.posZ);
    }

    public static void setVisualRotations(float yaw, float pitch) {
        RotationUtil.mc.thePlayer.rotationYawHead = RotationUtil.mc.thePlayer.renderYawOffset = yaw;
        RotationUtil.mc.thePlayer.rotationPitchHead = pitch;
    }

    public static Vec3 getVectorForRotation(Vector2f rotation) {
        float yawCos = MathHelper.cos(-rotation.getX() * ((float)Math.PI / 180) - (float)Math.PI);
        float yawSin = MathHelper.sin(-rotation.getX() * ((float)Math.PI / 180) - (float)Math.PI);
        float pitchCos = -MathHelper.cos(-rotation.getY() * ((float)Math.PI / 180));
        float pitchSin = MathHelper.sin(-rotation.getY() * ((float)Math.PI / 180));
        return new Vec3(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
    }

    public static float[] getRotations(Entity entity) {
        double pX = Minecraft.getMinecraft().thePlayer.posX;
        double pY = Minecraft.getMinecraft().thePlayer.posY + (double)Minecraft.getMinecraft().thePlayer.getEyeHeight();
        double pZ = Minecraft.getMinecraft().thePlayer.posZ;
        double eX = entity.posX;
        double eY = entity.posY + (double)(entity.height / 2.0f);
        double eZ = entity.posZ;
        double dX = pX - eX;
        double dY = pY - eY;
        double dZ = pZ - eZ;
        double dH = Math.sqrt(Math.pow(dX, 2.0) + Math.pow(dZ, 2.0));
        double yaw = Math.toDegrees(Math.atan2(dZ, dX)) + 90.0;
        double pitch = Math.toDegrees(Math.atan2(dH, dY));
        return new float[]{(float)yaw, (float)(90.0 - pitch)};
    }

    public static Vec3 getNearestPointBB(Vec3 eye, AxisAlignedBB box) {
        double[] origin = new double[]{eye.xCoord, eye.yCoord, eye.zCoord};
        double[] destMins = new double[]{box.minX, box.minY, box.minZ};
        double[] destMaxs = new double[]{box.maxX, box.maxY, box.maxZ};
        for (int i = 0; i < 3; ++i) {
            if (origin[i] > destMaxs[i]) {
                origin[i] = destMaxs[i];
                continue;
            }
            if (!(origin[i] < destMins[i])) continue;
            origin[i] = destMins[i];
        }
        return new Vec3(origin[0], origin[1], origin[2]);
    }

    public static Vector2f toRotationMisc(Vec3 vec, boolean predict) {
        Vec3 eyesPos = new Vec3(RotationUtil.mc.thePlayer.posX, RotationUtil.mc.thePlayer.getEntityBoundingBox().minY + (double)RotationUtil.mc.thePlayer.getEyeHeight(), RotationUtil.mc.thePlayer.posZ);
        if (predict) {
            eyesPos.addVector(RotationUtil.mc.thePlayer.motionX, RotationUtil.mc.thePlayer.motionY, RotationUtil.mc.thePlayer.motionZ);
        }
        double diffX = vec.xCoord - eyesPos.xCoord;
        double diffY = vec.yCoord - eyesPos.yCoord;
        double diffZ = vec.zCoord - eyesPos.zCoord;
        return new Vector2f(MathHelper.wrapAngleTo180_float((float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f), MathHelper.wrapAngleTo180_float((float)(-Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ))))));
    }

    public static float getTrajAngleSolutionLow(float d3, float d1, float velocity) {
        float g2 = 0.006f;
        float sqrt = velocity * velocity * velocity * velocity - g2 * (g2 * (d3 * d3) + 2.0f * d1 * (velocity * velocity));
        return (float)Math.toDegrees(Math.atan(((double)(velocity * velocity) - Math.sqrt(sqrt)) / (double)(g2 * d3)));
    }

    public static float getBowRot(Entity entity) {
        double diffY;
        double diffX = entity.posX - RotationUtil.mc.thePlayer.posX;
        double diffZ = entity.posZ - RotationUtil.mc.thePlayer.posZ;
        Location BestPos = new Location(entity.posX, entity.posY, entity.posZ);
        Location myEyePos = new Location(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY + (double)RotationUtil.mc.thePlayer.getEyeHeight(), Minecraft.getMinecraft().thePlayer.posZ);
        for (diffY = entity.boundingBox.minY + 0.7; diffY < entity.boundingBox.maxY - 0.1; diffY += 0.1) {
            Location location = new Location(entity.posX, diffY, entity.posZ);
            if (!(myEyePos.distanceTo(location) < myEyePos.distanceTo(BestPos))) continue;
            BestPos = new Location(entity.posX, diffY, entity.posZ);
        }
        diffY = BestPos.getY() - (Minecraft.getMinecraft().thePlayer.posY + (double)Minecraft.getMinecraft().thePlayer.getEyeHeight());
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-(Math.atan2(diffY, dist) * 180.0 / Math.PI));
        return yaw;
    }

    public static Vector2f calculate(Vector3d from, Vector3d to) {
        Vector3d diff = to.subtract(from);
        double distance = Math.hypot(diff.getX(), diff.getZ());
        float yaw = (float)(MathHelper.atan2(diff.getZ(), diff.getX()) * (double)MathHelper.TO_DEGREES) - 90.0f;
        float pitch = (float)(-(MathHelper.atan2(diff.getY(), distance) * (double)MathHelper.TO_DEGREES));
        return new Vector2f(yaw, pitch);
    }

    public static Vector2f calculate(Vec3 to) {
        return RotationUtil.calculate(RotationUtil.mc.thePlayer.getCustomPositionVector().add(0.0, RotationUtil.mc.thePlayer.getEyeHeight(), 0.0), new Vector3d(to.xCoord, to.yCoord, to.zCoord));
    }

    public static Vector2f calculate(Vector3d to) {
        return RotationUtil.calculate(RotationUtil.mc.thePlayer.getCustomPositionVector().add(0.0, RotationUtil.mc.thePlayer.getEyeHeight(), 0.0), to);
    }

    public static Vector2f calculate(Vector3d position, EnumFacing enumFacing) {
        double x2 = position.getX() + 0.5;
        double y2 = position.getY() + 0.5;
        double z = position.getZ() + 0.5;
        return RotationUtil.calculate(new Vector3d(x2 + (double) enumFacing.getDirectionVec().getX() * 0.5, y2 + (double) enumFacing.getDirectionVec().getY() * 0.5, z + (double) enumFacing.getDirectionVec().getZ() * 0.5));
    }

    public static Vector2f calculate(Entity entity) {
        return RotationUtil.calculate(entity.getCustomPositionVector().add(0.0, Math.max(0.0, Math.min(RotationUtil.mc.thePlayer.posY - entity.posY + (double)RotationUtil.mc.thePlayer.getEyeHeight(), (entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) * 0.9)), 0.0));
    }

    public static Vector2f calculate(Entity entity, boolean adaptive, double range) {
        Vector2f normalRotations = RotationUtil.calculate(entity);
        if (!adaptive || RayCastUtil.rayCast(normalRotations, range).typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
            return normalRotations;
        }
        for (double yPercent = 1.0; yPercent >= 0.0; yPercent -= 0.25) {
            for (double xPercent = 1.0; xPercent >= -0.5; xPercent -= 0.5) {
                for (double zPercent = 1.0; zPercent >= -0.5; zPercent -= 0.5) {
                    Vector2f adaptiveRotations = RotationUtil.calculate(entity.getCustomPositionVector().add((entity.getEntityBoundingBox().maxX - entity.getEntityBoundingBox().minX) * xPercent, (entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) * yPercent, (entity.getEntityBoundingBox().maxZ - entity.getEntityBoundingBox().minZ) * zPercent));
                    if (RayCastUtil.rayCast(adaptiveRotations, range).typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) continue;
                    return adaptiveRotations;
                }
            }
        }
        return normalRotations;
    }

    public static void setVisualRotations(float[] rotations) {
        RotationUtil.setVisualRotations(rotations[0], rotations[1]);
    }

    public static void setVisualRotations(EventMotion e) {
        RotationUtil.setVisualRotations(e.getYaw(), e.getPitch());
    }

    public static float getRotation(float currentRotation, float targetRotation, float maxIncrement) {
        float deltaAngle = MathHelper.wrapAngleTo180_float(targetRotation - currentRotation);
        if (deltaAngle > maxIncrement) {
            deltaAngle = maxIncrement;
        }
        if (deltaAngle < -maxIncrement) {
            deltaAngle = -maxIncrement;
        }
        return currentRotation + deltaAngle / 2.0f;
    }

    public static Vector2f resetRotation(Vector2f rotation) {
        if (rotation == null) {
            return null;
        }
        float yaw = RotationUtil.mc.thePlayer.rotationYaw;
        float pitch = RotationUtil.mc.thePlayer.rotationPitch;
        return new Vector2f(yaw, pitch);
    }

    public static Vector2f applySensitivityPatch(Vector2f rotation, Vector2f previousRotation) {
        float mouseSensitivity = (float)((double)RotationUtil.mc.gameSettings.mouseSensitivity * (1.0 + Math.random() / 1.0E7) * (double)0.6f + (double)0.2f);
        double multiplier = (double)(mouseSensitivity * mouseSensitivity * mouseSensitivity * 8.0f) * 0.15;
        float yaw = previousRotation.x + (float)((double)Math.round((double)(rotation.x - previousRotation.x) / multiplier) * multiplier);
        float pitch = previousRotation.y + (float)((double)Math.round((double)(rotation.y - previousRotation.y) / multiplier) * multiplier);
        return new Vector2f(yaw, MathHelper.clamp_float(pitch, -90.0f, 90.0f));
    }

    public static Vector2f smooth(Vector2f targetRotation) {
        float yaw = targetRotation.x;
        float pitch = targetRotation.y;
        return new Vector2f(yaw, pitch);
    }

    public static Vector2f smooth(Vector2f lastRotation, Vector2f targetRotation, double speed) {
        float yaw = targetRotation.x;
        float pitch = targetRotation.y;
        return new Vector2f(yaw, pitch);
    }

    public static Vector2f applySensitivityPatch(Vector2f rotation) {
        Vector2f previousRotation = RotationUtil.mc.thePlayer.getPreviousRotation();
        float mouseSensitivity = (float)((double)RotationUtil.mc.gameSettings.mouseSensitivity * (1.0 + Math.random() / 1.0E7) * (double)0.6f + (double)0.2f);
        double multiplier = (double)(mouseSensitivity * mouseSensitivity * mouseSensitivity * 8.0f) * 0.15;
        float yaw = previousRotation.x + (float)((double)Math.round((double)(rotation.x - previousRotation.x) / multiplier) * multiplier);
        float pitch = previousRotation.y + (float)((double)Math.round((double)(rotation.y - previousRotation.y) / multiplier) * multiplier);
        return new Vector2f(yaw, MathHelper.clamp_float(pitch, -90.0f, 90.0f));
    }

    private static float[] getRotationsByVec(Vec3 origin, Vec3 position) {
        Vec3 difference = position.subtract(origin);
        double distance = difference.flat().lengthVector();
        float yaw = (float)Math.toDegrees(Math.atan2(difference.zCoord, difference.xCoord)) - 90.0f;
        float pitch = (float)(-Math.toDegrees(Math.atan2(difference.yCoord, distance)));
        return new float[]{yaw, pitch};
    }

    public static float[] getRotationBlock(BlockPos pos) {
        return RotationUtil.getRotationsByVec(RotationUtil.mc.thePlayer.getPositionVector().addVector(0.0, RotationUtil.mc.thePlayer.getEyeHeight(), 0.0), new Vec3((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5));
    }

    public static float getYawDirection(float yaw, float strafe, float moveForward) {
        float rotationYaw = yaw;
        if (moveForward < 0.0f) {
            rotationYaw += 180.0f;
        }
        float forward = 1.0f;
        if (moveForward < 0.0f) {
            forward = -0.5f;
        } else if (moveForward > 0.0f) {
            forward = 0.5f;
        }
        if (strafe > 0.0f) {
            rotationYaw -= 90.0f * forward;
        }
        if (strafe < 0.0f) {
            rotationYaw += 90.0f * forward;
        }
        return rotationYaw;
    }

    public static float getClampRotation() {
        float rotationYaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
        float n = 1.0f;
        if (Minecraft.getMinecraft().thePlayer.movementInput.moveForward < 0.0f) {
            rotationYaw += 180.0f;
            n = -0.5f;
        } else if (Minecraft.getMinecraft().thePlayer.movementInput.moveForward > 0.0f) {
            n = 0.5f;
        }
        if (Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe > 0.0f) {
            rotationYaw -= 90.0f * n;
        }
        if (Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe < 0.0f) {
            rotationYaw += 90.0f * n;
        }
        return rotationYaw * ((float)Math.PI / 180);
    }

    @Setter
    @Getter
    public static class Rotation {
        float yaw;
        float pitch;

        public Rotation(float yaw, float pitch) {
            this.yaw = yaw;
            this.pitch = pitch;
        }

        public void toPlayer(EntityPlayer player) {
            if (Float.isNaN(this.yaw) || Float.isNaN(this.pitch)) {
                return;
            }
            this.fixedSensitivity(RotationUtil.mc.gameSettings.mouseSensitivity);
            player.rotationYaw = this.yaw;
            player.rotationPitch = this.pitch;
        }

        public void fixedSensitivity(Float sensitivity) {
            float f = sensitivity * 0.6f + 0.2f;
            float gcd = f * f * f * 1.2f;
            this.yaw -= this.yaw % gcd;
            this.pitch -= this.pitch % gcd;
        }

        public static float updateRotation(float current, float calc, float maxDelta) {
            float f = MathHelper.wrapAngleTo180_float(calc - current);
            if (f > maxDelta) {
                f = maxDelta;
            }
            if (f < -maxDelta) {
                f = -maxDelta;
            }
            return current + f;
        }

        public float rotateToYaw(float yawSpeed, float currentYaw, float calcYaw) {
            float yaw = Rotation.updateRotation(currentYaw, calcYaw, yawSpeed + RandomUtils.nextFloat(0.0f, 15.0f));
            double diffYaw = MathHelper.wrapAngleTo180_float(calcYaw - currentYaw);
            if ((double)(-yawSpeed) > diffYaw || diffYaw > (double)yawSpeed) {
                yaw += (float)((double)RandomUtils.nextFloat(1.0f, 2.0f) * Math.sin((double)RotationUtil.mc.thePlayer.rotationPitch * Math.PI));
            }
            if (yaw == currentYaw) {
                return currentYaw;
            }
            if ((double)RotationUtil.mc.gameSettings.mouseSensitivity == 0.5) {
                RotationUtil.mc.gameSettings.mouseSensitivity = 0.47887325f;
            }
            float f1 = RotationUtil.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
            float f2 = f1 * f1 * f1 * 8.0f;
            int deltaX = (int)((6.667 * (double)yaw - 6.666666666666667 * (double)currentYaw) / (double)f2);
            float f3 = (float)deltaX * f2;
            yaw = (float)((double)currentYaw + (double)f3 * 0.15);
            return yaw;
        }

        public float rotateToYaw(float yawSpeed, float[] currentRots, float calcYaw) {
            float yaw = Rotation.updateRotation(currentRots[0], calcYaw, yawSpeed + RandomUtils.nextFloat(0.0f, 15.0f));
            if (yaw != calcYaw) {
                yaw += (float)((double)RandomUtils.nextFloat(1.0f, 2.0f) * Math.sin((double)currentRots[1] * Math.PI));
            }
            if (yaw == currentRots[0]) {
                return currentRots[0];
            }
            yaw += (float)(ThreadLocalRandom.current().nextGaussian() * 0.2);
            if ((double)RotationUtil.mc.gameSettings.mouseSensitivity == 0.5) {
                RotationUtil.mc.gameSettings.mouseSensitivity = 0.47887325f;
            }
            float f1 = RotationUtil.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
            float f2 = f1 * f1 * f1 * 8.0f;
            int deltaX = (int)((6.667 * (double)yaw - 6.6666667 * (double)currentRots[0]) / (double)f2);
            float f3 = (float)deltaX * f2;
            yaw = (float)((double)currentRots[0] + (double)f3 * 0.15);
            return yaw;
        }

        public float rotateToPitch(float pitchSpeed, float currentPitch, float calcPitch) {
            float pitch = Rotation.updateRotation(currentPitch, calcPitch, pitchSpeed + RandomUtils.nextFloat(0.0f, 15.0f));
            if (pitch != calcPitch) {
                pitch += (float)((double)RandomUtils.nextFloat(1.0f, 2.0f) * Math.sin((double)RotationUtil.mc.thePlayer.rotationYaw * Math.PI));
            }
            if ((double)RotationUtil.mc.gameSettings.mouseSensitivity == 0.5) {
                RotationUtil.mc.gameSettings.mouseSensitivity = 0.47887325f;
            }
            float f1 = RotationUtil.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
            float f2 = f1 * f1 * f1 * 8.0f;
            int deltaY = (int)((6.667 * (double)pitch - 6.666667 * (double)currentPitch) / (double)f2) * -1;
            float f3 = (float)deltaY * f2;
            float f4 = (float)((double)currentPitch - (double)f3 * 0.15);
            pitch = MathHelper.clamp_float(f4, -90.0f, 90.0f);
            return pitch;
        }

        public float rotateToPitch(float pitchSpeed, float[] currentRots, float calcPitch) {
            float pitch = Rotation.updateRotation(currentRots[1], calcPitch, pitchSpeed + RandomUtils.nextFloat(0.0f, 15.0f));
            if (pitch != calcPitch) {
                pitch += (float)((double)RandomUtils.nextFloat(1.0f, 2.0f) * Math.sin((double)currentRots[0] * Math.PI));
            }
            if ((double)RotationUtil.mc.gameSettings.mouseSensitivity == 0.5) {
                RotationUtil.mc.gameSettings.mouseSensitivity = 0.47887325f;
            }
            float f1 = RotationUtil.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
            float f2 = f1 * f1 * f1 * 8.0f;
            int deltaY = (int)((6.667 * (double)pitch - 6.666667 * (double)currentRots[1]) / (double)f2) * -1;
            float f3 = (float)deltaY * f2;
            float f4 = (float)((double)currentRots[1] - (double)f3 * 0.15);
            pitch = MathHelper.clamp_float(f4, -90.0f, 90.0f);
            return pitch;
        }

    }
    public static float[] getRotationToBlock(BlockPos pos) {
        double x = pos.getX() - Minecraft.getMinecraft().thePlayer.posX;
        double y = pos.getY() - Minecraft.getMinecraft().thePlayer.posY + 1.7;
        double z = pos.getZ() - Minecraft.getMinecraft().thePlayer.posZ;
        double dist = Math.sqrt(x * x + z * z);
        float yaw = (float) Math.toDegrees(Math.atan2(z, x)) - 90.0F;
        float pitch = (float) -Math.toDegrees(Math.atan2(y, dist));
        return new float[]{yaw, pitch};
    }
}

