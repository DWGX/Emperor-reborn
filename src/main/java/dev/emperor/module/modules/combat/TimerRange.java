package dev.emperor.module.modules.combat;

import dev.emperor.event.EventTarget;
import dev.emperor.event.attack.EventAttack;
import dev.emperor.event.world.EventPacketReceive;
import dev.emperor.event.world.EventUpdate;
import dev.emperor.gui.notification.NotificationManager;
import dev.emperor.gui.notification.NotificationType;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.values.BoolValue;
import dev.emperor.module.values.ModeValue;
import dev.emperor.module.values.NumberValue;
import dev.emperor.utils.client.MathUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class TimerRange
extends Module {
    private int playerTicks = 0;
    private int smartCounter = 0;
    private boolean confirmAttack = false;
    private boolean confirmLagBack = false;
    private final ModeValue<mode> timerBoostMode = new ModeValue("TimerMode", (Enum[])mode.values(), (Enum)mode.Normal);
    private final NumberValue ticksValue = new NumberValue("Ticks", 10.0, 1.0, 20.0, 1.0);
    private final NumberValue timerBoostValue = new NumberValue("TimerBoost", 1.5, 0.01, 10.0, 0.01);
    private final NumberValue timerChargedValue = new NumberValue("TimerCharged", 0.45, 0.05, 5.0, 0.01);
    private final NumberValue rangeValue = new NumberValue("Range", 3.5, 1.0, 5.0, 0.1, () -> this.timerBoostMode.is("Normal"));
    private final NumberValue minRange = new NumberValue("MinRange", 1.0, 1.0, 5.0, 0.1, () -> this.timerBoostMode.is("Smart"));
    private final NumberValue maxRange = new NumberValue("MaxRange", 5.0, 1.0, 5.0, 0.1, () -> this.timerBoostMode.is("Smart"));
    private final NumberValue minTickDelay = new NumberValue("MinTickDelay", 5.0, 1.0, 100.0, 1.0, () -> this.timerBoostMode.is("Smart"));
    private final NumberValue maxTickDelay = new NumberValue("MaxTickDelay", 100.0, 1.0, 100.0, 1.0, () -> this.timerBoostMode.is("Smart"));
    private final BoolValue resetlagBack = new BoolValue("ResetOnLagback", false);

    public TimerRange() {
        super("TimerRange", Category.Combat);
    }

    @Override
    public void onEnable() {
        this.timerReset();
    }

    @Override
    public void onDisable() {
        this.timerReset();
        this.smartCounter = 0;
        this.playerTicks = 0;
    }

    @EventTarget
    public void onAttack(EventAttack event) {
        boolean shouldSlowed;
        if (!(event.getTarget() instanceof EntityLivingBase) || this.shouldResetTimer()) {
            this.timerReset();
            return;
        }
        this.confirmAttack = true;
        EntityLivingBase targetEntity = (EntityLivingBase)event.getTarget();
        double entityDistance = TimerRange.mc.thePlayer.getClosestDistanceToEntity(targetEntity);
        int randomCounter = MathUtil.getRandomNumberUsingNextInt(((Double)this.minTickDelay.getValue()).intValue(), ((Double)this.maxTickDelay.getValue()).intValue());
        double randomRange = MathUtil.getRandomInRange((Double)this.minRange.getValue(), (Double)this.maxRange.getValue());
        ++this.smartCounter;
        switch (((mode)((Object)this.timerBoostMode.getValue())).name()) {
            case "Normal": {
                shouldSlowed = entityDistance <= (Double)this.rangeValue.getValue();
                break;
            }
            case "Smart": {
                shouldSlowed = this.smartCounter >= randomCounter && entityDistance <= randomRange;
                break;
            }
            default: {
                shouldSlowed = false;
            }
        }
        if (shouldSlowed && this.confirmAttack) {
            this.confirmAttack = false;
            this.playerTicks = ((Double)this.ticksValue.getValue()).intValue();
            if (((Boolean)this.resetlagBack.getValue()).booleanValue()) {
                this.confirmLagBack = true;
            }
            this.smartCounter = 0;
        } else {
            this.timerReset();
        }
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        float adjustedTimerSpeed;
        this.setSuffix(((mode)((Object)this.timerBoostMode.getValue())).name());
        double timerboost = MathUtil.getRandomInRange(0.5, 0.56);
        double charged = MathUtil.getRandomInRange(0.75, 0.91);
        if (this.playerTicks <= 0) {
            this.timerReset();
            return;
        }
        double tickProgress = (double)this.playerTicks / (Double)this.ticksValue.getValue();
        float playerSpeed = (float)(tickProgress < timerboost ? (Double)this.timerBoostValue.getValue() : (tickProgress < charged ? (Double)this.timerChargedValue.getValue() : 1.0));
        float speedAdjustment = playerSpeed >= 0.0f ? playerSpeed : (float)(1.0 + (Double)this.ticksValue.getValue() - (double)this.playerTicks);
        TimerRange.mc.timer.timerSpeed = adjustedTimerSpeed = Math.max(speedAdjustment, 0.0f);
        --this.playerTicks;
    }

    private void timerReset() {
        TimerRange.mc.timer.timerSpeed = 1.0f;
    }

    private boolean shouldResetTimer() {
        EntityPlayerSP player = TimerRange.mc.thePlayer;
        return this.playerTicks >= 1 || player.isSpectator() || player.isDead || player.isInWater() || player.isInLava() || player.isInWeb || player.isOnLadder() || player.isRiding();
    }

    @EventTarget
    public void onPacket(EventPacketReceive event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook && ((Boolean)this.resetlagBack.getValue()).booleanValue() && this.confirmLagBack && !this.shouldResetTimer()) {
            this.confirmLagBack = false;
            this.timerReset();
            NotificationManager.post(NotificationType.WARNING, "TimerRange", "Lagback Detected | Timer Reset");
        }
    }

    public static enum mode {
        Normal,
        Smart;

    }
}

