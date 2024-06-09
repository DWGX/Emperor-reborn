package dev.emperor.module.modules.combat;

import dev.emperor.event.EventTarget;
import dev.emperor.event.misc.EventClick;
import dev.emperor.event.world.EventTick;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.values.BoolValue;
import dev.emperor.module.values.NumberValue;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class Reach
extends Module {
    public static NumberValue MinReach = new NumberValue("Min", 3.5, 0.0, 6.0, 0.1);
    public static NumberValue MaxReach = new NumberValue("Max", 6.0, 0.1, 6.0, 0.1);
    private final BoolValue RandomReach = new BoolValue("Random Reach", true);
    private final BoolValue weaponOnly = new BoolValue("Weapon Only", false);
    private final BoolValue movingOnly = new BoolValue("Moving Only", false);
    private final BoolValue sprintOnly = new BoolValue("Sprint Only", false);
    private final BoolValue hitThroughBlocks = new BoolValue("HitThroughBlocks", false);

    public Reach() {
        super("Reach", Category.Combat);
    }

    public static double getRandomDoubleInRange(double minDouble, double maxDouble) {
        return minDouble >= maxDouble ? minDouble : new Random().nextDouble() * (maxDouble - minDouble) + minDouble;
    }

    public static Object[] doReach(double reachValue, double AABB) {
        Entity target = mc.getRenderViewEntity();
        Entity entity = null;
        if (target == null || Reach.mc.theWorld == null) {
            return null;
        }
        Reach.mc.mcProfiler.startSection("pick");
        Vec3 targetEyes = target.getPositionEyes(0.0f);
        Vec3 targetLook = target.getLook(0.0f);
        Vec3 targetVector = targetEyes.addVector(targetLook.xCoord * reachValue, targetLook.yCoord * reachValue, targetLook.zCoord * reachValue);
        Vec3 targetVec = null;
        List<Entity> targetHitbox = Reach.mc.theWorld.getEntitiesWithinAABBExcludingEntity(target, target.getEntityBoundingBox().addCoord(targetLook.xCoord * reachValue, targetLook.yCoord * reachValue, targetLook.zCoord * reachValue).expand(1.0, 1.0, 1.0));
        double reaching = reachValue;
        for (Entity targetEntity : targetHitbox) {
            double targetHitVec;
            if (!targetEntity.canBeCollidedWith()) continue;
            float targetCollisionBorderSize = targetEntity.getCollisionBorderSize();
            AxisAlignedBB targetAABB = targetEntity.getEntityBoundingBox().expand(targetCollisionBorderSize, targetCollisionBorderSize, targetCollisionBorderSize);
            targetAABB = targetAABB.expand(AABB, AABB, AABB);
            MovingObjectPosition targetPosition = targetAABB.calculateIntercept(targetEyes, targetVector);
            if (targetAABB.isVecInside(targetEyes)) {
                if (!(0.0 < reaching) && reaching != 0.0) continue;
                entity = targetEntity;
                targetVec = targetPosition == null ? targetEyes : targetPosition.hitVec;
                reaching = 0.0;
                continue;
            }
            if (targetPosition == null || !((targetHitVec = targetEyes.distanceTo(targetPosition.hitVec)) < reaching) && reaching != 0.0) continue;
            if (targetEntity == target.ridingEntity) {
                if (reaching != 0.0) continue;
                entity = targetEntity;
                targetVec = targetPosition.hitVec;
                continue;
            }
            entity = targetEntity;
            targetVec = targetPosition.hitVec;
            reaching = targetHitVec;
        }
        if (reaching < reachValue && !(entity instanceof EntityLivingBase) && !(entity instanceof EntityItemFrame)) {
            entity = null;
        }
        Reach.mc.mcProfiler.endSection();
        if (entity == null || targetVec == null) {
            return null;
        }
        return new Object[]{entity, targetVec};
    }

    @EventTarget
    public void onTick(EventTick e) {
        this.setSuffix(String.format("Min:%s Max:%s", MinReach.getValue(), MaxReach.getValue()));
    }

    @EventTarget
    public void onClicked(EventClick ev) {
        BlockPos blocksReach;
        if (((Boolean)this.weaponOnly.getValue()).booleanValue()) {
            if (Reach.mc.thePlayer.getCurrentEquippedItem() == null) {
                return;
            }
            if (!(Reach.mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword) && !(Reach.mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemAxe)) {
                return;
            }
        }
        if (((Boolean)this.movingOnly.getValue()).booleanValue() && (double)Reach.mc.thePlayer.moveForward == 0.0 && (double)Reach.mc.thePlayer.moveStrafing == 0.0) {
            return;
        }
        if (((Boolean)this.sprintOnly.getValue()).booleanValue() && !Reach.mc.thePlayer.isSprinting()) {
            return;
        }
        if (!((Boolean)this.hitThroughBlocks.getValue()).booleanValue() && Reach.mc.objectMouseOver != null && (blocksReach = Reach.mc.objectMouseOver.getBlockPos()) != null && Reach.mc.theWorld.getBlockState(blocksReach).getBlock() != Blocks.air) {
            return;
        }
        double Reach2 = (Boolean)this.RandomReach.getValue() != false ? Reach.getRandomDoubleInRange((Double)MinReach.getValue(), (Double)MaxReach.getValue()) + 0.1 : (Double)MinReach.getValue();
        Object[] reach = Reach.doReach(Reach2, 0.0);
        Reach.doReach(Reach2, 0.0);
        if (reach == null) {
            return;
        }
        Reach.mc.objectMouseOver = new MovingObjectPosition((Entity)reach[0], (Vec3)reach[1]);
        Reach.mc.pointedEntity = (Entity)reach[0];
    }
}

