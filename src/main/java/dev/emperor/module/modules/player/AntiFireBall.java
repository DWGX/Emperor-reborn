package dev.emperor.module.modules.player;

import dev.emperor.event.EventTarget;
import dev.emperor.event.world.EventMotion;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.values.BoolValue;
import dev.emperor.module.values.NumberValue;
import dev.emperor.utils.RotationComponent;
import dev.emperor.utils.client.MathUtil;
import dev.emperor.utils.client.PacketUtil;
import dev.emperor.utils.client.TimeUtil;
import dev.emperor.utils.player.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;

public class AntiFireBall
extends Module {
    private final BoolValue postValue = new BoolValue("Post", false);
    private final BoolValue sendPostC0FFix = new BoolValue("SendPostC0FFix", true);
    private final BoolValue rotation = new BoolValue("Rotation", true);
    private final NumberValue minRotationSpeed = new NumberValue("MinRotationSpeed", 10.0, 0.0, 10.0, 1.0);
    private final NumberValue maxRotationSpeed = new NumberValue("MaxRotationSpeed", 10.0, 0.0, 10.0, 1.0);
    private final BoolValue moveFix = new BoolValue("MoveFix", false);
    private final BoolValue noSwing = new BoolValue("NoSwing", false);
    private final TimeUtil timerUtil = new TimeUtil();

    public AntiFireBall() {
        super("AntiFireBall", Category.Player);
    }

    @EventTarget
    public void onMotion(EventMotion event) {
        if (((Boolean)this.postValue.getValue()).booleanValue() && event.isPost() || !((Boolean)this.postValue.getValue()).booleanValue() && event.isPre()) {
            for (Entity entity : AntiFireBall.mc.theWorld.loadedEntityList) {
                if (!(entity instanceof EntityFireball) || !(AntiFireBall.mc.thePlayer.getDistanceToEntity(entity) < 5.5f) || !this.timerUtil.delay(300.0f)) continue;
                this.timerUtil.reset();
                if (((Boolean)this.rotation.getValue()).booleanValue()) {
                    RotationComponent.setRotations(RotationUtil.getRotationsNonLivingEntity(entity), MathUtil.getRandom(((Double)this.minRotationSpeed.getValue()).intValue(), ((Double)this.maxRotationSpeed.getValue()).intValue()), true);
                }
                if (((Boolean)this.sendPostC0FFix.getValue()).booleanValue() && ((Boolean)this.postValue.getValue()).booleanValue()) {
                    PacketUtil.sendPacketC0F();
                }
                PacketUtil.send(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
                if (((Boolean)this.noSwing.getValue()).booleanValue()) {
                    if (((Boolean)this.sendPostC0FFix.getValue()).booleanValue() && ((Boolean)this.postValue.getValue()).booleanValue()) {
                        PacketUtil.sendPacketC0F();
                    }
                    PacketUtil.send(new C0APacketAnimation());
                    continue;
                }
                if (((Boolean)this.sendPostC0FFix.getValue()).booleanValue() && ((Boolean)this.postValue.getValue()).booleanValue()) {
                    PacketUtil.sendPacketC0F();
                }
                AntiFireBall.mc.thePlayer.swingItem();
            }
        }
    }
}

