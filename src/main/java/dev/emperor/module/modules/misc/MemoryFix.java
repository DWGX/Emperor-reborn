package dev.emperor.module.modules.misc;

import dev.emperor.event.EventTarget;
import dev.emperor.event.world.EventTick;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.values.NumberValue;
import dev.emperor.utils.client.TimeUtil;
import dev.emperor.utils.system.MemoryUtils;

public class MemoryFix
extends Module {
    private final NumberValue cleanUpDelay = new NumberValue("CleanUpDelay", 120.0, 10.0, 600.0, 1.0);
    private final NumberValue cleanUpLimit = new NumberValue("CleanUpLimit", 80.0, 20.0, 95.0, 1.0);
    private final TimeUtil cleanUpDelayTime = new TimeUtil();

    public MemoryFix() {
        super("MemoryFix", Category.Misc);
    }

    @EventTarget
    public void onTick(EventTick event) {
        long maxMem = Runtime.getRuntime().maxMemory();
        long totalMem = Runtime.getRuntime().totalMemory();
        long freeMem = Runtime.getRuntime().freeMemory();
        long usedMem = totalMem - freeMem;
        if (this.cleanUpDelayTime.hasReached((Double)this.cleanUpDelay.getValue() * 1000.0) && (Double)this.cleanUpLimit.getValue() <= (double)(usedMem * 100L / maxMem)) {
            MemoryUtils.memoryCleanup();
            this.cleanUpDelayTime.reset();
        }
    }
}

