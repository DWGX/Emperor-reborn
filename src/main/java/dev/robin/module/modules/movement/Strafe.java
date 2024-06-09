package dev.robin.module.modules.movement;

import dev.robin.event.EventTarget;
import dev.robin.event.world.EventStrafe;
import dev.robin.module.Category;
import dev.robin.module.Module;
import dev.robin.utils.player.MoveUtil;

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

