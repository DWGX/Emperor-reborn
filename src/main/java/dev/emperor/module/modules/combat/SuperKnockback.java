package dev.emperor.module.modules.combat;

import dev.emperor.event.EventTarget;
import dev.emperor.event.attack.EventAttack;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.values.BoolValue;
import dev.emperor.module.values.ModeValue;
import dev.emperor.utils.player.MoveUtil;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class SuperKnockback
extends Module {
    private final ModeValue<KnockBackMode> modeValue = new ModeValue("Mode", KnockBackMode.values(), KnockBackMode.Vanilla);
    private final BoolValue onlyMoveValue = new BoolValue("OnlyMove", true);
    private final BoolValue onlyGroundValue = new BoolValue("OnlyGround", false);

    public SuperKnockback() {
        super("SuperKnockback", Category.Combat);
    }

    @EventTarget
    public void onAttack(EventAttack event) {
        if (!MoveUtil.isMoving() && this.onlyMoveValue.getValue().booleanValue() || !SuperKnockback.mc.thePlayer.onGround && this.onlyGroundValue.getValue().booleanValue()) {
            return;
        }
        switch (this.modeValue.getValue()) {
            case Vanilla: {
                if (SuperKnockback.mc.thePlayer.isSprinting()) {
                    mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(SuperKnockback.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                }
                mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(SuperKnockback.mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(SuperKnockback.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(SuperKnockback.mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(SuperKnockback.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(SuperKnockback.mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                SuperKnockback.mc.thePlayer.setSprinting(true);
                SuperKnockback.mc.thePlayer.serverSprintState = true;
                break;
            }
            case SneakPacket: {
                if (SuperKnockback.mc.thePlayer.isSprinting()) {
                    SuperKnockback.mc.thePlayer.setSprinting(true);
                }
                mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(SuperKnockback.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(SuperKnockback.mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(SuperKnockback.mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(SuperKnockback.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
                SuperKnockback.mc.thePlayer.serverSprintState = true;
                break;
            }
            case ExtraPacket: {
                if (SuperKnockback.mc.thePlayer.isSprinting()) {
                    SuperKnockback.mc.thePlayer.setSprinting(true);
                }
                mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(SuperKnockback.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(SuperKnockback.mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(SuperKnockback.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(SuperKnockback.mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                SuperKnockback.mc.thePlayer.setSprinting(true);
                SuperKnockback.mc.thePlayer.serverSprintState = true;
            }
        }
    }

    public enum KnockBackMode {
        Vanilla,
        SneakPacket,
        ExtraPacket

    }
}

