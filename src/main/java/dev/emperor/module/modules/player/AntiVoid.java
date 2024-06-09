package dev.emperor.module.modules.player;

import dev.emperor.event.EventTarget;
import dev.emperor.event.world.EventMotion;
import dev.emperor.event.world.EventPacketReceive;
import dev.emperor.event.world.EventPacketSend;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.values.BoolValue;
import dev.emperor.module.values.ModeValue;
import dev.emperor.module.values.NumberValue;
import dev.emperor.utils.DebugUtil;
import dev.emperor.utils.component.FallDistanceComponent;
import dev.emperor.utils.player.PlayerUtil;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.Vec3;

public class AntiVoid
extends Module {
    private final NumberValue distance = new NumberValue("Distance", 5.0, 0.0, 10.0, 1.0);
    private final BoolValue toggleScaffold = new BoolValue("Toggle Scaffold", true);
    private int overVoidTicks;
    private Vec3 position;
    private Vec3 motion;
    private boolean wasVoid;
    private boolean setBack;
    boolean shouldStuck;
    double x;
    double y;
    double z = 0.0;
    boolean wait;
    ModeValue<modeEnum> mode = new ModeValue("Mode", (Enum[])modeEnum.values(), (Enum)modeEnum.Grim);

    public AntiVoid() {
        super("AntiVoid", Category.Player);
    }

    @Override
    public void onDisable() {
        AntiVoid.mc.thePlayer.isDead = false;
    }

    @EventTarget
    public void onPacket(EventPacketSend event) {
        if (!AntiVoid.mc.thePlayer.onGround && this.shouldStuck && event.getPacket() instanceof C03PacketPlayer && !(event.packet instanceof C03PacketPlayer.C05PacketPlayerLook) && !(event.packet instanceof C03PacketPlayer.C06PacketPlayerPosLook)) {
            C03PacketPlayer c03 = (C03PacketPlayer)event.getPacket();
            event.setCancelled();
        }
        if (event.getPacket() instanceof C08PacketPlayerBlockPlacement && this.wait) {
            this.shouldStuck = false;
            AntiVoid.mc.timer.timerSpeed = 0.2f;
            this.wait = false;
        }
    }

    @EventTarget
    public void onPacket(EventPacketReceive event) {
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook s08 = (S08PacketPlayerPosLook)event.getPacket();
            this.x = s08.getX();
            this.y = s08.getY();
            this.z = s08.getZ();
            AntiVoid.mc.timer.timerSpeed = 0.2f;
        }
    }

    @EventTarget
    public void onUpdate(EventMotion event) {
        boolean overVoid;
        if (event.isPost()) {
            return;
        }
        if (AntiVoid.mc.thePlayer.getHeldItem() == null) {
            AntiVoid.mc.timer.timerSpeed = 1.0f;
        }
        if (AntiVoid.mc.thePlayer.getHeldItem().getItem() instanceof ItemEnderPearl) {
            this.wait = true;
        }
        if (this.shouldStuck && !AntiVoid.mc.thePlayer.onGround) {
            AntiVoid.mc.thePlayer.motionX = 0.0;
            AntiVoid.mc.thePlayer.motionY = 0.0;
            AntiVoid.mc.thePlayer.motionZ = 0.0;
            AntiVoid.mc.thePlayer.setPositionAndRotation(this.x, this.y, this.z, AntiVoid.mc.thePlayer.rotationYaw, AntiVoid.mc.thePlayer.rotationPitch);
        }
        boolean bl = overVoid = !AntiVoid.mc.thePlayer.onGround && !PlayerUtil.isBlockUnder(30.0, true);
        if (!overVoid) {
            this.shouldStuck = false;
            this.x = AntiVoid.mc.thePlayer.posX;
            this.y = AntiVoid.mc.thePlayer.posY;
            this.z = AntiVoid.mc.thePlayer.posZ;
            AntiVoid.mc.timer.timerSpeed = 1.0f;
        }
        if (overVoid) {
            ++this.overVoidTicks;
        } else if (AntiVoid.mc.thePlayer.onGround) {
            this.overVoidTicks = 0;
        }
        if (overVoid && this.position != null && this.motion != null && (double)this.overVoidTicks < 30.0 + (Double)this.distance.getValue() * 20.0) {
            if (!this.setBack) {
                this.wasVoid = true;
                if ((double)FallDistanceComponent.distance > (Double)this.distance.getValue() || this.setBack) {
                    FallDistanceComponent.distance = 0.0f;
                    this.setBack = true;
                    DebugUtil.log(1);
                    this.shouldStuck = true;
                    this.x = AntiVoid.mc.thePlayer.posX;
                    this.y = AntiVoid.mc.thePlayer.posY;
                    this.z = AntiVoid.mc.thePlayer.posZ;
                }
            }
        } else {
            if (this.shouldStuck) {
                this.toggle();
            }
            this.shouldStuck = false;
            AntiVoid.mc.timer.timerSpeed = 1.0f;
            this.setBack = false;
            if (this.wasVoid) {
                this.wasVoid = false;
            }
            this.motion = new Vec3(AntiVoid.mc.thePlayer.motionX, AntiVoid.mc.thePlayer.motionY, AntiVoid.mc.thePlayer.motionZ);
            this.position = new Vec3(AntiVoid.mc.thePlayer.posX, AntiVoid.mc.thePlayer.posY, AntiVoid.mc.thePlayer.posZ);
        }
    }

    static enum modeEnum {
        Grim;

    }
}

