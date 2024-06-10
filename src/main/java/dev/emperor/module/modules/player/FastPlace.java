package dev.emperor.module.modules.player;

import dev.emperor.event.EventTarget;
import dev.emperor.event.world.EventTick;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.values.NumberValue;
import net.minecraft.item.ItemBlock;

public class FastPlace
extends Module {
    private final NumberValue ticks = new NumberValue("Ticks", 0.0, 4.0, 0.0, 1.0);

    public FastPlace() {
        super("FastPlace", Category.Player);
    }

    @EventTarget
    public void onTick(EventTick e) {
        if (FastPlace.mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock) {
            FastPlace.mc.rightClickDelayTimer = Math.min(0, this.ticks.getValue().intValue());
        }
    }
}

