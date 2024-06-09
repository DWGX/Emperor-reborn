package dev.emperor.module.modules.movement;

import dev.emperor.event.EventTarget;
import dev.emperor.event.world.EventStrafe;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.utils.player.MoveUtil;

public class Strafe
extends Module {
    public Strafe() {
        super("Strafe", Category.Movement);
    }

    @Override
    public void onDisable() {
        Strafe.mc.timer.timerSpeed = 1.0f;
    }

    @EventTarget
    public void onStrafe(EventStrafe event) {
        MoveUtil.strafe();
    }
}

