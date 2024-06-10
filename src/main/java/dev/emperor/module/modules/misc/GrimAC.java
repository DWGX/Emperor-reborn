package dev.emperor.module.modules.misc;

import dev.emperor.event.EventTarget;
import dev.emperor.event.world.EventPacketReceive;
import dev.emperor.event.world.EventWorldLoad;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.values.BoolValue;
import dev.emperor.utils.DebugUtil;
import java.text.DecimalFormat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.util.EnumChatFormatting;

public class GrimAC
extends Module {
    public BoolValue reachValue = new BoolValue("Reach", true);
    public BoolValue noslowAValue = new BoolValue("NoSlowA", true);
    public static final DecimalFormat DF_1 = new DecimalFormat("0.000000");
    int vl;

    public GrimAC() {
        super("GrimAC", Category.Misc);
    }

    @Override
    public void onEnable() {
        this.vl = 0;
    }

    @EventTarget
    public void onWorld(EventWorldLoad event) {
        this.vl = 0;
    }

    @EventTarget
    public void onPacket(EventPacketReceive event) {
        if (GrimAC.mc.thePlayer.ticksExisted % 6 == 0) {
            S19PacketEntityStatus s19;
            if (event.getPacket() instanceof S19PacketEntityStatus && this.reachValue.getValue().booleanValue() && (s19 = (S19PacketEntityStatus)event.getPacket()).getOpCode() == 2) {
                new Thread(() -> this.checkCombatHurt(s19.getEntity(GrimAC.mc.theWorld))).start();
            }
            if (event.getPacket() instanceof S14PacketEntity packet && this.noslowAValue.getValue().booleanValue()) {
                Entity entity = packet.getEntity(GrimAC.mc.theWorld);
                if (!(entity instanceof EntityPlayer)) {
                    return;
                }
                new Thread(() -> this.checkPlayer((EntityPlayer)entity)).start();
            }
        }
    }

    private void checkCombatHurt(Entity entity) {
        if (!(entity instanceof EntityLivingBase)) {
            return;
        }
        Entity attacker = null;
        int attackerCount = 0;
        for (Entity worldEntity : GrimAC.mc.theWorld.getLoadedEntityList()) {
            if (!(worldEntity instanceof EntityPlayer) || worldEntity.getDistanceToEntity(entity) > 7.0f || ((Object)worldEntity).equals(entity)) continue;
            ++attackerCount;
            attacker = worldEntity;
        }
        if (attacker == null || attacker.equals(entity) || Teams.isSameTeam(attacker)) {
            return;
        }
        double reach = attacker.getDistanceToEntity(entity);
        String prefix = EnumChatFormatting.GRAY + "[" + EnumChatFormatting.AQUA + "GrimAC" + EnumChatFormatting.GRAY + "] " + EnumChatFormatting.RESET + EnumChatFormatting.GRAY + attacker.getName() + EnumChatFormatting.WHITE + " failed ";
        if (reach > 3.0) {
            DebugUtil.log(prefix + EnumChatFormatting.AQUA + "Reach" + EnumChatFormatting.WHITE + " (vl:" + attackerCount + ".0)" + EnumChatFormatting.GRAY + ": " + DF_1.format(reach) + " blocks");
        }
    }

    private void checkPlayer(EntityPlayer player) {
        if (player.equals(GrimAC.mc.thePlayer) || Teams.isSameTeam(player)) {
            return;
        }
        String prefix = EnumChatFormatting.GRAY + "[" + EnumChatFormatting.AQUA + "GrimAC" + EnumChatFormatting.GRAY + "] " + EnumChatFormatting.RESET + EnumChatFormatting.GRAY + player.getName() + EnumChatFormatting.WHITE + " failed ";
        if (player.isUsingItem() && (player.posX - player.lastTickPosX > 0.2 || player.posZ - player.lastTickPosZ > 0.2)) {
            DebugUtil.log(prefix + EnumChatFormatting.AQUA + "NoSlowA (Prediction)" + EnumChatFormatting.WHITE + " (vl:" + this.vl + ".0)");
            ++this.vl;
        }
        if (!GrimAC.mc.theWorld.loadedEntityList.contains(player) || !player.isEntityAlive()) {
            this.vl = 0;
        }
    }
}

