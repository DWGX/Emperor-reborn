package dev.robin.utils;

public class FixedRotations {
    private float yaw;
    private float pitch;
    private float lastYaw;
    private float lastPitch;

    public FixedRotations(float startingYaw, float startingPitch) {
        this.lastYaw = this.yaw = startingYaw;
        this.lastPitch = this.pitch = startingPitch;
    }

    public void updateRotations(float requestedYaw, float requestedPitch, boolean sprint) {
        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;
        this.yaw = requestedYaw;
        this.pitch = Math.max(-90.0f, sprint ? this.pitch : Math.min(90.0f, this.pitch));
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getLastYaw() {
        return this.lastYaw;
    }

    public float getLastPitch() {
        return this.lastPitch;
    }
}

