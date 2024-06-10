package dev.emperor.module.modules.combat;

import dev.emperor.event.EventTarget;
import dev.emperor.event.attack.EventAttack;
import dev.emperor.event.rendering.EventRender3D;
import dev.emperor.event.world.EventPacketReceive;
import dev.emperor.event.world.EventTick;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.modules.render.HUD;
import dev.emperor.module.values.BoolValue;
import dev.emperor.module.values.NumberValue;
import dev.emperor.utils.render.RenderUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.util.Vec3;

public class BackTrack
extends Module {
    public static EntityLivingBase target;
    private final NumberValue amount = new NumberValue("Amount", 1.0, 1.0, 3.0, 0.1);
    private final NumberValue range = new NumberValue("Range", 2.0, 2.0, 8.0, 0.1);
    private final NumberValue interval = new NumberValue("IntervalTick", 1.0, 0.0, 10.0, 1.0);
    private final BoolValue esp = new BoolValue("Esp", false);
    private Vec3 realTargetPosition = new Vec3(0.0, 0.0, 0.0);
    public static double realX;
    public static double realY;
    public static double realZ;
    int tick = 0;

    public BackTrack() {
        super("BackTrack", Category.Combat);
    }

    @EventTarget
    public void onAttack(EventAttack e) {
        target = (EntityLivingBase)e.getTarget();
    }

    @EventTarget
    public void onTick(EventTick e) {
        if ((double)this.tick <= this.interval.getValue()) {
            ++this.tick;
        }
        if (target != null && (double)BackTrack.mc.thePlayer.getDistanceToEntity(target) <= this.range.getValue()) {
            Vec3 vec3 = new Vec3(BackTrack.target.posX, BackTrack.target.posY, BackTrack.target.posZ);
            if (vec3.distanceTo(this.realTargetPosition) < this.amount.getValue() && (double)this.tick > this.interval.getValue()) {
                BackTrack.target.posX = BackTrack.target.lastTickPosX;
                BackTrack.target.posY = BackTrack.target.lastTickPosY;
                BackTrack.target.posZ = BackTrack.target.lastTickPosZ;
                this.tick = 0;
            }
        }
    }

    @EventTarget
    public void onPacketReceive(EventPacketReceive e) {
            S18PacketEntityTeleport s18;
            if (e.getPacket() instanceof S18PacketEntityTeleport && (s18 = (S18PacketEntityTeleport)e.getPacket()).getEntityId() == target.getEntityId()) {
                this.realTargetPosition = new Vec3((double)s18.getX() / 32.0, (double)s18.getY() / 32.0, (double)s18.getZ() / 32.0);
                realX = (double)s18.getX() / 32.0;
                realY = (double)s18.getY() / 32.0;
                realZ = (double)s18.getZ() / 32.0;
            }
    }

    @EventTarget
    public void onRender3D(EventRender3D event) {
        if (this.esp.getValue().booleanValue() && KillAura.target != null) {
            RenderUtil.renderBoundingBox(target, HUD.color(2), 60.0f);
            RenderUtil.resetColor();
        }
    }

    private void render(EntityLivingBase entity) {
        float red = 0.0f;
        float green = 1.1333333f;
        float blue = 0.0f;
        float lineWidth = 3.0f;
        float alpha = 0.03137255f;
        if (BackTrack.mc.thePlayer.getDistanceToEntity(entity) > 1.0f) {
            double d0 = 1.0f - BackTrack.mc.thePlayer.getDistanceToEntity(entity) / 20.0f;
            if (d0 < 0.3) {
                d0 = 0.3;
            }
            lineWidth *= (float)d0;
        }
        RenderUtil.drawEntityServerESP(entity, 0.0f, 1.1333333f, 0.0f, 0.03137255f, 1.0f, lineWidth);
    }

    @Override
    public void onDisable() {
        target = null;
        this.tick = 0;
    }
}

