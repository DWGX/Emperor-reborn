package dev.emperor.module.modules.movement;

import dev.emperor.event.EventTarget;
import dev.emperor.event.misc.EventCollideWithBlock;
import dev.emperor.event.world.EventJump;
import dev.emperor.event.world.EventMotion;
import dev.emperor.event.world.EventMove;
import dev.emperor.event.world.EventPacketSend;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.values.ModeValue;
import dev.emperor.module.values.NumberValue;
import dev.emperor.utils.player.BlockUtil;
import dev.emperor.utils.player.MoveUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;

public class WallClimb
extends Module {
    private final ModeValue<WallClimbModes> modeValue = new ModeValue("Mode", WallClimbModes.values(), WallClimbModes.Simple);
    private final ModeValue<ClipMode> clipMode = new ModeValue("ClipMode", ClipMode.values(), ClipMode.Fast, () -> this.modeValue.get().equals(WallClimbModes.Clip));
    private final NumberValue checkerClimbMotionValue = new NumberValue("CheckerClimbMotion", 0.0, 0.0, 1.0, 0.1, () -> this.modeValue.get().equals(WallClimbModes.CheckerClimb));
    private final NumberValue verusClimbSpeed = new NumberValue("VerusClimbSpeed", 0.0, 0.0, 1.0, 0.1, () -> this.modeValue.get().equals(WallClimbModes.Verus));
    private boolean glitch;
    private boolean canClimb;
    private int waited;

    public WallClimb() {
        super("WallClimb", Category.Movement);
    }

    @Override
    public void onEnable() {
        this.glitch = false;
        this.canClimb = false;
        this.waited = 0;
    }

    @EventTarget
    public void onMove(EventMove event) {
        if (!WallClimb.mc.thePlayer.isCollidedHorizontally || WallClimb.mc.thePlayer.isOnLadder() || WallClimb.mc.thePlayer.isInWater() || WallClimb.mc.thePlayer.isInLava()) {
            return;
        }
        if (this.modeValue.getValue().equals(WallClimbModes.Simple)) {
            event.setY(0.2);
            WallClimb.mc.thePlayer.motionY = 0.0;
        }
    }

    @EventTarget
    public void onJump(EventJump event) {
        if (this.modeValue.getValue().equals(WallClimbModes.Verus) && this.canClimb) {
            event.setCancelled();
        }
    }

    @EventTarget
    public void onUpdate(EventMotion event) {
        if (event.isPost()) {
            return;
        }
        switch (this.modeValue.getValue()) {
            case Clip: {
                if (WallClimb.mc.thePlayer.motionY < 0.0) {
                    this.glitch = true;
                }
                if (!WallClimb.mc.thePlayer.isCollidedHorizontally) break;
                switch (this.clipMode.get()) {
                    case Jump: {
                        if (!WallClimb.mc.thePlayer.onGround) break;
                        WallClimb.mc.thePlayer.jump();
                        break;
                    }
                    case Fast: {
                        if (WallClimb.mc.thePlayer.onGround) {
                            WallClimb.mc.thePlayer.motionY = 0.42;
                            break;
                        }
                        if (!(WallClimb.mc.thePlayer.motionY < 0.0)) break;
                        WallClimb.mc.thePlayer.motionY = -0.3;
                    }
                }
                break;
            }
            case CheckerClimb: {
                boolean isInsideBlock = BlockUtil.collideBlockIntersects(WallClimb.mc.thePlayer.getEntityBoundingBox(), block -> !(block instanceof BlockAir));
                float motion = this.checkerClimbMotionValue.getValue().floatValue();
                if (!isInsideBlock || motion == 0.0f) break;
                WallClimb.mc.thePlayer.motionY = motion;
                break;
            }
            case AAC_3_3_12: {
                if (WallClimb.mc.thePlayer.isCollidedHorizontally && !WallClimb.mc.thePlayer.isOnLadder()) {
                    ++this.waited;
                    if (this.waited == 1) {
                        WallClimb.mc.thePlayer.motionY = 0.43;
                    }
                    if (this.waited == 12) {
                        WallClimb.mc.thePlayer.motionY = 0.43;
                    }
                    if (this.waited == 23) {
                        WallClimb.mc.thePlayer.motionY = 0.43;
                    }
                    if (this.waited == 29) {
                        WallClimb.mc.thePlayer.setPosition(WallClimb.mc.thePlayer.posX, WallClimb.mc.thePlayer.posY + 0.5, WallClimb.mc.thePlayer.posZ);
                    }
                    if (this.waited < 30) break;
                    this.waited = 0;
                    break;
                }
                if (!WallClimb.mc.thePlayer.onGround) break;
                this.waited = 0;
                break;
            }
            case AACGlide: {
                if (!WallClimb.mc.thePlayer.isCollidedHorizontally || WallClimb.mc.thePlayer.isOnLadder()) {
                    return;
                }
                WallClimb.mc.thePlayer.motionY = -0.189;
                break;
            }
            case Verus: {
                if (!WallClimb.mc.thePlayer.isCollidedHorizontally || WallClimb.mc.thePlayer.isInWater() || WallClimb.mc.thePlayer.isInLava() || WallClimb.mc.thePlayer.isOnLadder() || WallClimb.mc.thePlayer.isInWeb || WallClimb.mc.thePlayer.isOnLadder()) {
                    this.canClimb = false;
                    break;
                }
                this.canClimb = true;
                WallClimb.mc.thePlayer.motionY = this.verusClimbSpeed.getValue();
                WallClimb.mc.thePlayer.onGround = true;
            }
        }
    }

    @EventTarget
    public void onPacket(EventPacketSend event) {
        Packet packet = event.getPacket();
        if (packet instanceof C03PacketPlayer packetPlayer) {
            if (this.glitch) {
                float yaw = (float)MoveUtil.getDirection();
                packetPlayer.x -= (double)MathHelper.sin(yaw) * 1.0E-8;
                packetPlayer.z += (double)MathHelper.cos(yaw) * 1.0E-8;
                this.glitch = false;
            }
            if (this.canClimb) {
                packetPlayer.onGround = true;
            }
        }
    }

    @EventTarget
    public void onBlockBB(EventCollideWithBlock event) {
        if (WallClimb.mc.thePlayer == null) {
            return;
        }
        switch (this.modeValue.getValue()) {
            case CheckerClimb: {
                if (!((double)event.getY() > WallClimb.mc.thePlayer.posY)) break;
                event.setBoundingBox(null);
                break;
            }
            case Clip: {
                if (event.getBlock() == null || WallClimb.mc.thePlayer == null || !(event.getBlock() instanceof BlockAir) || !((double)event.getY() < WallClimb.mc.thePlayer.posY) || !WallClimb.mc.thePlayer.isCollidedHorizontally || WallClimb.mc.thePlayer.isOnLadder() || WallClimb.mc.thePlayer.isInWater() || WallClimb.mc.thePlayer.isInLava()) break;
                event.setBoundingBox(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).offset(WallClimb.mc.thePlayer.posX, (int)WallClimb.mc.thePlayer.posY - 1, WallClimb.mc.thePlayer.posZ));
            }
        }
    }

    public enum ClipMode {
        Jump,
        Fast

    }

    public enum WallClimbModes {
        Simple,
        CheckerClimb,
        Clip,
        AAC_3_3_12,
        AACGlide,
        Verus

    }
}

