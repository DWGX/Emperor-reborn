package dev.emperor.module.modules.combat.velocity;

import dev.emperor.event.EventTarget;
import dev.emperor.event.attack.EventAttack;
import dev.emperor.event.world.EventPacketReceive;
import dev.emperor.event.world.EventPacketSend;
import dev.emperor.event.world.EventTick;
import dev.emperor.event.world.EventUpdate;
import dev.emperor.event.world.EventWorldLoad;
import dev.emperor.module.Category;

public class JumpResetVelocity
extends VelocityMode {
    public JumpResetVelocity() {
        super("JumpReset", Category.Combat);
    }

    @Override
    public String getTag() {
        return "JumpReset";
    }

    @Override
    public void onAttack(EventAttack event) {
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onPacketSend(EventPacketSend event) {
    }

    @Override
    public void onWorldLoad(EventWorldLoad event) {
    }

    @Override
    public void onUpdate(EventUpdate event) {
        if (this.mc.thePlayer.onGround && this.mc.thePlayer.hurtTime > 0) {
            this.mc.thePlayer.setSprinting(false);
            this.mc.thePlayer.movementInput.jump = true;
        }
    }

    @Override
    public void onTick(EventTick event) {
    }

    @Override
    @EventTarget
    public void onPacketReceive(EventPacketReceive e) {
    }
}

