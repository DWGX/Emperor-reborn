package dev.robin.module.modules.misc;

import dev.robin.Client;
import dev.robin.event.EventTarget;
import dev.robin.event.world.EventMotion;
import dev.robin.event.world.EventPacketReceive;
import dev.robin.event.world.EventWorldLoad;
import dev.robin.module.Category;
import dev.robin.module.Module;
import dev.robin.module.modules.combat.KillAura;
import dev.robin.module.modules.player.ChestStealer;
import dev.robin.module.modules.player.InvCleaner;
import dev.robin.module.modules.world.Scaffold;
import dev.robin.module.values.BoolValue;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.world.WorldSettings;

public class ModuleHelper
extends Module {
    private final BoolValue lagBackCheckValue = new BoolValue("LagBackCheck", false);

    public ModuleHelper() {
        super("ModuleHelper", Category.Misc);
    }

    @EventTarget
    public void onPacketReceive(EventPacketReceive event) {
        if (event.getPacket() instanceof S01PacketJoinGame || event.getPacket() instanceof S08PacketPlayerPosLook && ((Boolean)this.lagBackCheckValue.getValue()).booleanValue()) {
            this.disableModule();
        }
    }

    @EventTarget
    public void onWorld(EventWorldLoad event) {
        this.disableModule();
    }

    public void disableModule() {
        if (Client.instance.moduleManager.getModule(KillAura.class).state) {
            Client.instance.moduleManager.getModule(KillAura.class).setState(false);
        }
        if (Client.instance.moduleManager.getModule(InvCleaner.class).state) {
            Client.instance.moduleManager.getModule(InvCleaner.class).setState(false);
        }
        if (Client.instance.moduleManager.getModule(ChestStealer.class).state) {
            Client.instance.moduleManager.getModule(ChestStealer.class).setState(false);
        }
        if (Client.instance.moduleManager.getModule(Scaffold.class).state) {
            Client.instance.moduleManager.getModule(Scaffold.class).setState(false);
        }
    }

    @EventTarget
    public void onMotion(EventMotion event) {
        if (ModuleHelper.mc.playerController.getCurrentGameType() == WorldSettings.GameType.SPECTATOR) {
            if (Client.instance.moduleManager.getModule(InvCleaner.class).state) {
                Client.instance.moduleManager.getModule(InvCleaner.class).setState(false);
            }
            if (Client.instance.moduleManager.getModule(ChestStealer.class).state) {
                Client.instance.moduleManager.getModule(ChestStealer.class).setState(false);
            }
        }
    }
}

