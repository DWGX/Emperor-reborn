package dev.emperor.module.modules.movement;

import dev.emperor.event.EventTarget;
import dev.emperor.event.world.EventStrafe;
import dev.emperor.module.Category;
import dev.emperor.module.Module;

public class Sprint
extends Module {
    public Sprint() {
        super("Sprint", Category.Movement);
        this.setState(true);
    }

    @EventTarget
    public void onUpdate(EventStrafe event) {
        Sprint.mc.gameSettings.keyBindSprint.pressed = true;
    }
}

