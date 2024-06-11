package dev.emperor.module.modules.world;

import dev.emperor.Client;
import dev.emperor.event.EventTarget;
import dev.emperor.event.world.EventPacketReceive;
import dev.emperor.event.world.EventPacketSend;
import dev.emperor.event.world.EventUpdate;
import dev.emperor.gui.notification.NotificationManager;
import dev.emperor.gui.notification.NotificationType;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.modules.combat.KillAura;
import dev.emperor.module.modules.player.ChestStealer;
import dev.emperor.module.modules.player.InvCleaner;
import dev.emperor.module.values.BoolValue;
import dev.emperor.utils.client.PacketUtil;
import javax.vecmath.Vector2f;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class Hub
        extends Module {
    public BoolValue SB = new BoolValue("谁用谁SB", true);

    public Hub() {
        super("Hub", Category.World);
    }

    @Override
    public void onEnable() {
        Client.instance.moduleManager.getModule(InvCleaner.class).setState(false);
        Client.instance.moduleManager.getModule(ChestStealer.class).setState(false);
        Client.instance.moduleManager.getModule(KillAura.class).setState(false);
        Client.instance.moduleManager.getModule(ChestAura.class).setState(false);
        Client.instance.moduleManager.getModule(Scaffold.class).setState(false);
        mc.thePlayer.sendChatMessage("/hub");
        Client.instance.moduleManager.getModule(Hub.class).setState(false);
    }

}

