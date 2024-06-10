package dev.emperor.module.modules.combat;

import dev.emperor.Client;
import dev.emperor.event.EventTarget;
import dev.emperor.event.attack.EventAttack;
import dev.emperor.event.world.EventPacketReceive;
import dev.emperor.event.world.EventPacketSend;
import dev.emperor.event.world.EventTick;
import dev.emperor.event.world.EventUpdate;
import dev.emperor.event.world.EventWorldLoad;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.modules.combat.velocity.AACVelocity;
import dev.emperor.module.modules.combat.velocity.GrimVelocity;
import dev.emperor.module.modules.combat.velocity.VelocityMode;
import dev.emperor.module.modules.player.Blink;
import dev.emperor.module.values.BoolValue;
import dev.emperor.module.values.ModeValue;
import dev.emperor.module.values.NumberValue;
import dev.emperor.utils.DebugUtil;
import dev.emperor.utils.player.MoveUtil;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.world.WorldSettings;
import net.vialoadingbase.ViaLoadingBase;

public class Velocity
extends Module {
    public static final ModeValue<velMode> modes = new ModeValue("Mode", velMode.values(), velMode.Cancel);
    public static final ModeValue<GrimVelocity.velMode> grimModes = new ModeValue("GrimMode", GrimVelocity.velMode.values(), GrimVelocity.velMode.Vertical, () -> modes.getValue().equals(velMode.Grim));
    public static final BoolValue grimRayCastValue = new BoolValue("Grim-RayCast", false, () -> ViaLoadingBase.getInstance().getTargetVersion().getVersion() > 47 && grimModes.getValue().equals(GrimVelocity.velMode.Vertical));
    public static final ModeValue<AACVelocity.velMode> aacModes = new ModeValue("AACMode", AACVelocity.velMode.values(), AACVelocity.velMode.AAC5, () -> modes.getValue().equals(velMode.AAC));
    public BoolValue OnlyMove = new BoolValue("OnlyMove", false);
    public BoolValue OnlyGround = new BoolValue("OnlyGround", false);
    private final BoolValue BlinkCheck = new BoolValue("BlinkCheck", false);
    private final BoolValue WaterCheckValue = new BoolValue("WaterCheck", false);
    private final BoolValue S08FlagCheckValue = new BoolValue("S08FlagCheck", false);
    public NumberValue S08FlagTickValue = new NumberValue("S08FlagTicks", 6.0, 0.0, 30.0, 1.0);
    public BoolValue debugMessageValue = new BoolValue("S08DebugMessage", false);
    int flags;

    public Velocity() {
        super("Velocity", Category.Combat);
        VelocityMode.init();
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (mc.getNetHandler() == null) {
            return;
        }
        if (Velocity.mc.theWorld == null) {
            return;
        }
        if (Velocity.mc.thePlayer == null) {
            return;
        }
        if (this.S08FlagCheckValue.getValue() && this.flags > 0) {
            --this.flags;
        }
        VelocityMode vel = VelocityMode.get(modes.getValue().name());
        this.setSuffix(vel.getTag());
        vel.onUpdate(event);
    }

    @EventTarget
    public void onPacketReceive(EventPacketReceive e) {
        if (Velocity.mc.thePlayer == null) {
            return;
        }
        Packet<?> packet = e.getPacket();
        if (this.OnlyGround.getValue() && !Velocity.mc.thePlayer.onGround || this.OnlyMove.getValue() && !MoveUtil.isMoving() || this.flags != 0) {
            return;
        }
        if (Velocity.mc.thePlayer.isDead) {
            return;
        }
        if (Velocity.mc.currentScreen instanceof GuiGameOver) {
            return;
        }
        if (Velocity.mc.playerController.getCurrentGameType() == WorldSettings.GameType.SPECTATOR) {
            return;
        }
        if (Velocity.mc.thePlayer.isInWater() && this.WaterCheckValue.getValue()) {
            return;
        }
        if (Velocity.mc.thePlayer.isOnLadder()) {
            return;
        }
        if (Client.instance.moduleManager.getModule(Blink.class).getState() && this.BlinkCheck.getValue()) {
            return;
        }
        if (packet instanceof S08PacketPlayerPosLook && this.S08FlagCheckValue.getValue()) {
            this.flags = this.S08FlagTickValue.getValue().intValue();
            if (this.debugMessageValue.getValue()) {
                DebugUtil.log(true, "VelocityDebug S08 Flags");
            }
        }
        VelocityMode vel = VelocityMode.get(modes.getValue().name());
        vel.onPacketReceive(e);
    }

    @EventTarget
    public void onAttack(EventAttack e) {
        VelocityMode vel = VelocityMode.get(modes.getValue().name());
        vel.onAttack(e);
    }

    @EventTarget
    public void onTick(EventTick e) {
        VelocityMode vel = VelocityMode.get(modes.getValue().name());
        vel.onTick(e);
    }

    @EventTarget
    public void onWorldLoad(EventWorldLoad e) {
        VelocityMode vel = VelocityMode.get(modes.getValue().name());
        vel.onWorldLoad(e);
    }

    @EventTarget
    public void onPacketSend(EventPacketSend e) {
        if (Velocity.mc.thePlayer == null) {
            return;
        }
        if (this.OnlyGround.getValue() && !Velocity.mc.thePlayer.onGround || this.OnlyMove.getValue() && !MoveUtil.isMoving() || this.flags != 0) {
            return;
        }
        if (Velocity.mc.thePlayer.isDead) {
            return;
        }
        if (Velocity.mc.currentScreen instanceof GuiGameOver) {
            return;
        }
        if (Velocity.mc.playerController.getCurrentGameType() == WorldSettings.GameType.SPECTATOR) {
            return;
        }
        if (Velocity.mc.thePlayer.isInWater() && this.WaterCheckValue.getValue()) {
            return;
        }
        if (Velocity.mc.thePlayer.isOnLadder()) {
            return;
        }
        if (Client.instance.moduleManager.getModule(Blink.class).getState() && this.BlinkCheck.getValue()) {
            return;
        }
        VelocityMode vel = VelocityMode.get(modes.getValue().name());
        vel.onPacketSend(e);
    }

    public enum velMode {
        Grim,
        AAC,
        Cancel,
        JumpReset,
        Hypixel
    }
}

