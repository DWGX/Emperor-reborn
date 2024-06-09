package dev.robin.module.modules.player;

import dev.robin.event.EventTarget;
import dev.robin.event.world.EventMotion;
import dev.robin.module.Category;
import dev.robin.module.Module;
import dev.robin.module.values.ModeValue;

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

