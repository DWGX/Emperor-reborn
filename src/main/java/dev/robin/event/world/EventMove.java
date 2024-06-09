package dev.robin.event.world;

import dev.robin.event.api.events.callables.EventCancellable;
import net.minecraft.client.Minecraft;

public class EventMove
extends EventCancellable {
    public double x;
    public double y;
    public double z;
    private final double motionX;
    private final double motionY;
    private final double motionZ;

    public EventMove(double x2, double y2, double z) {
        this.x = x2;
        this.y = y2;
        this.z = z;
        this.motionX = x2;
        this.motionY = y2;
        this.motionZ = z;
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x2) {
        this.x = x2;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y2) {
        this.y = y2;
    }

    public double getZ() {
        return this.z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setMoveSpeed(double speed) {
        double forward = Minecraft.getMinecraft().thePlayer.movementInput.moveForward;
        double strafe = Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe;
        float yaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            this.setX(0.0);
            this.setZ(0.0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (float)(forward > 0.0 ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += (float)(forward > 0.0 ? 45 : -45);
                }
                strafe = 0.0;
                forward = forward > 0.0 ? 1.0 : -1.0;
            }
            double sin = Math.sin(Math.toRadians(yaw + 90.0f));
            double cos = Math.cos(Math.toRadians(yaw + 90.0f));
            this.setX(forward * speed * cos + strafe * speed * sin);
            this.setZ(forward * speed * sin - strafe * speed * cos);
        }
    }
}

