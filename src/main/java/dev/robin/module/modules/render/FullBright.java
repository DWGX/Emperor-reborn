package dev.robin.module.modules.render;

import dev.robin.event.EventTarget;
import dev.robin.event.world.EventTick;
import dev.robin.module.Category;
import dev.robin.module.Module;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class FullBright
extends Module {
    public FullBright() {
        super("FullBright", Category.Render);
    }

    @EventTarget
    public void onTick(EventTick event) {
        FullBright.mc.thePlayer.addPotionEffect(new PotionEffect(Potion.nightVision.id, 1337, 5));
    }

    @Override
    public void onDisable() {
        if (FullBright.mc.thePlayer.isPotionActive(Potion.nightVision)) {
            FullBright.mc.thePlayer.removePotionEffectClient(Potion.nightVision.id);
        }
    }
}

