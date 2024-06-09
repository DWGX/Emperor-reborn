package dev.emperor.module.modules.player;

import dev.emperor.event.EventTarget;
import dev.emperor.event.world.EventUpdate;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.values.BoolValue;
import dev.emperor.module.values.ModeValue;
import dev.emperor.module.values.NumberValue;
import dev.emperor.utils.client.PacketUtil;
import dev.emperor.utils.player.MoveUtil;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.vialoadingbase.ViaLoadingBase;

public class Regen
extends Module {
    private final ModeValue<regenMode> modeValue = new ModeValue("Mode", (Enum[])regenMode.values(), (Enum)regenMode.Vanilla);
    private final NumberValue healthValue = new NumberValue("Health", 18.0, 0.0, 20.0, 1.0);
    private final NumberValue foodValue = new NumberValue("Food", 18.0, 0.0, 20.0, 1.0);
    private final NumberValue speedValue = new NumberValue("Speed", 100.0, 1.0, 100.0, 1.0);
    private final BoolValue noAirValue = new BoolValue("NoAir", false);
    private final BoolValue potionEffectValue = new BoolValue("PotionEffect", false);
    private boolean resetTimer = false;

    public Regen() {
        super("Regen", Category.Player);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        this.setSuffix((Object)this.modeValue.get());
        if (this.resetTimer) {
            Regen.mc.timer.timerSpeed = 1.0f;
        }
        this.resetTimer = false;
        if ((!((Boolean)this.noAirValue.getValue()).booleanValue() || Regen.mc.thePlayer.onGround) && !Regen.mc.thePlayer.capabilities.isCreativeMode && (double)Regen.mc.thePlayer.getFoodStats().getFoodLevel() > (Double)this.foodValue.getValue() && Regen.mc.thePlayer.isEntityAlive() && (double)Regen.mc.thePlayer.getHealth() < (Double)this.healthValue.getValue()) {
            if (((Boolean)this.potionEffectValue.getValue()).booleanValue() && !Regen.mc.thePlayer.isPotionActive(Potion.regeneration)) {
                return;
            }
            switch (this.modeValue.get()) {
                case Vanilla: {
                    int i = 0;
                    while ((double)i < (Double)this.speedValue.getValue()) {
                        PacketUtil.send(new C03PacketPlayer(Regen.mc.thePlayer.onGround));
                        ++i;
                    }
                    break;
                }
                case Grim1_17: {
                    if (ViaLoadingBase.getInstance().getTargetVersion().getVersion() > 755) {
                        return;
                    }
                    int i = 0;
                    while ((double)i < (Double)this.speedValue.getValue()) {
                        PacketUtil.send(new C03PacketPlayer.C06PacketPlayerPosLook(Regen.mc.thePlayer.posX, Regen.mc.thePlayer.posY, Regen.mc.thePlayer.posZ, Regen.mc.thePlayer.rotationYaw, Regen.mc.thePlayer.rotationPitch, Regen.mc.thePlayer.onGround));
                        ++i;
                    }
                    break;
                }
                case AAC4NoFire: {
                    break;
                }
                case NewSpartan: {
                    if (Regen.mc.thePlayer.ticksExisted % 5 == 0) {
                        this.resetTimer = true;
                        Regen.mc.timer.timerSpeed = 0.98f;
                        for (int i = 0; i < 10; ++i) {
                            PacketUtil.send(new C03PacketPlayer(true));
                        }
                        break;
                    }
                    if (!MoveUtil.isMoving()) break;
                    PacketUtil.send(new C03PacketPlayer(Regen.mc.thePlayer.onGround));
                    break;
                }
                case OldSpartan: {
                    if (MoveUtil.isMoving() || !Regen.mc.thePlayer.onGround) {
                        return;
                    }
                    for (int i = 0; i < 9; ++i) {
                        PacketUtil.send(new C03PacketPlayer(Regen.mc.thePlayer.onGround));
                    }
                    Regen.mc.timer.timerSpeed = 0.45f;
                    this.resetTimer = true;
                }
            }
        }
    }

    public static enum regenMode {
        Vanilla,
        Grim1_17,
        OldSpartan,
        NewSpartan,
        AAC4NoFire;

    }
}

