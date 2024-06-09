package dev.robin.module.modules.combat.velocity;

import dev.robin.event.EventTarget;
import dev.robin.event.attack.EventAttack;
import dev.robin.event.world.EventPacketReceive;
import dev.robin.event.world.EventPacketSend;
import dev.robin.event.world.EventTick;
import dev.robin.event.world.EventUpdate;
import dev.robin.event.world.EventWorldLoad;
import dev.robin.module.Category;
import dev.robin.module.modules.combat.velocity.VelocityMode;

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

