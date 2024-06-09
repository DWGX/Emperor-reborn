package dev.robin.module.modules.movement;

import dev.robin.event.EventTarget;
import dev.robin.event.world.EventMotion;
import dev.robin.event.world.EventWorldLoad;
import dev.robin.module.Category;
import dev.robin.module.Module;
import dev.robin.module.modules.combat.KillAura;
import dev.robin.module.values.ModeValue;
import dev.robin.utils.client.PacketUtil;
import dev.robin.utils.player.BlockUtil;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;

public class NoLiquid
extends Module {
    private final ModeValue<noWaterMode> modeValue = new ModeValue("Mode", (Enum[])noWaterMode.values(), (Enum)noWaterMode.Vanilla);
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
        this.setSuffix(((noWaterMode)((Object)this.modeValue.getValue())).name());
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
            if (!((noWaterMode)((Object)this.modeValue.getValue())).equals((Object)noWaterMode.Grim) || !shouldCancelWater) continue;
            PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, block.getKey(), NoLiquid.mc.objectMouseOver.sideHit));
        }
    }

    public static enum noWaterMode {
        Vanilla,
        Grim;

    }
}

