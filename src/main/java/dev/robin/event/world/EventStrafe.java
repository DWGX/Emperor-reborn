package dev.robin.event.world;

import dev.robin.Client;
import dev.robin.event.api.events.callables.EventCancellable;
import dev.robin.utils.player.MoveUtil;

public class EventStrafe
extends EventCancellable {
    public float strafe;
    public float forward;
    public float friction;
    public float yaw;

    public void setSpeed(double speed, double motionMultiplier) {
        this.setFriction((float)(this.getForward() != 0.0f && this.getStrafe() != 0.0f ? speed * (double)0.98f : speed));
        Client.mc.thePlayer.motionX *= motionMultiplier;
        Client.mc.thePlayer.motionZ *= motionMultiplier;
    }

    public void setSpeed(double speed) {
        this.setFriction((float)(this.getForward() != 0.0f && this.getStrafe() != 0.0f ? speed * (double)0.98f : speed));
        MoveUtil.stop();
    }

    public EventStrafe(float Strafe2, float Forward, float Friction, float Yaw) {
        this.strafe = Strafe2;
        this.forward = Forward;
        this.friction = Friction;
        this.yaw = Yaw;
    }

    public float getStrafe() {
        return this.strafe;
    }

    public void setStrafe(float strafe) {
        this.strafe = strafe;
    }

    public float getForward() {
        return this.forward;
    }

    public void setForward(float forward) {
        this.forward = forward;
    }

    public float getFriction() {
        return this.friction;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}

