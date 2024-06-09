package dev.robin.module.modules.player;

import dev.robin.event.EventTarget;
import dev.robin.event.world.EventTick;
import dev.robin.module.Category;
import dev.robin.module.Module;
import dev.robin.module.values.NumberValue;
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
            FastPlace.mc.rightClickDelayTimer = Math.min(0, ((Double)this.ticks.getValue()).intValue());
        }
    }
}

