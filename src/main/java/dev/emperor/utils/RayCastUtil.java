package dev.emperor.utils;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import dev.emperor.Client;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.compatibility.util.vector.Vector2f;

public final class RayCastUtil {
    public static MovingObjectPosition rayCast(Vector2f rotation, double range) {
        return RayCastUtil.rayCast(rotation, range, 0.0f);
    }

    public static MovingObjectPosition rayCast(Vector2f rotation, double range, float expand) {
        return RayCastUtil.rayCast(rotation, range, expand, Client.mc.thePlayer);
    }

    public static MovingObjectPosition rayCast(Vector2f rotation, double range, float expand, Entity entity) {
        float partialTicks = Client.mc.timer.renderPartialTicks;
        if (entity != null && Client.mc.theWorld != null) {
            MovingObjectPosition objectMouseOver = entity.rayTraceCustom(range, rotation.x, rotation.y);
            double d1 = range;
            Vec3 vec3 = entity.getPositionEyes(partialTicks);
            if (objectMouseOver != null) {
                d1 = objectMouseOver.hitVec.distanceTo(vec3);
            }
            Vec3 vec31 = Client.mc.thePlayer.getVectorForRotation(rotation.y, rotation.x);
            Vec3 vec32 = vec3.addVector(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range);
            Entity pointedEntity = null;
            Vec3 vec33 = null;
            float f = 1.0f;
            List<Entity> list = Client.mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range).expand(1.0, 1.0, 1.0), (Predicate<? super Entity>)Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
            double d2 = d1;
            for (Entity entity1 : list) {
                double d3;
                float f1 = entity1.getCollisionBorderSize() + expand;
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(f1, f1, f1);
                MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
                if (axisalignedbb.isVecInside(vec3)) {
                    if (!(d2 >= 0.0)) continue;
                    pointedEntity = entity1;
                    vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                    d2 = 0.0;
                    continue;
                }
                if (movingobjectposition == null || !((d3 = vec3.distanceTo(movingobjectposition.hitVec)) < d2) && d2 != 0.0) continue;
                pointedEntity = entity1;
                vec33 = movingobjectposition.hitVec;
                d2 = d3;
            }
            if (pointedEntity != null && (d2 < d1 || objectMouseOver == null)) {
                objectMouseOver = new MovingObjectPosition(pointedEntity, vec33);
            }
            return objectMouseOver;
        }
        return null;
    }

    public static boolean overBlock(Vector2f rotation, EnumFacing enumFacing, BlockPos pos, boolean strict) {
        MovingObjectPosition movingObjectPosition = Client.mc.thePlayer.rayTraceCustom(4.5, rotation.x, rotation.y);
        if (movingObjectPosition == null) {
            return false;
        }
        Vec3 hitVec = movingObjectPosition.hitVec;
        if (hitVec == null) {
            return false;
        }
        return movingObjectPosition.getBlockPos().equals(pos) && (!strict || movingObjectPosition.sideHit == enumFacing);
    }

    public static boolean overBlock(EnumFacing enumFacing, BlockPos pos, boolean strict) {
        MovingObjectPosition movingObjectPosition = Client.mc.objectMouseOver;
        if (movingObjectPosition == null) {
            return false;
        }
        Vec3 hitVec = movingObjectPosition.hitVec;
        if (hitVec == null) {
            return false;
        }
        return movingObjectPosition.getBlockPos().equals(pos) && (!strict || movingObjectPosition.sideHit == enumFacing);
    }

    public static Boolean overBlock(Vector2f rotation, BlockPos pos) {
        return RayCastUtil.overBlock(rotation, EnumFacing.UP, pos, false);
    }

    public static Boolean overBlock(Vector2f rotation, BlockPos pos, EnumFacing enumFacing) {
        return RayCastUtil.overBlock(rotation, enumFacing, pos, true);
    }

    public static MovingObjectPosition rayCast(float partialTicks) {
        MovingObjectPosition objectMouseOver = null;
        Entity entity = Client.mc.getRenderViewEntity();
        if (entity != null && Client.mc.theWorld != null) {
            Client.mc.mcProfiler.startSection("pick");
            Client.mc.pointedEntity = null;
            double d0 = Client.mc.playerController.getBlockReachDistance();
            objectMouseOver = entity.rayTrace(d0, partialTicks);
            double d2 = d0;
            Vec3 vec3 = entity.getPositionEyes(partialTicks);
            boolean flag = false;
            boolean flag2 = true;
            if (Client.mc.playerController.extendedReach()) {
                d0 = 6.0;
                d2 = 6.0;
            } else if (d0 > 3.0) {
                flag = true;
            }
            if (objectMouseOver != null) {
                d2 = objectMouseOver.hitVec.distanceTo(vec3);
            }
            Vec3 vec4 = entity.getLook(partialTicks);
            Vec3 vec5 = vec3.addVector(vec4.xCoord * d0, vec4.yCoord * d0, vec4.zCoord * d0);
            Entity pointedEntity = null;
            Vec3 vec6 = null;
            float f = 1.0f;
            List<Entity> list = Client.mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec4.xCoord * d0, vec4.yCoord * d0, vec4.zCoord * d0).expand(1.0, 1.0, 1.0), (Predicate<? super Entity>)Predicates.and((Predicate[])new Predicate[]{EntitySelectors.NOT_SPECTATING}));
            double d3 = d2;
            Object realBB = null;
            for (int i = 0; i < list.size(); ++i) {
                double d4;
                Entity entity2 = list.get(i);
                float f2 = entity2.getCollisionBorderSize();
                AxisAlignedBB axisalignedbb = entity2.getEntityBoundingBox().expand(f2, f2, f2);
                MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec5);
                if (axisalignedbb.isVecInside(vec3)) {
                    if (!(d3 >= 0.0)) continue;
                    pointedEntity = entity2;
                    vec6 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                    d3 = 0.0;
                    continue;
                }
                if (movingobjectposition == null || !((d4 = vec3.distanceTo(movingobjectposition.hitVec)) < d3) && d3 != 0.0) continue;
                boolean flag3 = false;
                if (entity2 == entity.ridingEntity && !flag3) {
                    if (d3 != 0.0) continue;
                    pointedEntity = entity2;
                    vec6 = movingobjectposition.hitVec;
                    continue;
                }
                pointedEntity = entity2;
                vec6 = movingobjectposition.hitVec;
                d3 = d4;
            }
            if (pointedEntity != null && flag && vec3.distanceTo(vec6) > 3.0) {
                pointedEntity = null;
                objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec6, null, new BlockPos(vec6));
            }
            if (pointedEntity != null && (d3 < d2 || objectMouseOver == null)) {
                objectMouseOver = new MovingObjectPosition(pointedEntity, vec6);
                if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame) {
                    // empty if block
                }
            }
        }
        return objectMouseOver;
    }

}

