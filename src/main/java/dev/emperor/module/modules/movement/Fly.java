package dev.emperor.module.modules.movement;

import dev.emperor.event.EventTarget;
import dev.emperor.event.misc.EventTeleport;
import dev.emperor.event.rendering.EventRender3D;
import dev.emperor.event.world.EventPacketSend;
import dev.emperor.event.world.EventStrafe;
import dev.emperor.event.world.EventUpdate;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.values.ModeValue;
import dev.emperor.utils.BlinkUtils;
import dev.emperor.utils.DebugUtil;
import dev.emperor.utils.client.PacketUtil;
import dev.emperor.utils.client.TimeUtil;
import dev.emperor.utils.player.MoveUtil;
import dev.emperor.utils.render.RenderUtil;
import java.awt.Color;
import java.util.LinkedList;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

public class Fly
extends Module {
    private final ModeValue<flyModes> flyMode = new ModeValue("FlyMode", flyModes.values(), flyModes.Vanilla);
    private boolean started;
    private boolean notUnder;
    private boolean clipped;
    private boolean teleport;
    private final LinkedList<double[]> positions = new LinkedList();
    private final TimeUtil pulseTimer = new TimeUtil();

    public Fly() {
        super("Fly", Category.Movement);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onDisable() {
        Fly.mc.timer.timerSpeed = 1.0f;
        if (this.flyMode.getValue() == flyModes.GrimGhostBlock) {
            LinkedList<double[]> linkedList = this.positions;
            synchronized (linkedList) {
                this.positions.clear();
            }
            if (Fly.mc.thePlayer == null) {
                return;
            }
            BlinkUtils.setBlinkState(true, true, false, false, false, false, false, false, false, false, false);
            BlinkUtils.clearPacket(null, false, -1);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onEnable() {
        if (this.flyMode.getValue() == flyModes.DoMCer) {
            DebugUtil.log("\u9700\u8981\u9876\u5934");
            this.notUnder = false;
            this.started = false;
            this.clipped = false;
            this.teleport = false;
        }
        if (this.flyMode.getValue() == flyModes.GrimGhostBlock) {
            if (Fly.mc.thePlayer == null) {
                return;
            }
            BlinkUtils.setBlinkState(false, false, true, false, false, false, false, false, false, false, false);
            LinkedList<double[]> linkedList = this.positions;
            synchronized (linkedList) {
                this.positions.add(new double[]{Fly.mc.thePlayer.posX, Fly.mc.thePlayer.getEntityBoundingBox().minY + (double)(Fly.mc.thePlayer.getEyeHeight() / 2.0f), Fly.mc.thePlayer.posZ});
                this.positions.add(new double[]{Fly.mc.thePlayer.posX, Fly.mc.thePlayer.getEntityBoundingBox().minY, Fly.mc.thePlayer.posZ});
            }
            this.pulseTimer.reset();
        }
    }

    @EventTarget
    public void onPacketSend(EventPacketSend event) {
        if (this.flyMode.getValue() == flyModes.GrimGhostBlock && (event.getPacket() instanceof C16PacketClientStatus || event.getPacket() instanceof C00PacketKeepAlive)) {
            event.setCancelled();
        }
    }

    @EventTarget
    public void onTP(EventTeleport event) {
        if (this.teleport) {
            event.setCancelled(true);
            this.teleport = false;
            DebugUtil.log("Teleported");
            this.toggle();
        }
    }

    @EventTarget
    public void onStrafe(EventStrafe event) {
        block7: {
            block9: {
                block8: {
                    if (!this.flyMode.getValue().equals(flyModes.DoMCer)) break block7;
                    AxisAlignedBB bb = Fly.mc.thePlayer.getEntityBoundingBox().offset(0.0, 1.0, 0.0);
                    if (!Fly.mc.theWorld.getCollidingBoundingBoxes(Fly.mc.thePlayer, bb).isEmpty() && !this.started) break block8;
                    switch (Fly.mc.thePlayer.offGroundTicks) {
                        case 0: {
                            if (this.notUnder && this.clipped) {
                                this.started = true;
                                event.setSpeed(10.0);
                                Fly.mc.thePlayer.motionY = 0.42f;
                                this.notUnder = false;
                                break;
                            }
                            break block9;
                        }
                        case 1: {
                            if (this.started) {
                                event.setSpeed(9.6);
                                break;
                            }
                            break block9;
                        }
                    }
                    break block9;
                }
                this.notUnder = true;
                if (this.clipped) {
                    return;
                }
                this.clipped = true;
                PacketUtil.send(new C03PacketPlayer.C06PacketPlayerPosLook(Fly.mc.thePlayer.posX, Fly.mc.thePlayer.posY, Fly.mc.thePlayer.posZ, Fly.mc.thePlayer.rotationYaw, Fly.mc.thePlayer.rotationPitch, false));
                PacketUtil.send(new C03PacketPlayer.C06PacketPlayerPosLook(Fly.mc.thePlayer.posX, Fly.mc.thePlayer.posY - 0.1, Fly.mc.thePlayer.posZ, Fly.mc.thePlayer.rotationYaw, Fly.mc.thePlayer.rotationPitch, false));
                PacketUtil.send(new C03PacketPlayer.C06PacketPlayerPosLook(Fly.mc.thePlayer.posX, Fly.mc.thePlayer.posY, Fly.mc.thePlayer.posZ, Fly.mc.thePlayer.rotationYaw, Fly.mc.thePlayer.rotationPitch, false));
                this.teleport = true;
            }
            MoveUtil.strafe();
            Fly.mc.timer.timerSpeed = 0.4f;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (this.flyMode.getValue() == flyModes.GrimGhostBlock) {
            this.setSuffix("GhostBlock-Blink:" + BlinkUtils.bufferSize(null));
            LinkedList<double[]> linkedList = this.positions;
            synchronized (linkedList) {
                this.positions.add(new double[]{Fly.mc.thePlayer.posX, Fly.mc.thePlayer.getEntityBoundingBox().minY, Fly.mc.thePlayer.posZ});
            }
            if (Fly.mc.thePlayer.ticksExisted % 2 == 1) {
                PacketUtil.sendPacketC0F(true);
            }
            if (this.pulseTimer.hasReached(2900.0)) {
                linkedList = this.positions;
                synchronized (linkedList) {
                    this.positions.clear();
                }
                BlinkUtils.releasePacket(null, false, -1, 0);
                this.pulseTimer.reset();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @EventTarget
    public void onRender3D(EventRender3D event) {
        if (this.flyMode.getValue() == flyModes.GrimGhostBlock) {
            LinkedList<double[]> linkedList = this.positions;
            synchronized (linkedList) {
                GL11.glPushMatrix();
                GL11.glDisable(3553);
                GL11.glBlendFunc(770, 771);
                GL11.glEnable(2848);
                GL11.glEnable(3042);
                GL11.glDisable(2929);
                Fly.mc.entityRenderer.disableLightmap();
                GL11.glLineWidth(2.0f);
                GL11.glBegin(3);
                RenderUtil.glColor(new Color(68, 131, 123, 255).getRGB());
                double renderPosX = Fly.mc.getRenderManager().viewerPosX;
                double renderPosY = Fly.mc.getRenderManager().viewerPosY;
                double renderPosZ = Fly.mc.getRenderManager().viewerPosZ;
                for (double[] pos : this.positions) {
                    GL11.glVertex3d(pos[0] - renderPosX, pos[1] - renderPosY, pos[2] - renderPosZ);
                }
                GL11.glColor4d(1.0, 1.0, 1.0, 1.0);
                GL11.glEnd();
                GL11.glEnable(2929);
                GL11.glDisable(2848);
                GL11.glDisable(3042);
                GL11.glEnable(3553);
                GL11.glPopMatrix();
            }
        }
    }

    public enum flyModes {
        Vanilla,
        DoMCer,
        GrimGhostBlock

    }
}

