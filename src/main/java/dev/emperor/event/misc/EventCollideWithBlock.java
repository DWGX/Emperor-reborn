package dev.emperor.event.misc;

import dev.emperor.event.api.events.Event;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class EventCollideWithBlock
implements Event {
    public AxisAlignedBB boundingBox;
    private Block block;
    private BlockPos blockPos;
    private final int x;
    private final int y;
    private final int z;

    public EventCollideWithBlock(Block block, BlockPos pos, AxisAlignedBB boundingBox) {
        this.block = block;
        this.blockPos = pos;
        this.boundingBox = boundingBox;
        this.x = this.blockPos.getX();
        this.y = this.blockPos.getY();
        this.z = this.blockPos.getZ();
    }

    public Block getBlock() {
        return this.block;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public BlockPos getPos() {
        return this.blockPos;
    }

    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }

    public void setBoundingBox(AxisAlignedBB boundingBox) {
        this.boundingBox = boundingBox;
    }

    public void setBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }
}

