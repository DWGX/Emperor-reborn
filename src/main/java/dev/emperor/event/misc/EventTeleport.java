package dev.emperor.event.misc;

import dev.emperor.event.api.events.callables.EventCancellable;
import net.minecraft.network.play.client.C03PacketPlayer;

public final class EventTeleport
extends EventCancellable {
    private C03PacketPlayer.C06PacketPlayerPosLook response;
    private double posX;
    private double posY;
    private double posZ;
    private float yaw;
    private float pitch;

    public EventTeleport(C03PacketPlayer.C06PacketPlayerPosLook response, double posX, double posY, double posZ, float yaw, float pitch) {
        this.response = response;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public C03PacketPlayer.C06PacketPlayerPosLook getResponse() {
        return this.response;
    }

    public void setResponse(C03PacketPlayer.C06PacketPlayerPosLook response) {
        this.response = response;
    }

    public double getPosX() {
        return this.posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return this.posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public double getPosZ() {
        return this.posZ;
    }

    public void setPosZ(double posZ) {
        this.posZ = posZ;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}

