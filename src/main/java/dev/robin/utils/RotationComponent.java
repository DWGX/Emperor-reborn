package dev.robin.utils;

import dev.robin.event.EventTarget;
import dev.robin.event.world.EventJump;
import dev.robin.event.world.EventLook;
import dev.robin.event.world.EventMotion;
import dev.robin.event.world.EventMoveInput;
import dev.robin.event.world.EventStrafe;
import dev.robin.event.world.EventUpdate;
import dev.robin.module.modules.combat.KillAura;
import dev.robin.module.modules.movement.Speed;
import dev.robin.utils.Rotation;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import org.lwjgl.compatibility.util.vector.Vector2f;

public final class RotationComponent {
    private static Minecraft mc = Minecraft.getMinecraft();
    public static Vector2f rotation;
    public static Vector2f lastRotation;
    public static Vector2f targetRotation;
    public static Vector2f lastServerRotation;
    private static float rotationSpeed;
    public static boolean modify;
    private static boolean smoothed;
    private static boolean movementFix;
    private static boolean strict;

    public RotationComponent() {
        rotation = new Vector2f(0.0f, 0.0f);
    }

    public Vector2f getRotation() {
        return rotation;
    }

    public static void setRotation(Vector2f rotation, float rotationSpeed, boolean movementFix, boolean strict) {
        targetRotation = rotation;
        RotationComponent.rotationSpeed = rotationSpeed * 18.0f;
        RotationComponent.movementFix = movementFix;
        modify = true;
        RotationComponent.strict = strict;
        RotationComponent.smoothRotation();
    }

    public static void setRotations(Vector2f rotation, float rotationSpeed, boolean movementFix) {
        targetRotation = rotation;
        RotationComponent.rotationSpeed = rotationSpeed * 18.0f;
        RotationComponent.movementFix = movementFix;
        modify = true;
        strict = false;
        RotationComponent.smoothRotation();
    }

    public static void setRotation(Rotation rotation, float rotationSpeed, boolean movementFix) {
        targetRotation = rotation.toVec2f();
        RotationComponent.rotationSpeed = rotationSpeed * 18.0f;
        RotationComponent.movementFix = movementFix;
        modify = true;
        strict = false;
        RotationComponent.smoothRotation();
    }

    public static double getRotationDifference(Rotation rotation) {
        return lastServerRotation == null ? 0.0 : RotationComponent.getRotationDifference(rotation, lastServerRotation);
    }

    public static float getAngleDifference(float a, float b2) {
        return ((a - b2) % 360.0f + 540.0f) % 360.0f - 180.0f;
    }

    public static double getRotationDifference(Rotation a, Vector2f b2) {
        return Math.hypot(RotationComponent.getAngleDifference(a.getYaw(), b2.getX()), a.getPitch() - b2.getY());
    }

    @EventTarget(value=100)
    public void onMotion(EventUpdate event) {
        if (!modify || rotation == null || lastRotation == null || targetRotation == null) {
            lastServerRotation = targetRotation = new Vector2f(RotationComponent.mc.thePlayer.rotationYaw, RotationComponent.mc.thePlayer.rotationPitch);
            lastRotation = targetRotation;
            rotation = targetRotation;
        }
        if (modify) {
            RotationComponent.smoothRotation();
        }
    }

    @EventTarget(value=100)
    public void onMovementInput(EventMoveInput event) {
        if (modify && movementFix && !strict && (KillAura.target == null || !Speed.INSTANCE.shouldFollow())) {
            float yaw = rotation.getX();
            float forward = event.getForward();
            float strafe = event.getStrafe();
            double angle = MathHelper.wrapAngleTo180_double(Math.toDegrees(RotationComponent.getDirection(RotationComponent.mc.thePlayer.rotationYaw, forward, strafe)));
            if (forward == 0.0f && strafe == 0.0f) {
                return;
            }
            float closestForward = 0.0f;
            float closestStrafe = 0.0f;
            float closestDifference = Float.MAX_VALUE;
            for (float predictedForward = -1.0f; predictedForward <= 1.0f; predictedForward += 1.0f) {
                for (float predictedStrafe = -1.0f; predictedStrafe <= 1.0f; predictedStrafe += 1.0f) {
                    double predictedAngle;
                    double difference;
                    if (predictedStrafe == 0.0f && predictedForward == 0.0f || !((difference = Math.abs(angle - (predictedAngle = MathHelper.wrapAngleTo180_double(Math.toDegrees(RotationComponent.getDirection(yaw, predictedForward, predictedStrafe)))))) < (double)closestDifference)) continue;
                    closestDifference = (float)difference;
                    closestForward = predictedForward;
                    closestStrafe = predictedStrafe;
                }
            }
            event.setForward(closestForward);
            event.setStrafe(closestStrafe);
        }
    }

    public static double getDirection(float rotationYaw, double moveForward, double moveStrafing) {
        if (moveForward < 0.0) {
            rotationYaw += 180.0f;
        }
        float forward = 1.0f;
        if (moveForward < 0.0) {
            forward = -0.5f;
        } else if (moveForward > 0.0) {
            forward = 0.5f;
        }
        if (moveStrafing > 0.0) {
            rotationYaw -= 90.0f * forward;
        }
        if (moveStrafing < 0.0) {
            rotationYaw += 90.0f * forward;
        }
        return Math.toRadians(rotationYaw);
    }

    @EventTarget(value=100)
    public void onLook(EventLook event) {
        if (modify) {
            event.setRotation(rotation);
        }
    }

    @EventTarget(value=100)
    public void onStrafe(EventStrafe event) {
        if (modify && movementFix) {
            event.setYaw(rotation.getX());
        }
    }

    @EventTarget(value=100)
    public void onJump(EventJump event) {
        if (modify && movementFix) {
            event.setYaw(rotation.getX());
        }
    }

    @EventTarget(value=100)
    public void onUpdate(EventMotion event) {
        if (event.isPre()) {
            if (modify) {
                event.setYaw(rotation.getX());
                event.setPitch(rotation.getY());
                RotationComponent.mc.thePlayer.renderYawOffset = rotation.getX();
                RotationComponent.mc.thePlayer.rotationYawHead = rotation.getX();
                RotationComponent.mc.thePlayer.renderPitchHead = rotation.getY();
                lastServerRotation = new Vector2f(rotation.getX(), rotation.getY());
                if (Math.abs((rotation.getX() - RotationComponent.mc.thePlayer.rotationYaw) % 360.0f) < 1.0f && Math.abs(rotation.getY() - RotationComponent.mc.thePlayer.rotationPitch) < 1.0f) {
                    modify = false;
                    this.correctDisabledRotations();
                }
                lastRotation = rotation;
            } else {
                lastRotation = new Vector2f(RotationComponent.mc.thePlayer.rotationYaw, RotationComponent.mc.thePlayer.rotationPitch);
            }
            targetRotation = new Vector2f(RotationComponent.mc.thePlayer.rotationYaw, RotationComponent.mc.thePlayer.rotationPitch);
            smoothed = false;
        }
    }

    private void correctDisabledRotations() {
        Vector2f rotations = new Vector2f(RotationComponent.mc.thePlayer.rotationYaw, RotationComponent.mc.thePlayer.rotationPitch);
        Vector2f fixedRotations = this.resetRotation(this.applySensitivityPatch(rotations, lastRotation));
        RotationComponent.mc.thePlayer.rotationYaw = fixedRotations.getX();
        RotationComponent.mc.thePlayer.rotationPitch = fixedRotations.getY();
    }

    public Vector2f resetRotation(Vector2f rotation) {
        if (rotation == null) {
            return null;
        }
        float yaw = rotation.getX() + MathHelper.wrapAngleTo180_float(RotationComponent.mc.thePlayer.rotationYaw - rotation.getX());
        float pitch = RotationComponent.mc.thePlayer.rotationPitch;
        return new Vector2f(yaw, pitch);
    }

    public Vector2f applySensitivityPatch(Vector2f rotation, Vector2f previousRotation) {
        float mouseSensitivity = (float)((double)RotationComponent.mc.gameSettings.mouseSensitivity * (1.0 + Math.random() / 1.0E7) * (double)0.6f + (double)0.2f);
        double multiplier = (double)(mouseSensitivity * mouseSensitivity * mouseSensitivity * 8.0f) * 0.15;
        float yaw = previousRotation.getX() + (float)((double)Math.round((double)(rotation.getX() - previousRotation.getX()) / multiplier) * multiplier);
        float pitch = previousRotation.getY() + (float)((double)Math.round((double)(rotation.getY() - previousRotation.getY()) / multiplier) * multiplier);
        return new Vector2f(yaw, MathHelper.clamp_float(pitch, -90.0f, 90.0f));
    }

    private static void smoothRotation() {
        if (!smoothed) {
            float lastYaw = lastRotation.getX();
            float lastPitch = lastRotation.getY();
            float targetYaw = targetRotation.getX();
            float targetPitch = targetRotation.getY();
            rotation = RotationComponent.getSmoothRotation(new Vector2f(lastYaw, lastPitch), new Vector2f(targetYaw, targetPitch), (double)rotationSpeed + Math.random());
            if (movementFix) {
                RotationComponent.mc.thePlayer.movementYaw = rotation.getX();
            }
            RotationComponent.mc.thePlayer.velocityYaw = rotation.getX();
        }
        smoothed = true;
        RotationComponent.mc.entityRenderer.getMouseOver(1.0f);
    }

    public static Vector2f getSmoothRotation(Vector2f lastRotation, Vector2f targetRotation, double speed) {
        float yaw = targetRotation.getX();
        float pitch = targetRotation.getY();
        float lastYaw = lastRotation.getX();
        float lastPitch = lastRotation.getY();
        if (speed != 0.0) {
            float rotationSpeed = (float)speed;
            double deltaYaw = MathHelper.wrapAngleTo180_float(targetRotation.getX() - lastRotation.getX());
            double deltaPitch = pitch - lastPitch;
            double distance = Math.sqrt(deltaYaw * deltaYaw + deltaPitch * deltaPitch);
            double distributionYaw = Math.abs(deltaYaw / distance);
            double distributionPitch = Math.abs(deltaPitch / distance);
            double maxYaw = (double)rotationSpeed * distributionYaw;
            double maxPitch = (double)rotationSpeed * distributionPitch;
            float moveYaw = (float)Math.max(Math.min(deltaYaw, maxYaw), -maxYaw);
            float movePitch = (float)Math.max(Math.min(deltaPitch, maxPitch), -maxPitch);
            yaw = lastYaw + moveYaw;
            pitch = lastPitch + movePitch;
        }
        boolean randomise = Math.random() > 0.8;
        for (int i = 1; i <= (int)(2.0 + Math.random() * 2.0); ++i) {
            if (randomise) {
                yaw += (float)((Math.random() - 0.5) / 1.0E8);
                pitch -= (float)(Math.random() / 2.0E8);
            }
            Vector2f rotations = new Vector2f(yaw, pitch);
            Vector2f fixedRotations = RotationComponent.applySensitivityPatch(rotations);
            yaw = fixedRotations.getX();
            pitch = Math.max(-90.0f, Math.min(90.0f, fixedRotations.getY()));
        }
        return new Vector2f(yaw, pitch);
    }

    public static Vector2f applySensitivityPatch(Vector2f rotation) {
        Vector2f previousRotation = new Vector2f(RotationComponent.mc.thePlayer.lastReportedYaw, RotationComponent.mc.thePlayer.lastReportedPitch);
        float mouseSensitivity = (float)((double)RotationComponent.mc.gameSettings.mouseSensitivity * (1.0 + Math.random() / 1.0E7) * (double)0.6f + (double)0.2f);
        double multiplier = (double)(mouseSensitivity * mouseSensitivity * mouseSensitivity * 8.0f) * 0.15;
        float yaw = previousRotation.getX() + (float)((double)Math.round((double)(rotation.getX() - previousRotation.getX()) / multiplier) * multiplier);
        float pitch = previousRotation.getY() + (float)((double)Math.round((double)(rotation.getY() - previousRotation.getY()) / multiplier) * multiplier);
        return new Vector2f(yaw, MathHelper.clamp_float(pitch, -90.0f, 90.0f));
    }
}

