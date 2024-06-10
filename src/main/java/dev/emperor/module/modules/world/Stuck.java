package dev.emperor.module.modules.world;

import dev.emperor.event.EventTarget;
import dev.emperor.event.world.EventPacketReceive;
import dev.emperor.event.world.EventPacketSend;
import dev.emperor.event.world.EventUpdate;
import dev.emperor.gui.notification.NotificationManager;
import dev.emperor.gui.notification.NotificationType;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.values.BoolValue;
import dev.emperor.utils.client.PacketUtil;
import javax.vecmath.Vector2f;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class Stuck
extends Module {
    private static Stuck INSTANCE;
    public BoolValue antiSB = new BoolValue("Anti SB", true);
    private double x;
    private double y;
    private double z;
    private double motionX;
    private double motionY;
    private double motionZ;
    private boolean onGround = false;
    private Vector2f rotation;

    public Stuck() {
        super("Stuck", Category.World);
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        if (Stuck.mc.thePlayer == null) {
            return;
        }
        this.onGround = Stuck.mc.thePlayer.onGround;
        this.x = Stuck.mc.thePlayer.posX;
        this.y = Stuck.mc.thePlayer.posY;
        this.z = Stuck.mc.thePlayer.posZ;
        this.motionX = Stuck.mc.thePlayer.motionX;
        this.motionY = Stuck.mc.thePlayer.motionY;
        this.motionZ = Stuck.mc.thePlayer.motionZ;
        this.rotation = new Vector2f(Stuck.mc.thePlayer.rotationYaw, Stuck.mc.thePlayer.rotationPitch);
        float f = Stuck.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        float gcd = f * f * f * 1.2f;
        this.rotation.x -= this.rotation.x % gcd;
        this.rotation.y -= this.rotation.y % gcd;
    }

    @Override
    public void onDisable() {
        if (this.antiSB.getValue() && !Stuck.mc.thePlayer.onGround) {
            NotificationManager.post(NotificationType.WARNING, "Stuck", "You can't disable this module now!");
            this.setState(true);
        }
    }

    @EventTarget
    public void onPacket(EventPacketSend event) {
        if (event.getPacket() instanceof C08PacketPlayerBlockPlacement packet) {
            Vector2f current = new Vector2f(Stuck.mc.thePlayer.rotationYaw, Stuck.mc.thePlayer.rotationPitch);
            float f = Stuck.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
            float gcd = f * f * f * 1.2f;
            current.x -= current.x % gcd;
            current.y -= current.y % gcd;
            if (this.rotation.equals(current)) {
                return;
            }
            this.rotation = current;
            event.setCancelled(true);
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(current.x, current.y, this.onGround));
            PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(Stuck.mc.thePlayer.getHeldItem()));
        }
        if (event.getPacket() instanceof C03PacketPlayer) {
            event.setCancelled(true);
        }
    }

    @EventTarget
    public void onPacketR(EventPacketReceive event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            this.setStateSilent(false);
        }
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        Stuck.mc.thePlayer.motionX = 0.0;
        Stuck.mc.thePlayer.motionY = 0.0;
        Stuck.mc.thePlayer.motionZ = 0.0;
        Stuck.mc.thePlayer.setPosition(this.x, this.y, this.z);
    }

    public static boolean isStuck() {
        return INSTANCE.getState();
    }

    public static void throwPearl(Vector2f current) {
        if (!INSTANCE.getState()) {
            return;
        }
        Stuck.mc.thePlayer.rotationYaw = current.x;
        Stuck.mc.thePlayer.rotationPitch = current.y;
        float f = Stuck.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        float gcd = f * f * f * 1.2f;
        current.x -= current.x % gcd;
        current.y -= current.y % gcd;
        if (!Stuck.INSTANCE.rotation.equals(current)) {
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(current.x, current.y, Stuck.INSTANCE.onGround));
        }
        Stuck.INSTANCE.rotation = current;
        PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(Stuck.mc.thePlayer.getHeldItem()));
    }
}

