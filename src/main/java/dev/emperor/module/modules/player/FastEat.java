package dev.emperor.module.modules.player;

import dev.emperor.event.EventTarget;
import dev.emperor.event.world.EventMove;
import dev.emperor.event.world.EventUpdate;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.values.BoolValue;
import dev.emperor.module.values.ModeValue;
import dev.emperor.module.values.NumberValue;
import dev.emperor.utils.client.TimeUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;

public final class FastEat
extends Module {
    public final ModeValue<eatModes> modeValue = new ModeValue("Mode", eatModes.values(), eatModes.Grim);
    private final BoolValue noMoveValue = new BoolValue("NoMove", false);
    private final NumberValue delayValue = new NumberValue("CustomDelay", 0.0, 0.0, 300.0, 1.0);
    private final NumberValue customSpeedValue = new NumberValue("CustomSpeed", 2.0, 1.0, 35.0, 1.0);
    private final NumberValue customTimer = new NumberValue("CustomTimer", 1.1, 0.5, 2.0, 0.1);
    private final TimeUtil msTimer = new TimeUtil();
    private boolean usedTimer = false;
    public boolean grimEat;

    public FastEat() {
        super("FastEat", Category.Player);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (FastEat.mc.thePlayer == null) {
            return;
        }
        if (this.usedTimer) {
            FastEat.mc.timer.timerSpeed = 1.0f;
            this.usedTimer = false;
        }
        if (!FastEat.mc.thePlayer.isUsingItem()) {
            this.msTimer.reset();
            return;
        }
        Item usingItem = FastEat.mc.thePlayer.getItemInUse().getItem();
        if (usingItem instanceof ItemFood || usingItem instanceof ItemBucketMilk || usingItem instanceof ItemPotion && !(FastEat.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword)) {
            eatModes mode2 = this.modeValue.getValue();
            switch (mode2) {
                case Instant: {
                    for (int i = 0; i < 35; ++i) {
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer(FastEat.mc.thePlayer.onGround));
                    }
                    FastEat.mc.playerController.onStoppedUsingItem(FastEat.mc.thePlayer);
                    break;
                }
                case NCP: {
                    if (FastEat.mc.thePlayer.getItemInUseDuration() <= 14) break;
                    for (int i = 0; i < 20; ++i) {
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer(FastEat.mc.thePlayer.onGround));
                    }
                    FastEat.mc.playerController.onStoppedUsingItem(FastEat.mc.thePlayer);
                    break;
                }
                case AAC: {
                    FastEat.mc.timer.timerSpeed = 1.22f;
                    this.usedTimer = true;
                    break;
                }
                case VulCan: {
                    if (!FastEat.mc.thePlayer.onGround) break;
                    FastEat.mc.timer.timerSpeed = 0.55f;
                    this.usedTimer = true;
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer(FastEat.mc.thePlayer.onGround));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(FastEat.mc.thePlayer.posX, FastEat.mc.thePlayer.posY, FastEat.mc.thePlayer.posZ, FastEat.mc.thePlayer.onGround));
                    break;
                }
                case Grim: {
                    this.usedTimer = true;
                    this.grimEat = true;
                    FastEat.mc.timer.timerSpeed = 0.3f;
                    for (int i = 0; i < 2; ++i) {
                        ++FastEat.mc.thePlayer.positionUpdateTicks;
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer(FastEat.mc.thePlayer.onGround));
                    }
                    this.grimEat = false;
                    break;
                }
                case CustomDelay: {
                    FastEat.mc.timer.timerSpeed = this.customTimer.getValue().floatValue();
                    this.usedTimer = true;
                    if (!this.msTimer.hasPassed(this.delayValue.getValue().longValue())) {
                        return;
                    }
                    for (int i = 0; i < this.customSpeedValue.getValue().intValue(); ++i) {
                        mc.getNetHandler().addToSendQueue(new C03PacketPlayer(FastEat.mc.thePlayer.onGround));
                    }
                    this.msTimer.reset();
                }
            }
        }
    }

    @EventTarget
    public void onMove(EventMove event) {
        if (FastEat.mc.thePlayer == null || event == null) {
            return;
        }
        if (!(this.getState() && FastEat.mc.thePlayer.isUsingItem() && this.noMoveValue.getValue().booleanValue())) {
            return;
        }
        Item usingItem = FastEat.mc.thePlayer.getItemInUse().getItem();
        if (usingItem instanceof ItemFood || usingItem instanceof ItemBucketMilk || usingItem instanceof ItemPotion && !(FastEat.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword)) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onDisable() {
        if (this.usedTimer) {
            FastEat.mc.timer.timerSpeed = 1.0f;
            this.usedTimer = false;
        }
    }

    public enum eatModes {
        Instant,
        NCP,
        Grim,
        VulCan,
        AAC,
        CustomDelay

    }
}

