package dev.emperor.module.modules.combat.velocity;

import dev.emperor.event.attack.EventAttack;
import dev.emperor.event.world.EventPacketReceive;
import dev.emperor.event.world.EventPacketSend;
import dev.emperor.event.world.EventTick;
import dev.emperor.event.world.EventUpdate;
import dev.emperor.event.world.EventWorldLoad;
import dev.emperor.module.Category;

import java.util.HashMap;
import net.minecraft.client.Minecraft;

public abstract class VelocityMode {
    private final String name;
    Minecraft mc = Minecraft.getMinecraft();
    private static final HashMap<Class<? extends VelocityMode>, VelocityMode> velocitys = new HashMap();

    public VelocityMode(String name, Category category) {
        this.name = name;
    }

    public static void init() {
        velocitys.put(AACVelocity.class, new AACVelocity());
        velocitys.put(CancelVelocity.class, new CancelVelocity());
        velocitys.put(GrimVelocity.class, new GrimVelocity());
        velocitys.put(JumpResetVelocity.class, new JumpResetVelocity());
        velocitys.put(HypixelVelocity.class, new HypixelVelocity());
    }

    public abstract void onEnable();

    public abstract void onDisable();

    public abstract String getTag();

    public static VelocityMode get(String name) {
        return velocitys.values().stream().filter(vel -> vel.name.equals(name)).findFirst().orElse(null);
    }

    public abstract void onAttack(EventAttack var1);

    public abstract void onPacketSend(EventPacketSend var1);

    public abstract void onWorldLoad(EventWorldLoad var1);

    public abstract void onPacketReceive(EventPacketReceive var1);

    public abstract void onUpdate(EventUpdate var1);

    public abstract void onTick(EventTick var1);
}

