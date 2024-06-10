package dev.emperor.module.modules.movement;

import dev.emperor.event.EventTarget;
import dev.emperor.event.world.EventMotion;
import dev.emperor.event.world.EventWorldLoad;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.modules.combat.KillAura;
import dev.emperor.module.values.ModeValue;
import dev.emperor.utils.client.PacketUtil;
import dev.emperor.utils.player.BlockUtil;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;

public class NoLiquid
extends Module {
    private final ModeValue<noWaterMode> modeValue = new ModeValue("Mode", noWaterMode.values(), noWaterMode.Vanilla);
    public static boolean shouldCancelWater;

    public NoLiquid() {
        super("NoLiquid", Category.Movement);
    }

    @Override
    public void onDisable() {
        shouldCancelWater = false;
    }

    @EventTarget
    public void onWorldLoad(EventWorldLoad e) {
        shouldCancelWater = false;
    }

    @EventTarget
    public void onUpdate(EventMotion e) {
        this.setSuffix(this.modeValue.getValue().name());
        if (KillAura.target != null) {
            return;
        }
        if (NoLiquid.mc.thePlayer == null) {
            return;
        }
        if (e.isPost()) {
            return;
        }
        shouldCancelWater = false;
        Map<BlockPos, Block> searchBlock = BlockUtil.searchBlocks(2);
        for (Map.Entry<BlockPos, Block> block : searchBlock.entrySet()) {
            boolean checkBlock = NoLiquid.mc.theWorld.getBlockState(block.getKey()).getBlock() == Blocks.water || NoLiquid.mc.theWorld.getBlockState(block.getKey()).getBlock() == Blocks.flowing_water || NoLiquid.mc.theWorld.getBlockState(block.getKey()).getBlock() == Blocks.lava || NoLiquid.mc.theWorld.getBlockState(block.getKey()).getBlock() == Blocks.flowing_lava;
            if (!checkBlock) continue;
            shouldCancelWater = true;
            if (!this.modeValue.getValue().equals(noWaterMode.Grim) || !shouldCancelWater) continue;
            PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, block.getKey(), NoLiquid.mc.objectMouseOver.sideHit));
        }
    }

    public enum noWaterMode {
        Vanilla,
        Grim

    }
}

