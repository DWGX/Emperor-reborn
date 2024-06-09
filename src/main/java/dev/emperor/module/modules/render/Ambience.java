package dev.emperor.module.modules.render;

import dev.emperor.event.EventTarget;
import dev.emperor.event.world.EventPacketReceive;
import dev.emperor.event.world.EventTick;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.values.BoolValue;
import dev.emperor.module.values.ModeValue;
import dev.emperor.module.values.NumberValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

public class Ambience
extends Module {
    private final ModeValue<timemode> mode = new ModeValue("Time-Mode", (Enum[])timemode.values(), (Enum)timemode.Static);
    private final ModeValue<weatherMode> weathermode = new ModeValue("Weather-Mode", (Enum[])weatherMode.values(), (Enum)weatherMode.Clear);
    private final NumberValue cycleSpeed = new NumberValue("Cycle-Speed", 24.0, 1.0, 24.0, 1.0);
    private final BoolValue reverseCycle = new BoolValue("Reverse-Cycle", false);
    private final NumberValue time = new NumberValue("Static-Time", 24000.0, 0.0, 24000.0, 100.0);
    private final NumberValue rainstrength = new NumberValue("Rain-Strength", 0.1, 0.1, 0.5, 0.1);
    private int timeCycle = 0;

    public Ambience() {
        super("Ambience", Category.Render);
    }

    @Override
    public void onEnable() {
        this.timeCycle = 0;
    }

    @EventTarget
    public void onUpdate(EventTick event) {
        if (((timemode)((Object)this.mode.getValue())).name().equalsIgnoreCase("static")) {
            Ambience.mc.theWorld.setWorldTime(((Double)this.time.getValue()).longValue());
        } else {
            Ambience.mc.theWorld.setWorldTime(this.timeCycle);
            this.timeCycle = (int)((double)this.timeCycle + ((Boolean)this.reverseCycle.getValue() != false ? -((Double)this.cycleSpeed.getValue()).doubleValue() : (Double)this.cycleSpeed.getValue()) * 10.0);
            if (this.timeCycle > 24000) {
                this.timeCycle = 0;
            } else if (this.timeCycle < 0) {
                this.timeCycle = 24000;
            }
        }
        if (((weatherMode)((Object)this.weathermode.getValue())).name().equalsIgnoreCase("clear")) {
            Ambience.mc.theWorld.setRainStrength(0.0f);
        } else {
            Ambience.mc.theWorld.setRainStrength(((Double)this.rainstrength.getValue()).longValue());
        }
    }

    @EventTarget
    public void onPacket(EventPacketReceive event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof S03PacketTimeUpdate) {
            event.setCancelled(true);
        }
    }

    public static enum weatherMode {
        Clear,
        Rain;

    }

    public static enum timemode {
        Static,
        Cycle;

    }
}

