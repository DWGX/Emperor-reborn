package dev.robin.module.modules.movement;

import dev.robin.event.EventTarget;
import dev.robin.event.world.EventStrafe;
import dev.robin.module.Category;
import dev.robin.module.Module;

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

