package dev.emperor.module.modules.movement;

import dev.emperor.event.EventTarget;
import dev.emperor.event.world.EventMotion;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.modules.combat.KillAura;
import dev.emperor.module.modules.player.Blink;
import dev.emperor.module.values.ModeValue;
import dev.emperor.utils.RotationComponent;
import dev.emperor.utils.player.MoveUtil;
import dev.emperor.utils.player.PlayerUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;

public class Speed
extends Module {
    public static Speed INSTANCE;
    private static final ModeValue<speedMode> MODE;
    private static boolean wasOnGround;

    public Speed() {
        super("Speed", Category.Movement);
        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        if (Speed.mc.thePlayer == null) {
            return;
        }
        if (MODE.getValue() == speedMode.AAC4) {
            Speed.mc.timer.timerSpeed = 1.0f;
        }
    }

        @EventTarget
        public void onMove(EventMotion event) {
            this.setSuffix(((speedMode)((Object)MODE.getValue())).name());
            if (event.isPre()) {
                switch ((speedMode)((Object)MODE.getValue())) {
                    case HvH: {
                        if (MoveUtil.isMoving()) {
                            if (Speed.mc.thePlayer.onGround) {
                                Speed.mc.thePlayer.motionY = 0.42;
                                wasOnGround = true;
                            } else if (wasOnGround) {
                                if (!Speed.mc.thePlayer.isCollidedHorizontally) {
                                    Speed.mc.thePlayer.motionY = -0.0484000015258789;
                                }
                            wasOnGround = false;
                        }
                        MoveUtil.setMotion(0.63);
                        break;
                    }
                    Speed.mc.thePlayer.motionZ = 0.0;
                    Speed.mc.thePlayer.motionX = 0.0;
                    break;
                }
                case Vulcan: {
                    switch (Speed.mc.thePlayer.offGroundTicks) {
                        case 0: {
                            Speed.mc.thePlayer.jump();
                            if (Speed.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                                MoveUtil.strafe(0.6);
                                break;
                            }
                            MoveUtil.strafe(0.485);
                            break;
                        }
                        case 9: {
                            if (PlayerUtil.blockRelativeToPlayer(0.0, Speed.mc.thePlayer.motionY, 0.0) instanceof BlockAir) break;
                            MoveUtil.strafe();
                            break;
                        }
                        case 1: 
                        case 2: {
                            MoveUtil.strafe();
                            break;
                        }
                        case 5: {
                            Speed.mc.thePlayer.motionY = MoveUtil.predictedMotion(Speed.mc.thePlayer.motionY, 2);
                        }
                    }
                    break;
                }
                case WatchDog: {
                    if (Speed.mc.thePlayer.hurtTime > 6) {
                        Speed.mc.thePlayer.motionX *= 1.007;
                        Speed.mc.thePlayer.motionZ *= 1.007;
                    }
                    if ((Speed.mc.gameSettings.keyBindLeft.pressed || Speed.mc.gameSettings.keyBindRight.pressed) && Speed.mc.thePlayer.motionY < -0.05 && Speed.mc.thePlayer.motionY > -0.08) {
                        MoveUtil.strafe(0.15);
                    }
                    if (!Speed.mc.thePlayer.onGround || Speed.mc.thePlayer.moveForward == 0.0f && Speed.mc.thePlayer.moveStrafing == 0.0f) break;
                    Speed.mc.thePlayer.jump();
                    MoveUtil.setSpeed((float)(0.46 + (double)PlayerUtil.getSpeedPotion() * 0.02));
                    break;
                }
                case AAC4: {
                    if (Speed.mc.thePlayer.isInWater()) {
                        return;
                    }
                    if (MoveUtil.isMoving()) {
                        if (Speed.mc.thePlayer.onGround) {
                            Speed.mc.gameSettings.keyBindJump.pressed = false;
                            Speed.mc.thePlayer.jump();
                        }
                        if (!Speed.mc.thePlayer.onGround && (double)Speed.mc.thePlayer.fallDistance <= 0.1) {
                            Speed.mc.thePlayer.speedInAir = 0.03f;
                            Speed.mc.timer.timerSpeed = 1.45f;
                        }
                        if ((double)Speed.mc.thePlayer.fallDistance > 0.1 && (double)Speed.mc.thePlayer.fallDistance < 1.3) {
                            Speed.mc.thePlayer.speedInAir = 0.0105f;
                            Speed.mc.timer.timerSpeed = 0.7f;
                        }
                        if (!((double)Speed.mc.thePlayer.fallDistance >= 1.3)) break;
                        Speed.mc.timer.timerSpeed = 1.0f;
                        Speed.mc.thePlayer.speedInAir = 0.0105f;
                        break;
                    }
                    Speed.mc.thePlayer.motionX = 0.0;
                    Speed.mc.thePlayer.motionZ = 0.0;
                    break;
                }
                case Grim: {
                    AxisAlignedBB playerBox = Speed.mc.thePlayer.boundingBox.expand(1.0, 1.0, 1.0);
                    int c = 0;
                    for (Entity entity : Speed.mc.theWorld.loadedEntityList) {
                        if (!(entity instanceof EntityLivingBase) && !(entity instanceof EntityBoat) && !(entity instanceof EntityMinecart) && !(entity instanceof EntityFishHook) || entity instanceof EntityArmorStand || entity.getEntityId() == Speed.mc.thePlayer.getEntityId() || !playerBox.intersectsWith(entity.boundingBox) || entity.getEntityId() == -8 || entity.getEntityId() == -1337 || this.getModule(Blink.class).getState()) continue;
                        ++c;
                    }
                    if (c > 0 && MoveUtil.isMoving()) {
                        double strafeOffset = (double)Math.min(c, 3) * 0.08;
                        float yaw = this.getMoveYaw();
                        double mx = -Math.sin(Math.toRadians(yaw));
                        double mz = Math.cos(Math.toRadians(yaw));
                        Speed.mc.thePlayer.addVelocity(mx * strafeOffset, 0.0, mz * strafeOffset);
                        if (c < 4 && KillAura.target != null && this.shouldFollow()) {
                            Speed.mc.gameSettings.keyBindLeft.pressed = true;
                            break;
                        }
                        Speed.mc.gameSettings.keyBindLeft.pressed = GameSettings.isKeyDown(Speed.mc.gameSettings.keyBindLeft);
                        break;
                    }
                    Speed.mc.gameSettings.keyBindLeft.pressed = GameSettings.isKeyDown(Speed.mc.gameSettings.keyBindLeft);
                    break;
                }
                case Pika: {
                    if (MoveUtil.isMoving()) {
                        if (Speed.mc.thePlayer.onGround) {
                            Speed.mc.thePlayer.motionY = 0.42;
                            wasOnGround = true;
                        } else if (wasOnGround) {
                            wasOnGround = false;
                        }
                        MoveUtil.setMotion(MoveUtil.getBaseMoveSpeed() * (double)0.6f, Speed.mc.thePlayer.getRotationYawHead());
                        break;
                    }
                    Speed.mc.thePlayer.motionX *= 0.8;
                    Speed.mc.thePlayer.motionZ *= 0.8;
                }
            }
        }
    }

    public boolean shouldFollow() {
        return this.getState() && Speed.mc.gameSettings.keyBindJump.isKeyDown();
    }

    private float getMoveYaw() {
        EntityPlayerSP thePlayer = Speed.mc.thePlayer;
        float moveYaw = thePlayer.rotationYaw;
        if (thePlayer.moveForward != 0.0f && thePlayer.moveStrafing == 0.0f) {
            moveYaw += thePlayer.moveForward > 0.0f ? 0.0f : 180.0f;
        } else if (thePlayer.moveForward != 0.0f && thePlayer.moveStrafing != 0.0f) {
            moveYaw = thePlayer.moveForward > 0.0f ? (moveYaw += thePlayer.moveStrafing > 0.0f ? -45.0f : 45.0f) : (moveYaw -= thePlayer.moveStrafing > 0.0f ? -45.0f : 45.0f);
            moveYaw += thePlayer.moveForward > 0.0f ? 0.0f : 180.0f;
        } else if (thePlayer.moveStrafing != 0.0f && thePlayer.moveForward == 0.0f) {
            moveYaw += thePlayer.moveStrafing > 0.0f ? -70.0f : 70.0f;
        }
        if (KillAura.target != null && Speed.mc.gameSettings.keyBindJump.isKeyDown()) {
            moveYaw = RotationComponent.rotation.getX();
        }
        return moveYaw;
    }

    static {
        MODE = new ModeValue("Mode", (Enum[])speedMode.values(), (Enum)speedMode.HvH);
        wasOnGround = false;
    }

    static enum speedMode {
        HvH,
        Pika,
        WatchDog,
        Grim,
        Vulcan,
        AAC4;

    }
}

