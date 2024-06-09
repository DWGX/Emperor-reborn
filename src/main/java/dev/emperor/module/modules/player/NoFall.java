package dev.emperor.module.modules.player;

import dev.emperor.event.EventTarget;
import dev.emperor.event.world.EventMotion;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.values.ModeValue;

public class NoFall
extends Module {
    ModeValue<modeEnum> mode = new ModeValue("Mode", (Enum[])modeEnum.values(), (Enum)modeEnum.HypixelSpoof);

    public NoFall() {
        super("NoFall", Category.Player);
    }

    @EventTarget
    public void onUpdate(EventMotion e) {
        this.setSuffix(((modeEnum)((Object)this.mode.getValue())).toString());
        switch ((modeEnum)((Object)this.mode.getValue())) {
            case HypixelSpoof: {
                if (NoFall.mc.thePlayer.ticksExisted <= 50 || !(NoFall.mc.thePlayer.fallDistance > 3.0f)) break;
                e.setOnGround(true);
                break;
            }
            case GroundSpoof: {
                if (NoFall.mc.thePlayer.onGround) break;
                e.setOnGround(true);
            }
        }
    }

    static enum modeEnum {
        HypixelSpoof,
        GroundSpoof;

    }
}

