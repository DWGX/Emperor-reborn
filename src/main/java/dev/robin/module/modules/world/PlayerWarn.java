package dev.robin.module.modules.world;

import dev.robin.event.EventTarget;
import dev.robin.event.world.EventTick;
import dev.robin.event.world.EventWorldLoad;
import dev.robin.gui.notification.NotificationManager;
import dev.robin.gui.notification.NotificationType;
import dev.robin.module.Category;
import dev.robin.module.Module;
import dev.robin.module.modules.misc.Teams;
import dev.robin.utils.DebugUtil;
import dev.robin.utils.HYTUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerWarn
extends Module {
    public static List<Entity> flaggedEntity = new ArrayList<Entity>();

    public PlayerWarn() {
        super("PlayerTracker", Category.World);
    }

    @EventTarget
    public void onWorld(EventWorldLoad e) {
        flaggedEntity.clear();
    }

    @EventTarget
    public void onTick(EventTick e) {
        if (PlayerWarn.mc.theWorld == null || PlayerWarn.mc.theWorld.loadedEntityList.isEmpty()) {
            return;
        }
        if (HYTUtils.isInLobby()) {
            return;
        }
        if (PlayerWarn.mc.thePlayer.ticksExisted % 6 == 0) {
            for (Entity ent : PlayerWarn.mc.theWorld.loadedEntityList) {
                if (!(ent instanceof EntityPlayer) || ent == PlayerWarn.mc.thePlayer) continue;
                EntityPlayer player = (EntityPlayer)ent;
                if (HYTUtils.isStrength(player) > 0 && !flaggedEntity.contains(player) && !Teams.isSameTeam(player)) {
                    flaggedEntity.add(player);
                    DebugUtil.log("\u6709\u65b0\u7684\u50bb\u903c", (Object)(player.getName() + " \u662f\u50bb\u903c\u529b\u91cf\u72d7\uff0c\u5feb\u53bb\u65a9\u6740\u4ed6\u7684\u4eb2\u5988\u5427"));
                    NotificationManager.post(NotificationType.WARNING, "\u6709\u65b0\u7684\u50bb\u903c", player.getName() + " \u662f\u50bb\u903c\u529b\u91cf\u72d7\uff0c\u5feb\u53bb\u65a9\u6740\u4ed6\u7684\u4eb2\u5988\u5427", 20.0f);
                }
                if (HYTUtils.isRegen(player) > 0 && !flaggedEntity.contains(player) && !Teams.isSameTeam(player)) {
                    flaggedEntity.add(player);
                    DebugUtil.log("\u6709\u65b0\u7684\u50bb\u903c", (Object)(player.getName() + " \u4f7f\u7528\u4ed6\u7684\u6062\u590d\uff0c\u590d\u6d3b\u4ed6\u4eb2\u5988\uff0c\u5feb\u53bb\u65a9\u6740\u4ed6\u7684\u4eb2\u5988\u5427"));
                    NotificationManager.post(NotificationType.WARNING, "\u6709\u65b0\u7684\u50bb\u903c", player.getName() + " \u662f\u50bb\u903c\u751f\u547d\u6062\u590d\u72d7\uff0c\u5feb\u53bb\u65a9\u6740\u4ed6\u7684\u4eb2\u5988\u5427", 20.0f);
                }
                if (HYTUtils.isHoldingGodAxe(player) && !flaggedEntity.contains(player) && !Teams.isSameTeam(player)) {
                    flaggedEntity.add(player);
                    DebugUtil.log("\u6709\u65b0\u7684\u50bb\u903c", (Object)(player.getName() + " \u6b63\u5728\u4f7f\u7528\u79d2\u4eba\u65a7\u51fb\u6740\u4ed6\u7684\u6bcd\u4eb2\uff0c\u4e3b\u64ad\u5c0f\u5fc3\u70b9\u522b\u88ab\u6740\u4e86"));
                    NotificationManager.post(NotificationType.WARNING, "\u6709\u65b0\u7684\u50bb\u903c", player.getName() + " \u6b63\u5728\u4f7f\u7528\u79d2\u4eba\u65a7\u51fb\u6740\u4ed6\u7684\u6bcd\u4eb2\uff0c\u4e3b\u64ad\u5c0f\u5fc3\u70b9\u522b\u88ab\u6740\u4e86", 20.0f);
                }
                if (HYTUtils.isKBBall(player.getHeldItem()) && !flaggedEntity.contains(player) && !Teams.isSameTeam(player)) {
                    flaggedEntity.add(player);
                    DebugUtil.log("\u6709\u65b0\u7684\u50bb\u903c", (Object)(player.getName() + " \u6b63\u5728\u4f7f\u7528\u51fb\u9000\u7403\u628a\u4ed6\u7684\u8001\u6bcd\u6253\u5230\u5341\u91cc\u5f00\u5916\uff0c\u4e3b\u64ad\u5c0f\u5fc3\u70b9"));
                    NotificationManager.post(NotificationType.WARNING, "\u6709\u65b0\u7684\u50bb\u903c", player.getName() + " \u6b63\u5728\u4f7f\u7528\u51fb\u9000\u7403\u628a\u4ed6\u7684\u8001\u6bcd\u6253\u5230\u5341\u91cc\u5f00\u5916\uff0c\u4e3b\u64ad\u5c0f\u5fc3\u70b9", 20.0f);
                }
                if (HYTUtils.hasEatenGoldenApple(player) <= 0 || flaggedEntity.contains(player) || Teams.isSameTeam(player)) continue;
                flaggedEntity.add(player);
                DebugUtil.log("\u6709\u65b0\u7684\u50bb\u903c", (Object)(player.getName() + " \u5403\u4e86\u9644\u9b54\u91d1\u82f9\u679c\u6362\u53d6\u9501\u8840\u548c\u4ed6\u7684\u8001\u5988\u5b50\u51b3\u6597\uff0c\u4e3b\u64ad\u7b49\u4ed6\u9501\u8840\u6ca1\u4e86\u518d\u53bb\u6bb4\u6253\u4ed6\u5427"));
                NotificationManager.post(NotificationType.WARNING, "\u6709\u65b0\u7684\u50bb\u903c", player.getName() + " \u5403\u4e86\u9644\u9b54\u91d1\u82f9\u679c\u6362\u53d6\u9501\u8840\u548c\u4ed6\u7684\u8001\u5988\u5b50\u51b3\u6597\uff0c\u4e3b\u64ad\u7b49\u4ed6\u9501\u8840\u6ca1\u4e86\u518d\u53bb\u6bb4\u6253\u4ed6\u5427", 20.0f);
            }
        }
    }
}

