package dev.robin.module.modules.combat;

import dev.robin.event.EventTarget;
import dev.robin.event.attack.EventAttack;
import dev.robin.module.Category;
import dev.robin.module.Module;
import dev.robin.module.values.BoolValue;
import dev.robin.module.values.ModeValue;
import dev.robin.utils.player.MoveUtil;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public class SuperKnockback
extends Module {
    private final ModeValue<KnockBackMode> modeValue = new ModeValue("Mode", (Enum[])KnockBackMode.values(), (Enum)KnockBackMode.Vanilla);
    private final BoolValue onlyMoveValue = new BoolValue("OnlyMove", true);
    private final BoolValue onlyGroundValue = new BoolValue("OnlyGround", false);

    public SuperKnockback() {
        super("SuperKnockback", Category.Combat);
    }

    @EventTarget
    public void onAttack(EventAttack event) {
        if (!MoveUtil.isMoving() && ((Boolean)this.onlyMoveValue.getValue()).booleanValue() || !SuperKnockback.mc.thePlayer.onGround && ((Boolean)this.onlyGroundValue.getValue()).booleanValue()) {
            return;
        }
        switch ((KnockBackMode)((Object)this.modeValue.getValue())) {
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

    public static enum KnockBackMode {
        Vanilla,
        SneakPacket,
        ExtraPacket;

    }
}

