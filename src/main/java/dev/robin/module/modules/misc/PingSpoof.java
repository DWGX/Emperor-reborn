package dev.robin.module.modules.misc;

import dev.robin.event.EventTarget;
import dev.robin.event.world.EventUpdate;
import dev.robin.module.Category;
import dev.robin.module.Module;
import dev.robin.module.values.BoolValue;
import dev.robin.module.values.NumberValue;
import dev.robin.utils.client.MathUtil;
import dev.robin.utils.component.PingSpoofComponent;

public class PingSpoof
extends Module {
    private final NumberValue minDelay = new NumberValue("MinDelay", 50.0, 50.0, 30000.0, 50.0);
    private final NumberValue maxDelay = new NumberValue("MaxDelay", 50.0, 50.0, 30000.0, 50.0);
    private final BoolValue teleports = new BoolValue("DelayTeleports", false);
    private final BoolValue velocity = new BoolValue("DelayVelocity", false);
    private final BoolValue world = new BoolValue("DelayBlockUpdates", false);
    private final BoolValue entities = new BoolValue("DelayEntityMovements", false);

    public PingSpoof() {
        super("PingSpoof", Category.Misc);
    }

    @Override
    public void onEnable() {
        PingSpoofComponent.spoofing = true;
    }

    @Override
    public void onDisable() {
        PingSpoofComponent.spoofing = false;
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        PingSpoofComponent.setSpoofing(MathUtil.getRandom(((Double)this.minDelay.getValue()).intValue(), ((Double)this.maxDelay.getValue()).intValue()), true, (Boolean)this.teleports.getValue(), (Boolean)this.velocity.getValue(), (Boolean)this.world.getValue(), (Boolean)this.entities.getValue());
    }
}

