package dev.robin.module.modules.movement;

import dev.robin.event.EventTarget;
import dev.robin.event.world.EventMotion;
import dev.robin.module.Category;
import dev.robin.module.Module;
import dev.robin.module.values.NumberValue;
import dev.robin.utils.player.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockVine;
import net.minecraft.util.BlockPos;

public class FastLadder
extends Module {
    private final NumberValue yMotionValue = new NumberValue("YMotion", 0.15, 0.1, 0.2, 0.01);

    public FastLadder() {
        super("FastLadder", Category.Movement);
    }

    @EventTarget
    public void onUpdate(EventMotion event) {
        if (event.isPost()) {
            return;
        }
        Block block = BlockUtil.getBlock(new BlockPos(FastLadder.mc.thePlayer.posX, FastLadder.mc.thePlayer.posY + 1.0, FastLadder.mc.thePlayer.posZ));
        if (block instanceof BlockLadder && FastLadder.mc.thePlayer.isCollidedHorizontally || block instanceof BlockVine || BlockUtil.getBlock(new BlockPos(FastLadder.mc.thePlayer.posX, FastLadder.mc.thePlayer.posY, FastLadder.mc.thePlayer.posZ)) instanceof BlockVine) {
            FastLadder.mc.thePlayer.motionY = (Double)this.yMotionValue.getValue();
            FastLadder.mc.thePlayer.motionX = 0.0;
            FastLadder.mc.thePlayer.motionZ = 0.0;
        }
    }
}

