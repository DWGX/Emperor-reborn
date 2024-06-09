package dev.robin.utils.mobends.pack;

import dev.robin.utils.mobends.data.EntityData;

public class BendsVar {
    public static EntityData tempData;

    public static float getGlobalVar(String name) {
        if (name.equalsIgnoreCase("ticks")) {
            if (tempData == null) {
                return 0.0f;
            }
            return BendsVar.tempData.ticks;
        }
        if (name.equalsIgnoreCase("ticksAfterPunch")) {
            if (tempData == null) {
                return 0.0f;
            }
            return BendsVar.tempData.ticksAfterPunch;
        }
        return Float.POSITIVE_INFINITY;
    }
}

