package dev.robin.utils;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class BlockCache {
    private final BlockPos position;
    private final EnumFacing facing;

    public BlockCache(BlockPos position, EnumFacing facing) {
        this.position = position;
        this.facing = facing;
    }

    public BlockPos getPosition() {
        return this.position;
    }

    public EnumFacing getFacing() {
        return this.facing;
    }
}

