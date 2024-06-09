package dev.robin.module.modules.movement;

import dev.robin.event.EventTarget;
import dev.robin.event.world.EventMotion;
import dev.robin.module.Category;
import dev.robin.module.Module;
import dev.robin.module.modules.combat.KillAura;
import dev.robin.module.values.ModeValue;
import dev.robin.utils.client.PacketUtil;
import dev.robin.utils.player.BlockUtil;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWeb;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;

public class NoWeb
extends Module {
    private final ModeValue<noWebMode> modeValue = new ModeValue("Mode", (Enum[])noWebMode.values(), (Enum)noWebMode.Grim);

    public NoWeb() {
        super("NoWeb", Category.Movement);
    }

    @Override
    public void onDisable() {
        NoWeb.mc.timer.timerSpeed = 1.0f;
    }

    @EventTarget
    private void onUpdate(EventMotion e) {
        if (KillAura.target != null) {
            return;
        }
        if (e.isPost()) {
            return;
        }
        this.setSuffix(((noWebMode)((Object)this.modeValue.getValue())).name());
        if (!NoWeb.mc.thePlayer.isInWeb) {
            return;
        }
        switch ((noWebMode)((Object)this.modeValue.getValue())) {
            case Vanilla: {
                NoWeb.mc.thePlayer.isInWeb = false;
                break;
            }
            case Grim: {
                if (KillAura.target != null) {
                    return;
                }
                Map<BlockPos, Block> searchBlock = BlockUtil.searchBlocks(2);
                for (Map.Entry<BlockPos, Block> block : searchBlock.entrySet()) {
                    if (!(NoWeb.mc.theWorld.getBlockState(block.getKey()).getBlock() instanceof BlockWeb)) continue;
                    PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, block.getKey(), NoWeb.mc.objectMouseOver.sideHit));
                    PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, block.getKey(), NoWeb.mc.objectMouseOver.sideHit));
                }
                NoWeb.mc.thePlayer.isInWeb = false;
                break;
            }
            case AAC: {
                NoWeb.mc.thePlayer.jumpMovementFactor = 0.59f;
                if (NoWeb.mc.gameSettings.keyBindSneak.isKeyDown()) break;
                NoWeb.mc.thePlayer.motionY = 0.0;
                break;
            }
            case LowAAC: {
                float f = NoWeb.mc.thePlayer.jumpMovementFactor = NoWeb.mc.thePlayer.movementInput.moveStrafe != 0.0f ? 1.0f : 1.21f;
                if (!NoWeb.mc.gameSettings.keyBindSneak.isKeyDown()) {
                    NoWeb.mc.thePlayer.motionY = 0.0;
                }
                if (!NoWeb.mc.thePlayer.onGround) break;
                NoWeb.mc.thePlayer.jump();
                break;
            }
            case Rewind: {
                NoWeb.mc.thePlayer.jumpMovementFactor = 0.42f;
                if (!NoWeb.mc.thePlayer.onGround) break;
                NoWeb.mc.thePlayer.jump();
            }
        }
    }

    private static enum noWebMode {
        Vanilla,
        Grim,
        AAC,
        LowAAC,
        Rewind;

    }
}

