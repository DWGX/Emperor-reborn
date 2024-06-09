package dev.robin.utils.player;

import net.minecraft.util.Vec3;
import org.lwjgl.compatibility.util.vector.Vector2f;

class VecRotation {
    final Vec3 vec3;
    final Vector2f rotation;

    public VecRotation(Vec3 Vec32, Vector2f Rotation2) {
        this.vec3 = Vec32;
        this.rotation = Rotation2;
    }

    public Vec3 getVec3() {
        return this.vec3;
    }

    public Vector2f getRotation() {
        return this.rotation;
    }

    public Vec3 getVec() {
        return this.vec3;
    }
}

