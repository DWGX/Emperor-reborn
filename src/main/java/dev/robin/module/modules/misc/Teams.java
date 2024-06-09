package dev.robin.module.modules.misc;

import dev.robin.Client;
import dev.robin.module.Category;
import dev.robin.module.Module;
import dev.robin.module.values.BoolValue;
import dev.robin.utils.player.PlayerUtil;
import java.util.Objects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class Teams
extends Module {
    private static final BoolValue armorValue = new BoolValue("ArmorColor", true);
    private static final BoolValue colorValue = new BoolValue("Color", true);
    private static final BoolValue scoreboardValue = new BoolValue("ScoreboardTeam", true);

    public Teams() {
        super("Teams", Category.Misc);
    }

    public static boolean isSameTeam(Entity entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer)entity;
            if (Objects.requireNonNull(Client.instance.moduleManager.getModule("Teams")).getState()) {
                return (Boolean)armorValue.getValue() != false && PlayerUtil.armorTeam(entityPlayer) || (Boolean)colorValue.getValue() != false && PlayerUtil.colorTeam(entityPlayer) || (Boolean)scoreboardValue.getValue() != false && PlayerUtil.scoreTeam(entityPlayer);
            }
            return false;
        }
        return false;
    }
}

