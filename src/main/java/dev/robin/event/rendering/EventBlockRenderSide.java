package dev.robin.event.rendering;

import dev.robin.event.api.events.Event;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public class EventBlockRenderSide
implements Event {
    public final BlockPos pos;
    private final IBlockAccess world;
    private final EnumFacing side;
    public double maxX;
    public double maxY;
    public double maxZ;
    public double minX;
    public double minY;
    public double minZ;
    private boolean toRender;

    public EventBlockRenderSide(IBlockAccess world, BlockPos pos, EnumFacing side, double maxX, double minX, double maxY, double minY, double maxZ, double minZ) {
        this.world = world;
        this.pos = pos;
        this.side = side;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
    }

    public IBlockAccess getWorld() {
        return this.world;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public EnumFacing getSide() {
        return this.side;
    }

    public boolean isToRender() {
        return this.toRender;
    }

    public void setToRender(boolean toRender) {
        this.toRender = toRender;
    }
}

