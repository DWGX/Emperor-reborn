package dev.emperor.utils.player;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public class EnumFacingOffset {
    public EnumFacing enumFacing;
    private final Vec3 offset;

    public EnumFacingOffset(EnumFacing enumFacing, Vec3 offset) {
        this.enumFacing = enumFacing;
        this.offset = offset;
    }

    public EnumFacing getEnumFacing() {
        return this.enumFacing;
    }

    public Vec3 getOffset() {
        return this.offset;
    }
}

