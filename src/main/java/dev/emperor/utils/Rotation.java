package dev.emperor.utils;

import dev.emperor.Client;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.compatibility.util.vector.Vector2f;

public class Rotation {
    float yaw;
    float pitch;
    public double distanceSq;

    public Rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Vector2f toVec2f() {
        return new Vector2f(this.yaw, this.pitch);
    }

    public void toPlayer(EntityPlayer player) {
        if (Float.isNaN(this.yaw) || Float.isNaN(this.pitch)) {
            return;
        }
        this.fixedSensitivity(Float.valueOf(Client.mc.gameSettings.mouseSensitivity));
        player.rotationYaw = this.yaw;
        player.rotationPitch = this.pitch;
    }

    public void fixedSensitivity(Float sensitivity) {
        float f = sensitivity.floatValue() * 0.6f + 0.2f;
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
        float yaw = Rotation.updateRotation(currentYaw, calcYaw, yawSpeed + RandomUtils.nextFloat((float)0.0f, (float)15.0f));
        double diffYaw = MathHelper.wrapAngleTo180_float(calcYaw - currentYaw);
        if ((double)(-yawSpeed) > diffYaw || diffYaw > (double)yawSpeed) {
            yaw += (float)((double)RandomUtils.nextFloat((float)1.0f, (float)2.0f) * Math.sin((double)Client.mc.thePlayer.rotationPitch * Math.PI));
        }
        if (yaw == currentYaw) {
            return currentYaw;
        }
        if ((double)Client.mc.gameSettings.mouseSensitivity == 0.5) {
            Client.mc.gameSettings.mouseSensitivity = 0.47887325f;
        }
        float f1 = Client.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        float f2 = f1 * f1 * f1 * 8.0f;
        int deltaX = (int)((6.667 * (double)yaw - 6.666666666666667 * (double)currentYaw) / (double)f2);
        float f3 = (float)deltaX * f2;
        yaw = (float)((double)currentYaw + (double)f3 * 0.15);
        return yaw;
    }

    public float rotateToYaw(float yawSpeed, float[] currentRots, float calcYaw) {
        float yaw = Rotation.updateRotation(currentRots[0], calcYaw, yawSpeed + RandomUtils.nextFloat((float)0.0f, (float)15.0f));
        if (yaw != calcYaw) {
            yaw += (float)((double)RandomUtils.nextFloat((float)1.0f, (float)2.0f) * Math.sin((double)currentRots[1] * Math.PI));
        }
        if (yaw == currentRots[0]) {
            return currentRots[0];
        }
        yaw += (float)(ThreadLocalRandom.current().nextGaussian() * 0.2);
        if ((double)Client.mc.gameSettings.mouseSensitivity == 0.5) {
            Client.mc.gameSettings.mouseSensitivity = 0.47887325f;
        }
        float f1 = Client.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        float f2 = f1 * f1 * f1 * 8.0f;
        int deltaX = (int)((6.667 * (double)yaw - 6.6666667 * (double)currentRots[0]) / (double)f2);
        float f3 = (float)deltaX * f2;
        yaw = (float)((double)currentRots[0] + (double)f3 * 0.15);
        return yaw;
    }

    public float rotateToPitch(float pitchSpeed, float currentPitch, float calcPitch) {
        float pitch = Rotation.updateRotation(currentPitch, calcPitch, pitchSpeed + RandomUtils.nextFloat((float)0.0f, (float)15.0f));
        if (pitch != calcPitch) {
            pitch += (float)((double)RandomUtils.nextFloat((float)1.0f, (float)2.0f) * Math.sin((double)Client.mc.thePlayer.rotationYaw * Math.PI));
        }
        if ((double)Client.mc.gameSettings.mouseSensitivity == 0.5) {
            Client.mc.gameSettings.mouseSensitivity = 0.47887325f;
        }
        float f1 = Client.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        float f2 = f1 * f1 * f1 * 8.0f;
        int deltaY = (int)((6.667 * (double)pitch - 6.666667 * (double)currentPitch) / (double)f2) * -1;
        float f3 = (float)deltaY * f2;
        float f4 = (float)((double)currentPitch - (double)f3 * 0.15);
        pitch = MathHelper.clamp_float(f4, -90.0f, 90.0f);
        return pitch;
    }

    public float rotateToPitch(float pitchSpeed, float[] currentRots, float calcPitch) {
        float pitch = Rotation.updateRotation(currentRots[1], calcPitch, pitchSpeed + RandomUtils.nextFloat((float)0.0f, (float)15.0f));
        if (pitch != calcPitch) {
            pitch += (float)((double)RandomUtils.nextFloat((float)1.0f, (float)2.0f) * Math.sin((double)currentRots[0] * Math.PI));
        }
        if ((double)Client.mc.gameSettings.mouseSensitivity == 0.5) {
            Client.mc.gameSettings.mouseSensitivity = 0.47887325f;
        }
        float f1 = Client.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        float f2 = f1 * f1 * f1 * 8.0f;
        int deltaY = (int)((6.667 * (double)pitch - 6.666667 * (double)currentRots[1]) / (double)f2) * -1;
        float f3 = (float)deltaY * f2;
        float f4 = (float)((double)currentRots[1] - (double)f3 * 0.15);
        pitch = MathHelper.clamp_float(f4, -90.0f, 90.0f);
        return pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setDistanceSq(double distanceSq) {
        this.distanceSq = distanceSq;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public double getDistanceSq() {
        return this.distanceSq;
    }
}

