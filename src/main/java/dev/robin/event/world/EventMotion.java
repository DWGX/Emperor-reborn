package dev.robin.event.world;

import dev.robin.event.api.events.Event;
import dev.robin.event.api.events.callables.EventTyped;
import net.minecraft.client.Minecraft;

public class EventMotion
implements Event {
    public static float RENDERPREVYAW;
    public static float RENDERYAW;
    public static float RENDERPREVYAWHEAD;
    public static float RENDERYAWHEAD;
    public static float RENDERPREVYAWBODY;
    public static float RENDERYAWBODY;
    public static float RENDERPREVPITCH;
    public static float RENDERPITCH;
    public final boolean PRE;
    public float YAW;
    public float YAWHEAD;
    public float YAWBODY;
    public float prevYaw;
    public float prevPitch;
    public float PITCH;
    public double X;
    public double Y;
    public double Z;
    public boolean GROUND;
    public EventTyped EventTyped;

    public EventMotion(float yaw, float pitch, float prevYaw, float prevPitch, double posX, double posY, double posZ, boolean ground) {
        this.prevYaw = prevYaw;
        this.prevPitch = prevPitch;
        this.YAW = yaw;
        this.PITCH = pitch;
        this.GROUND = ground;
        this.X = posX;
        this.Y = posY;
        this.Z = posZ;
        this.PRE = true;
    }

    public EventMotion(float yaw, float pitch) {
        RENDERPREVYAW = RENDERYAW;
        RENDERYAW = yaw;
        RENDERPREVYAWHEAD = RENDERYAWHEAD;
        RENDERYAWHEAD = yaw;
        RENDERPREVYAWBODY = RENDERYAWBODY;
        RENDERYAWBODY = yaw;
        RENDERPREVPITCH = RENDERPITCH;
        RENDERPITCH = pitch;
        this.PRE = false;
    }

    public float getPrevYaw() {
        return this.prevYaw;
    }

    public float getPrevPitch() {
        return this.prevPitch;
    }

    public static float getRenderYaw() {
        return RENDERYAW;
    }

    public static float getRenderPitch() {
        return RENDERPITCH;
    }

    public static float getPrevRenderYaw() {
        return RENDERPREVYAW;
    }

    public static float getPrevRenderPitch() {
        return RENDERPREVPITCH;
    }

    public boolean isPre() {
        return this.PRE;
    }

    public boolean isPost() {
        return !this.PRE;
    }

    public double getX() {
        return this.X;
    }

    public void setX(double posX) {
        this.X = posX;
    }

    public double getY() {
        return this.Y;
    }

    public void setY(double posY) {
        this.Y = posY;
    }

    public double getZ() {
        return this.Z;
    }

    public void setZ(double posZ) {
        this.Z = posZ;
    }

    public float getYaw() {
        return this.YAW;
    }

    public void setYaw(float yaw) {
        this.YAW = yaw;
        Minecraft.getMinecraft().thePlayer.prevRenderYawOffset = RENDERPREVYAW;
        Minecraft.getMinecraft().thePlayer.renderYawOffset = RENDERYAW;
        Minecraft.getMinecraft().thePlayer.prevRotationYawHead = RENDERPREVYAW;
        Minecraft.getMinecraft().thePlayer.rotationYawHead = RENDERYAW;
    }

    public void setYawHead(float yaw) {
        this.YAWHEAD = yaw;
        Minecraft.getMinecraft().thePlayer.prevRotationYawHead = RENDERPREVYAW;
        Minecraft.getMinecraft().thePlayer.rotationYawHead = RENDERYAW;
    }

    public void setYawOffset(float yaw) {
        this.YAWBODY = yaw;
        Minecraft.getMinecraft().thePlayer.prevRenderYawOffset = RENDERPREVYAW;
        Minecraft.getMinecraft().thePlayer.renderYawOffset = RENDERYAW;
    }

    public float getPitch() {
        return this.PITCH;
    }

    public void setPitch(float pitch) {
        this.PITCH = pitch;
    }

    public boolean isOnGround() {
        return this.GROUND;
    }

    public void setOnGround(boolean ground) {
        this.GROUND = ground;
    }

    public int getType() {
        return 1;
    }
}

