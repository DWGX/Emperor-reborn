package dev.emperor.module.modules.movement;

import dev.emperor.event.EventTarget;
import dev.emperor.event.world.EventPacketReceive;
import dev.emperor.event.world.EventPacketSend;
import dev.emperor.event.world.EventUpdate;
import dev.emperor.event.world.EventWorldLoad;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.values.BoolValue;
import dev.emperor.module.values.NumberValue;
import dev.emperor.utils.BlinkUtils;
import dev.emperor.utils.DebugUtil;
import dev.emperor.utils.client.MathUtil;
import dev.emperor.utils.client.StopWatch;
import dev.emperor.utils.component.BadPacketsComponent;
import dev.emperor.utils.player.MoveUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class Timer
extends Module {
    private final NumberValue speedValue = new NumberValue("Speed", 2.0, 0.1, 10.0, 0.1);
    private final BoolValue onMoveValue = new BoolValue("OnMove", true);
    private final BoolValue grimTimer = new BoolValue("Grim-Timer[Balance]", false);
    private final BoolValue spartanBypass = new BoolValue("Spartan-Grim-Timer[Balance-Bypass]", false);
    private final BoolValue timerDebug = new BoolValue("Grim-Timer[Balance-Debug]", false, this.grimTimer::getValue);
    int balance = 0;
    boolean blinkStart;
    private final StopWatch stopWatch = new StopWatch();

    public Timer() {
        super("Timer", Category.Movement);
    }

    @Override
    public void onDisable() {
        if (Timer.mc.thePlayer == null) {
            return;
        }
        Timer.mc.timer.timerSpeed = 1.0f;
        this.balance = 0;
        this.reset();
        if (this.blinkStart) {
            if (this.spartanBypass.getValue()) {
                Timer.mc.timer.timerSpeed = 0.1f;
            }
            BlinkUtils.setBlinkState(true, true, false, false, false, false, false, false, false, false, false);
            BlinkUtils.clearPacket(null, false, -1);
            if (this.spartanBypass.getValue()) {
                Timer.mc.timer.timerSpeed = 1.0f;
            }
        }
    }

    @Override
    public void onEnable() {
        this.reset();
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (this.onMoveValue.getValue()) {
            Timer.mc.timer.timerSpeed = MoveUtil.isMoving() ? this.speedValue.getValue().floatValue() : 1.0f;
        } else {
            float f = Timer.mc.timer.timerSpeed = !this.spartanBypass.getValue() ? this.speedValue.getValue().floatValue() : (float)MathUtil.getRandom(0.1, this.speedValue.getValue());
        }
        if (this.balance > 0 && this.grimTimer.getValue() && this.timerDebug.getValue() && Timer.mc.thePlayer.ticksExisted % 20 == 0) {
            DebugUtil.log("[GrimTimer-Balance]:" + this.balance);
        }
        if (!MoveUtil.isMoving()) {
            BlinkUtils.setBlinkState(false, false, false, false, true, true, false, false, false, false, false);
            this.blinkStart = true;
        }
    }

    private void reset() {
        this.balance = 0;
        this.stopWatch.reset();
    }

    @EventTarget
    public void onPacketReceive(EventPacketReceive event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof S08PacketPlayerPosLook && this.grimTimer.getValue() && this.balance != 0) {
            this.balance = 0;
            if (this.blinkStart) {
                if (this.spartanBypass.getValue()) {
                    Timer.mc.timer.timerSpeed = 0.1f;
                }
                BlinkUtils.setBlinkState(true, true, false, false, false, false, false, false, false, false, false);
                BlinkUtils.clearPacket(null, false, -1);
                if (this.spartanBypass.getValue()) {
                    Timer.mc.timer.timerSpeed = 1.0f;
                }
            }
        }
    }

    @EventTarget
    public void onPacketSend(EventPacketSend event) {
        if (event.getPacket() instanceof C03PacketPlayer c03PacketPlayer && this.grimTimer.getValue()) {
            event.setCancelled(!c03PacketPlayer.getRotating() && !c03PacketPlayer.isMoving() && !BadPacketsComponent.bad() && Timer.mc.thePlayer.posX == Timer.mc.thePlayer.lastTickPosX && Timer.mc.thePlayer.posY == Timer.mc.thePlayer.lastTickPosY && Timer.mc.thePlayer.posZ == Timer.mc.thePlayer.lastTickPosZ);
            if (!event.isCancelled()) {
                this.balance -= 50;
            }
            this.balance = (int)((long)this.balance + this.stopWatch.getElapsedTime());
            this.stopWatch.reset();
        }
    }

    @EventTarget
    public void onWorld(EventWorldLoad event) {
        this.reset();
        this.state = false;
    }
}

