package dev.emperor.module.modules.player;

import dev.emperor.event.EventTarget;
import dev.emperor.event.rendering.EventRender3D;
import dev.emperor.event.world.*;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.values.BoolValue;
import dev.emperor.module.values.ColorValue;
import dev.emperor.module.values.NumberValue;
import dev.emperor.utils.TimerUtil;
import dev.emperor.utils.client.PacketUtil;
import dev.emperor.utils.component.BlinkComponent;
import dev.emperor.utils.component.PacketComponent;
import dev.emperor.utils.player.CopyOfPlayer;
import dev.emperor.utils.player.EntityUtil;
import dev.emperor.utils.render.RenderUtil;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dev.emperor.utils.vec.Vector3d;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.network.Packet;

public class Blink1
        extends Module {
    private final BoolValue pulse = new BoolValue("Pulse", false);
    private final NumberValue pulseDelay = new NumberValue("Pulse Delay", 1500, 0, 10000, 10);
    private final BoolValue antiAttack = new BoolValue("Anti-Attack", false);
    private final NumberValue range = new NumberValue("Range", 3.0F, 0.0F, 20.0F, 0.1F, antiAttack::getValue);
    private final BoolValue chams = new BoolValue("Chams", true);
    private final ColorValue boxColor = new ColorValue("Box Color", new Color(0, 200, 200, 80).getRGB(), chams::getValue);
    private final BoolValue outlineChams = new BoolValue("Outline Chams", true, chams::getValue);
    private final ColorValue outlineColor = new ColorValue("Box Color", new Color(0, 200, 200, 180).getRGB(), chams::getValue);
    private final BoolValue slowRelease = new BoolValue("Slow Release", false);
    private final NumberValue slowDelay = new NumberValue("Slow Delay", 200, 0, 2000, 10);
    private final NumberValue slowCount = new NumberValue("Slow Count", 1, 0, 20, 1);
    private final List<Vector3d> position = new ArrayList();
    private final TimerUtil releaseTimer = new TimerUtil();
    private final TimerUtil pulseTimer = new TimerUtil();
    private boolean should = false;
    private CopyOfPlayer blinkEntity;

    public Blink1() {
        super("Blink", Category.Player);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onEnable() {
        this.reset();
        if ((Boolean) this.chams.getValue()) {
            this.blinkEntity = new CopyOfPlayer(EntityUtil.getCopiedPlayer(mc.thePlayer), System.currentTimeMillis(), mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.getSkinType().equals("slim"));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onDisable() {
        this.reset();
        if ((Boolean) this.chams.getValue()) {
            this.blinkEntity = null;
        }
        BlinkComponent.stop();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @EventTarget
    public void onUpdate(EventUpdate event) {
        PacketUtil.sendC0F(0, (short) 0, true, true);
    }

    @EventTarget
    public void onMotionEvent(EventMotion e) {
        if (e.isPre()) {
            if (mc.thePlayer.posX != mc.thePlayer.lastTickPosX || mc.thePlayer.posY != mc.thePlayer.lastTickPosY || mc.thePlayer.posZ != mc.thePlayer.lastTickPosZ) {
                this.position.add(new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ));
            }
            if ((Boolean) this.pulse.getValue() && this.pulseTimer.hasTimeElapsed(this.pulseDelay.getValue().intValue())) {
                BlinkComponent.stop();
                this.updateEntityPosition();
                this.position.clear();
                this.pulseTimer.reset();
            }
            if (this.slowRelease.getValue() && this.releaseTimer.hasTimeElapsed(this.slowDelay.getValue().intValue())) {
                BlinkComponent.releaseStoredPackets(this.slowCount.getValue().intValue());
                this.releaseTimer.reset();
            }

            if ((Boolean) this.antiAttack.getValue()) {
                List<Entity> entities = mc.theWorld.getLoadedEntityList();
                Iterator var7 = entities.iterator();

                label73:
                while (true) {
                    Entity entity;
                    do {
                        if (!var7.hasNext()) {
                            break label73;
                        }

                        entity = (Entity) var7.next();
                        if (entity instanceof EntityArrow) {
                            EntityArrow entityArrow = (EntityArrow) entity;
                            if (entityArrow.getTicksInGround() <= 0) {
                                break;
                            }
                        }
                    } while (!(entity instanceof EntitySnowball) && !(entity instanceof EntityEgg));

                    if (entity.getDistanceToEntity(this.blinkEntity.getPlayer()) <= (Float) this.range.getValue().floatValue()) {
                        this.should = true;
                        break;
                    }
                }
            }

            if (this.should) {
                BlinkComponent.releaseStoredPackets(2);
                this.should = false;
            }
        }

    }


    @EventTarget
    public void onPacketSend(EventPacketSend event) {
        BlinkComponent.start(true);
    }

    @EventTarget
    public void onPacketReceive(EventPacketReceive event) {
        Packet<?> packet = event.getPacket();
        if (PacketUtil.isServerPacket(packet)) {
            PacketComponent.incomingClientPackets.add(packet);
            event.setCancelled();
        }
    }


    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @EventTarget
    public void onRender3D(EventRender3D event) {
        if ((Boolean) this.chams.getValue() && this.blinkEntity != null && !RenderUtil.renderPlayerModel(this.blinkEntity, this.boxColor.getColorC(), this.outlineChams.getValue() ? this.outlineColor.getColorC() : null, -1)) {
            this.blinkEntity = null;
        }
    }

    public void updateEntityPosition() {
        this.updateEntityPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
    }

    public void updateEntityPosition(double x, double y, double z, float yaw, float pitch) {
        if (this.blinkEntity != null) {
            this.blinkEntity.setX(x);
            this.blinkEntity.setY(y);
            this.blinkEntity.setZ(z);
            this.blinkEntity.getModel().getPlayer().setPosition(x, y, z);
            this.blinkEntity.getModel().setYaw(yaw);
            this.blinkEntity.getModel().setPitch(pitch);
            if (this.position != null && !this.position.isEmpty()) {
            }
        }

    }

    public void restart() {
        BlinkComponent.stop();
        this.updateEntityPosition();
        this.position.clear();
        BlinkComponent.start(true);
    }

    public void reset() {
        if (!this.position.isEmpty()) {
            this.position.clear();
        }

    }
}

