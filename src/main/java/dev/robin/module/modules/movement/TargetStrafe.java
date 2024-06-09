package dev.robin.module.modules.movement;

import dev.robin.Client;
import dev.robin.event.EventTarget;
import dev.robin.event.rendering.EventRender3D;
import dev.robin.event.world.EventJump;
import dev.robin.event.world.EventMotion;
import dev.robin.event.world.EventMoveInput;
import dev.robin.event.world.EventPacketReceive;
import dev.robin.event.world.EventStrafe;
import dev.robin.event.world.EventUpdate;
import dev.robin.module.Category;
import dev.robin.module.Module;
import dev.robin.module.modules.combat.AntiBot;
import dev.robin.module.modules.combat.KillAura;
import dev.robin.module.values.BoolValue;
import dev.robin.module.values.ColorValue;
import dev.robin.module.values.ModeValue;
import dev.robin.module.values.NumberValue;
import dev.robin.utils.player.MoveUtil;
import dev.robin.utils.player.PlayerUtil;
import dev.robin.utils.player.RotationUtil;
import dev.robin.utils.render.GLUtil;
import dev.robin.utils.render.RenderUtil;
import dev.robin.utils.vec.Vector3d;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.compatibility.util.vector.Vector2f;

public final class TargetStrafe
extends Module {
    public final ModeValue<RenderMode> renderModeValue = new ModeValue("Mode", (Enum[])RenderMode.values(), (Enum)RenderMode.NORMAL);
    public final NumberValue radiusValue = new NumberValue("Radius", 2.0, 0.1, 4.0, 0.1);
    public final NumberValue strafeSpeedValue = new NumberValue("Strafe Speed", 0.15, 0.01, 1.0, 0.05);
    public final BoolValue doTsValue = new BoolValue("Do Strafe", true);
    public final BoolValue holdSpaceValue = new BoolValue("Hold Space", true);
    public final BoolValue AutoJumpValue = new BoolValue("Auto Jump", true);
    private final BoolValue S08FlagCheckValue = new BoolValue("S08Flag Check", false);
    public NumberValue S08FlagTickValue = new NumberValue("S08Flag Ticks", 6.0, 0.0, 30.0, 1.0);
    public final NumberValue shapeValue = new NumberValue("Shape", 12.0, 0.0, 30.0, 1.0);
    private final NumberValue pointsValue = new NumberValue("Points", 12.0, 1.0, 90.0, 1.0);
    private final ColorValue renderColorValue = new ColorValue("Color", new Color(120, 255, 120).getRGB());
    private final ColorValue activePointColorValue = new ColorValue("Active Color", new Color(-2147418368).getRGB());
    private final ColorValue dormantPointColorValue = new ColorValue("Dormant Color", new Color(0x20FFFFFF).getRGB());
    private final ColorValue invalidPointColorValue = new ColorValue("Invalid Color", new Color(0x20FF0000).getRGB());
    private final List<Point3D> currentPoints = new ArrayList<Point3D>();
    EntityLivingBase currentTarget;
    private float yaw;
    private Entity target;
    private boolean left;
    private boolean colliding;
    public Point3D currentPoint;
    int flags;
    private final Predicate<Entity> ENTITY_FILTER = entity -> entity.isEntityAlive() && TargetStrafe.mc.thePlayer.getDistanceSqToEntity((Entity)entity) <= 6.0 && entity != TargetStrafe.mc.thePlayer && AntiBot.isServerBot(entity) && !(entity instanceof EntityArmorStand);

    public TargetStrafe() {
        super("TargetStrafe", Category.Movement);
    }

    @EventTarget
    public void onMove(EventMoveInput event) {
        if (!((Boolean)this.doTsValue.getValue()).booleanValue()) {
            return;
        }
        if (this.flags != 0) {
            return;
        }
        if (this.target != null && this.distanceToTarget() <= 3.0) {
            this.setRotation();
            event.setForward(1.0f);
            event.setStrafe(0.0f);
            event.setSneak(false);
        }
    }

    @EventTarget
    public void onJump(EventJump event) {
        if (!((Boolean)this.doTsValue.getValue()).booleanValue()) {
            return;
        }
        if (this.target != null && this.distanceToTarget() <= 3.0) {
            this.setRotation();
            event.setYaw(this.yaw);
        }
    }

    @EventTarget
    public void onStrafe(EventStrafe event) {
        if (!((Boolean)this.doTsValue.getValue()).booleanValue()) {
            return;
        }
        if (this.target != null && this.distanceToTarget() <= 3.0) {
            this.setRotation();
            event.setYaw(this.yaw);
            if (TargetStrafe.mc.thePlayer.hurtTime != 0) {
                return;
            }
            if (TargetStrafe.mc.thePlayer.onGround && ((Boolean)this.AutoJumpValue.getValue()).booleanValue()) {
                TargetStrafe.mc.thePlayer.jump();
            }
            float friction = 0.2f;
            event.setFriction(friction);
            MoveUtil.strafe(0.15f, this.yaw);
        }
    }

    private void setRotation() {
        if (this.target == null) {
            return;
        }
        float yaw = RotationUtil.smooth((Vector2f)RotationUtil.calculate((Vector3d)new Vector3d((double)this.target.posX, (double)this.target.posY, (double)this.target.posZ))).x + 135.0f * (float)(this.left ? -1 : 1);
        double range = (Double)this.radiusValue.getValue();
        double posX = (double)(-MathHelper.sin((float)Math.toRadians(yaw))) * range + this.target.posX;
        double posZ = (double)MathHelper.cos((float)Math.toRadians(yaw)) * range + this.target.posZ;
        this.yaw = yaw = RotationUtil.smooth((Vector2f)RotationUtil.calculate((Vector3d)new Vector3d((double)posX, (double)(this.target.posY + (double)this.target.getEyeHeight()), (double)posZ))).x;
    }

    @EventTarget
    public void onPacketReceive(EventPacketReceive event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook && ((Boolean)this.S08FlagCheckValue.getValue()).booleanValue()) {
            this.flags = ((Double)this.S08FlagTickValue.getValue()).intValue();
        }
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (!((Boolean)this.doTsValue.getValue()).booleanValue()) {
            return;
        }
        this.updateTarget();
        if (!((Boolean)this.doTsValue.getValue()).booleanValue()) {
            return;
        }
        if (((Boolean)this.S08FlagCheckValue.getValue()).booleanValue() && this.flags > 0) {
            --this.flags;
        }
        if (this.flags != 0) {
            return;
        }
        if (this.target == null) {
            return;
        }
        if (TargetStrafe.mc.gameSettings.keyBindSprint.isKeyDown() && this.target != null && this.distanceToTarget() <= 3.0) {
            TargetStrafe.mc.gameSettings.keyBindSprint.pressed = false;
        }
        if (TargetStrafe.mc.gameSettings.keyBindLeft.isKeyDown()) {
            this.left = true;
        }
        if (TargetStrafe.mc.gameSettings.keyBindRight.isKeyDown()) {
            this.left = false;
        }
        if (TargetStrafe.mc.thePlayer.isCollidedHorizontally || !PlayerUtil.isBlockUnder(5.0)) {
            if (!this.colliding) {
                this.left = !this.left;
            }
            this.colliding = true;
        } else {
            this.colliding = false;
        }
    }

    private double distanceToTarget() {
        return TargetStrafe.mc.thePlayer.getDistanceSqToEntity(this.target);
    }

    private void updateTarget() {
        KillAura aura = Client.instance.moduleManager.getModule(KillAura.class);
        if (aura.state && KillAura.target != null && (this.target == null || !this.target.isEntityAlive() || this.distanceToTarget() > 6.0 || TargetStrafe.mc.thePlayer.offGroundTicks < 2)) {
            this.target = aura.state && KillAura.target != null ? KillAura.target : this.getTarget();
        }
        if (this.target.isDead) {
            this.target = null;
        }
    }

    private Entity getTarget() {
        return TargetStrafe.mc.theWorld.loadedEntityList.parallelStream().filter(this.ENTITY_FILTER).findFirst().orElse(null);
    }

    @EventTarget
    public void onEventMotion(EventMotion event) {
        if (this.flags != 0) {
            return;
        }
        if (event.isPre()) {
            EntityLivingBase target = KillAura.getTarget();
            if (target != null) {
                this.currentTarget = target;
                this.collectPoints(this.currentTarget);
                this.currentPoint = this.findPoint(target, this.currentPoints);
            } else {
                this.currentTarget = null;
                this.currentPoint = null;
            }
        }
    }

    @EventTarget
    public void onRender3D(EventRender3D event) {
        if (this.flags != 0) {
            return;
        }
        if (this.renderModeValue.getValue() == RenderMode.POINT && KillAura.getTarget() != null) {
            float partialTicks = event.getPartialTicks();
            for (Point3D point : this.currentPoints) {
                double pointSize = 0.03;
                int color = this.currentPoint == point ? (Integer)this.activePointColorValue.getValue() : (point.valid ? ((Integer)this.dormantPointColorValue.getValue()).intValue() : ((Integer)this.invalidPointColorValue.getValue()).intValue());
                double x2 = RenderUtil.interpolate(point.prevX, point.x, (double)partialTicks);
                double y2 = RenderUtil.interpolate(point.prevY, point.y, (double)partialTicks);
                double z = RenderUtil.interpolate(point.prevZ, point.z, (double)partialTicks);
                AxisAlignedBB bb = new AxisAlignedBB(x2, y2, z, x2 + 0.03, y2 + 0.03, z + 0.03);
                GLUtil.enableBlending();
                GLUtil.disableDepth();
                GLUtil.disableTexture2D();
                RenderUtil.color(color);
                double renderX = Minecraft.getMinecraft().getRenderManager().renderPosX;
                double renderY = Minecraft.getMinecraft().getRenderManager().renderPosY;
                double renderZ = Minecraft.getMinecraft().getRenderManager().renderPosZ;
                GL11.glTranslated((double)(-renderX), (double)(-renderY), (double)(-renderZ));
                RenderGlobal.drawSelectionBoundingBox(bb, false, true);
                GL11.glTranslated((double)renderX, (double)renderY, (double)renderZ);
                GLUtil.disableBlending();
                GLUtil.enableDepth();
                GLUtil.enableTexture2D();
            }
        }
        if (this.renderModeValue.getValue() == RenderMode.NORMAL) {
            double x2 = KillAura.getTarget().lastTickPosX + (KillAura.getTarget().posX - KillAura.getTarget().lastTickPosX) * (double)Minecraft.getMinecraft().timer.renderPartialTicks - TargetStrafe.mc.getRenderManager().viewerPosX;
            double y2 = KillAura.getTarget().lastTickPosY + (KillAura.getTarget().posY - KillAura.getTarget().lastTickPosY) * (double)Minecraft.getMinecraft().timer.renderPartialTicks - TargetStrafe.mc.getRenderManager().viewerPosY;
            double z2 = KillAura.getTarget().lastTickPosZ + (KillAura.getTarget().posZ - KillAura.getTarget().lastTickPosZ) * (double)Minecraft.getMinecraft().timer.renderPartialTicks - TargetStrafe.mc.getRenderManager().viewerPosZ;
            RenderUtil.TScylinder2(KillAura.getTarget(), x2, y2, z2, (Double)this.radiusValue.getValue() - 0.00625, 6.0f, ((Double)this.shapeValue.getValue()).intValue(), new Color(0, 0, 0).getRGB());
            RenderUtil.TScylinder2(KillAura.getTarget(), x2, y2, z2, (Double)this.radiusValue.getValue() + 0.00625, 6.0f, ((Double)this.shapeValue.getValue()).intValue(), new Color(0, 0, 0).getRGB());
            if (MoveUtil.isMoving() && KillAura.strict && ((Boolean)this.holdSpaceValue.getValue()).booleanValue() && TargetStrafe.mc.gameSettings.keyBindJump.isKeyDown()) {
                RenderUtil.drawCircle(KillAura.getTarget(), x2, y2, z2, (Double)this.radiusValue.getValue(), ((Double)this.shapeValue.getValue()).intValue(), 5.0f, (Integer)this.renderColorValue.getValue());
            } else if (MoveUtil.isMoving() && KillAura.strict && !((Boolean)this.holdSpaceValue.getValue()).booleanValue() && this.canStrafe()) {
                RenderUtil.drawCircle(KillAura.getTarget(), x2, y2, z2, (Double)this.radiusValue.getValue(), ((Double)this.shapeValue.getValue()).intValue(), 5.0f, (Integer)this.renderColorValue.getValue());
            } else {
                RenderUtil.drawCircle(KillAura.getTarget(), x2, y2, z2, (Double)this.radiusValue.getValue(), ((Double)this.shapeValue.getValue()).intValue(), 5.0f, new Color(255, 255, 255).getRGB());
            }
        }
    }

    private Point3D findPoint(EntityLivingBase target, List<Point3D> points) {
        Point3D bestPoint = null;
        float biggestDif = -1.0f;
        for (Point3D point : points) {
            float yawChange;
            if (!point.valid || (yawChange = Math.abs(this.getYawChangeToPoint(target, point))) <= biggestDif) continue;
            biggestDif = yawChange;
            bestPoint = point;
        }
        return bestPoint;
    }

    private float getYawChangeToPoint(EntityLivingBase target, Point3D point) {
        double xDist = point.x - target.posX;
        double zDist = point.z - target.posZ;
        float yaw = target.rotationYaw;
        float pitch = (float)(StrictMath.atan2(zDist, xDist) * 180.0 / Math.PI) - 90.0f;
        return yaw + MathHelper.wrapAngleTo180_float(pitch - yaw);
    }

    private void collectPoints(EntityLivingBase entity) {
        int size = ((Double)this.pointsValue.getValue()).intValue();
        double radius = (Double)this.radiusValue.getValue();
        this.currentPoints.clear();
        double x2 = entity.posX;
        double y2 = entity.posY;
        double z = entity.posZ;
        double prevX = entity.prevPosX;
        double prevY = entity.prevPosY;
        double prevZ = entity.prevPosZ;
        for (int i = 0; i < size; ++i) {
            double cos = radius * StrictMath.cos((float)i * ((float)Math.PI * 2) / (float)size);
            double sin = radius * StrictMath.sin((float)i * ((float)Math.PI * 2) / (float)size);
            double pointX = x2 + cos;
            double pointZ = z + sin;
            this.currentPoints.add(new Point3D(pointX, y2, pointZ, prevX + cos, prevY, prevZ + sin, this.validatePoint(pointX, pointZ)));
        }
    }

    private boolean validatePoint(double x2, double z) {
        Vec3 pointVec = new Vec3(x2, TargetStrafe.mc.thePlayer.posY, z);
        IBlockState blockState = TargetStrafe.mc.theWorld.getBlockState(new BlockPos(pointVec));
        boolean canBeSeen = TargetStrafe.mc.theWorld.rayTraceBlocks(TargetStrafe.mc.thePlayer.getPositionVector(), pointVec, false, false, false) == null;
        return !this.isOverVoid(x2, z) && !blockState.getBlock().canCollideCheck(blockState, false) && canBeSeen;
    }

    private boolean isOverVoid(double x2, double z) {
        double startY;
        for (double posY = startY = TargetStrafe.mc.thePlayer.posY; posY > 0.0; posY -= 1.0) {
            IBlockState state = TargetStrafe.mc.theWorld.getBlockState(new BlockPos(x2, posY, z));
            if (!state.getBlock().canCollideCheck(state, false)) continue;
            return startY - posY > 3.0;
        }
        return true;
    }

    public boolean keyMode() {
        boolean active;
        if (TargetStrafe.mc.gameSettings.keyBindBack.isKeyDown() || TargetStrafe.mc.gameSettings.keyBindRight.isKeyDown() || TargetStrafe.mc.gameSettings.keyBindLeft.isKeyDown()) {
            return false;
        }
        boolean bl = active = Minecraft.getMinecraft().thePlayer.movementInput.moveForward != 0.0f;
        if (((Boolean)this.holdSpaceValue.getValue()).booleanValue()) {
            return TargetStrafe.mc.gameSettings.keyBindJump.isKeyDown() && active;
        }
        return active;
    }

    public boolean canStrafe() {
        return KillAura.strict && KillAura.target != null && !Minecraft.getMinecraft().thePlayer.isSneaking() && this.keyMode();
    }

    public static enum RenderMode {
        NORMAL,
        POLYGON,
        POINT,
        OFF;

    }

    private static final class Point3D {
        private final double x;
        private final double y;
        private final double z;
        private final double prevX;
        private final double prevY;
        private final double prevZ;
        private final boolean valid;

        public Point3D(double x2, double y2, double z, double prevX, double prevY, double prevZ, boolean valid) {
            this.x = x2;
            this.y = y2;
            this.z = z;
            this.prevX = prevX;
            this.prevY = prevY;
            this.prevZ = prevZ;
            this.valid = valid;
        }
    }
}

