package dev.robin.module.modules.combat;

import dev.robin.Client;
import dev.robin.event.EventTarget;
import dev.robin.event.attack.EventAttack;
import dev.robin.event.world.EventPacketReceive;
import dev.robin.event.world.EventPacketSend;
import dev.robin.event.world.EventTick;
import dev.robin.event.world.EventUpdate;
import dev.robin.event.world.EventWorldLoad;
import dev.robin.module.Category;
import dev.robin.module.Module;
import dev.robin.module.modules.combat.velocity.AACVelocity;
import dev.robin.module.modules.combat.velocity.GrimVelocity;
import dev.robin.module.modules.combat.velocity.VelocityMode;
import dev.robin.module.modules.player.Blink;
import dev.robin.module.values.BoolValue;
import dev.robin.module.values.ModeValue;
import dev.robin.module.values.NumberValue;
import dev.robin.utils.DebugUtil;
import dev.robin.utils.player.MoveUtil;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.world.WorldSettings;
import net.vialoadingbase.ViaLoadingBase;

public class Velocity
extends Module {
    public static final ModeValue<velMode> modes = new ModeValue("Mode", (Enum[])velMode.values(), (Enum)velMode.Cancel);
    public static final ModeValue<GrimVelocity.velMode> grimModes = new ModeValue("GrimMode", (Enum[])GrimVelocity.velMode.values(), (Enum)GrimVelocity.velMode.Vertical, () -> ((velMode)((Object)((Object)modes.getValue()))).equals((Object)velMode.Grim));
    public static final BoolValue grimRayCastValue = new BoolValue("Grim-RayCast", false, () -> ViaLoadingBase.getInstance().getTargetVersion().getVersion() > 47 && ((GrimVelocity.velMode)((Object)((Object)grimModes.getValue()))).equals((Object)GrimVelocity.velMode.Vertical));
    public static final BoolValue grimOnly0078 = new BoolValue("Grim-Only0.078", false, () -> ViaLoadingBase.getInstance().getTargetVersion().getVersion() > 47 && ((GrimVelocity.velMode)((Object)((Object)grimModes.getValue()))).equals((Object)GrimVelocity.velMode.Vertical));
    public static final NumberValue grimCheckRangeValue = new NumberValue("Grim-CheckRange", 4.0, 2.0, 6.0, 0.1, () -> ViaLoadingBase.getInstance().getTargetVersion().getVersion() > 47 && ((GrimVelocity.velMode)((Object)((Object)grimModes.getValue()))).equals((Object)GrimVelocity.velMode.Vertical));
    public static final NumberValue grimAttackPacketCountValue = new NumberValue("Grim-AttackPacket-Count", 12.0, 5.0, 50.0, 1.0, () -> ViaLoadingBase.getInstance().getTargetVersion().getVersion() > 47 && ((GrimVelocity.velMode)((Object)((Object)grimModes.getValue()))).equals((Object)GrimVelocity.velMode.Vertical));
    public static final ModeValue<AACVelocity.velMode> aacModes = new ModeValue("AACMode", (Enum[])AACVelocity.velMode.values(), (Enum)AACVelocity.velMode.AAC5, () -> ((velMode)((Object)((Object)modes.getValue()))).equals((Object)velMode.AAC));
    public BoolValue OnlyMove = new BoolValue("OnlyMove", false);
    public BoolValue OnlyGround = new BoolValue("OnlyGround", false);
    private final BoolValue BlinkCheck = new BoolValue("BlinkCheck", false);
    private final BoolValue FireCheckValue = new BoolValue("FireCheck", false);
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
        if (((Boolean)this.S08FlagCheckValue.getValue()).booleanValue() && this.flags > 0) {
            --this.flags;
        }
        VelocityMode vel = VelocityMode.get(((velMode)((Object)modes.getValue())).name());
        this.setSuffix(vel.getTag());
        vel.onUpdate(event);
    }

    @EventTarget
    public void onPacketReceive(EventPacketReceive e) {
        if (Velocity.mc.thePlayer == null) {
            return;
        }
        Packet<?> packet = e.getPacket();
        if ((Boolean)this.OnlyGround.getValue() != false && !Velocity.mc.thePlayer.onGround || (Boolean)this.OnlyMove.getValue() != false && !MoveUtil.isMoving() || this.flags != 0) {
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
        if (Velocity.mc.thePlayer.isInWater() && ((Boolean)this.WaterCheckValue.getValue()).booleanValue()) {
            return;
        }
        if (Velocity.mc.thePlayer.isOnLadder()) {
            return;
        }
        if (Client.instance.moduleManager.getModule(Blink.class).getState() && ((Boolean)this.BlinkCheck.getValue()).booleanValue()) {
            return;
        }
        if (packet instanceof S08PacketPlayerPosLook && ((Boolean)this.S08FlagCheckValue.getValue()).booleanValue()) {
            this.flags = ((Double)this.S08FlagTickValue.getValue()).intValue();
            if (((Boolean)this.debugMessageValue.getValue()).booleanValue()) {
                DebugUtil.log(true, (Object)"VelocityDebug S08 Flags");
            }
        }
        VelocityMode vel = VelocityMode.get(((velMode)((Object)modes.getValue())).name());
        vel.onPacketReceive(e);
    }

    @EventTarget
    public void onAttack(EventAttack e) {
        VelocityMode vel = VelocityMode.get(((velMode)((Object)modes.getValue())).name());
        vel.onAttack(e);
    }

    @EventTarget
    public void onTick(EventTick e) {
        VelocityMode vel = VelocityMode.get(((velMode)((Object)modes.getValue())).name());
        vel.onTick(e);
    }

    @EventTarget
    public void onWorldLoad(EventWorldLoad e) {
        VelocityMode vel = VelocityMode.get(((velMode)((Object)modes.getValue())).name());
        vel.onWorldLoad(e);
    }

    @EventTarget
    public void onPacketSend(EventPacketSend e) {
        if (Velocity.mc.thePlayer == null) {
            return;
        }
        if ((Boolean)this.OnlyGround.getValue() != false && !Velocity.mc.thePlayer.onGround || (Boolean)this.OnlyMove.getValue() != false && !MoveUtil.isMoving() || this.flags != 0) {
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
        if (Velocity.mc.thePlayer.isInWater() && ((Boolean)this.WaterCheckValue.getValue()).booleanValue()) {
            return;
        }
        if (Velocity.mc.thePlayer.isOnLadder()) {
            return;
        }
        if (Client.instance.moduleManager.getModule(Blink.class).getState() && ((Boolean)this.BlinkCheck.getValue()).booleanValue()) {
            return;
        }
        VelocityMode vel = VelocityMode.get(((velMode)((Object)modes.getValue())).name());
        vel.onPacketSend(e);
    }

    public static enum velMode {
        Grim,
        AAC,
        Cancel,
        JumpReset,
        Hypixel;

    }
}

