package dev.emperor.module.modules.player;

import dev.emperor.event.EventTarget;
import dev.emperor.event.world.EventPacketSend;
import dev.emperor.event.world.EventUpdate;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.values.BoolValue;
import dev.emperor.module.values.NumberValue;
import dev.emperor.utils.client.PacketUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class SpeedMine
extends Module {
    private final NumberValue speed = new NumberValue("Speed", 1.1, 1.0, 3.0, 0.1);
    private final BoolValue abortPacketSpoof = new BoolValue("AbortPacketSpoof", true);
    private final BoolValue speedCheckBypass = new BoolValue("VanillaCheckBypass", true);
    private EnumFacing facing;
    private BlockPos pos;
    private boolean boost = false;
    private float damage = 0.0f;

    public SpeedMine() {
        super("SpeedMine", Category.Player);
    }

    @Override
    public void onDisable() {
        if (SpeedMine.mc.thePlayer == null) {
            return;
        }
        if (this.speedCheckBypass.getValue()) {
            SpeedMine.mc.thePlayer.removePotionEffect(Potion.digSpeed.id);
        }
    }

    @EventTarget
    private void onPacket(EventPacketSend e) {
        if (e.packet instanceof C07PacketPlayerDigging) {
            if (((C07PacketPlayerDigging)e.getPacket()).getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                this.boost = true;
                this.pos = ((C07PacketPlayerDigging)e.getPacket()).getPosition();
                this.facing = ((C07PacketPlayerDigging)e.getPacket()).getFacing();
                this.damage = 0.0f;
            } else if (((C07PacketPlayerDigging)e.getPacket()).getStatus() == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK || ((C07PacketPlayerDigging)e.getPacket()).getStatus() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                this.boost = false;
                this.pos = null;
                this.facing = null;
            }
        }
    }

    @EventTarget
    private void onUpdate(EventUpdate e) {
        if (this.speedCheckBypass.getValue()) {
            SpeedMine.mc.thePlayer.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 89640, 2));
        }
        if (SpeedMine.mc.playerController.extendedReach()) {
            SpeedMine.mc.playerController.blockHitDelay = 0;
        } else if (this.pos != null && this.boost) {
            IBlockState blockState = SpeedMine.mc.theWorld.getBlockState(this.pos);
            this.damage = (float)((double)this.damage + (double)blockState.getBlock().getPlayerRelativeBlockHardness(SpeedMine.mc.thePlayer, SpeedMine.mc.theWorld, this.pos) * this.speed.getValue());
            if (this.damage >= 1.0f) {
                SpeedMine.mc.theWorld.setBlockState(this.pos, Blocks.air.getDefaultState(), 11);
                if (this.abortPacketSpoof.getValue()) {
                    PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.pos, this.facing));
                }
                PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.pos, this.facing));
                this.damage = 0.0f;
                this.boost = false;
            }
        }
    }
}

