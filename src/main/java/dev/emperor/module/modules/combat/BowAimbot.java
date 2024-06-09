package dev.emperor.module.modules.combat;

import dev.emperor.event.EventTarget;
import dev.emperor.event.world.EventMotion;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.modules.misc.Teams;
import dev.emperor.module.values.ModeValue;
import dev.emperor.module.values.NumberValue;
import dev.emperor.utils.RotationComponent;
import dev.emperor.utils.player.RotationUtil;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import org.lwjgl.compatibility.util.vector.Vector2f;

public class BowAimbot
extends Module {
    public static EntityLivingBase target;
    public static NumberValue fov;
    public static NumberValue range;
    public static ModeValue<PRIORITY> priority;

    public BowAimbot() {
        super("BowAimbot", Category.Combat);
    }

    @EventTarget
    public void onUpdatePre(EventMotion event) {
        if (event.isPost()) {
            return;
        }
        if (BowAimbot.mc.thePlayer.inventory.getCurrentItem().getItem() != Items.bow || !BowAimbot.mc.thePlayer.isUsingItem()) {
            target = null;
            return;
        }
        target = this.getTarget();
        if (target == null) {
            return;
        }
        float[] rotation = this.getPlayerRotations(target);
        RotationComponent.setRotations(new Vector2f(rotation[0], rotation[1]), 10.0f, true);
    }

    @Override
    public void onDisable() {
        target = null;
        super.onDisable();
    }

    private float[] getPlayerRotations(Entity entity) {
        double distanceToEnt = BowAimbot.mc.thePlayer.getDistanceToEntity(entity);
        double predictX = entity.posX + (entity.posX - entity.lastTickPosX) * (distanceToEnt * 0.8);
        double predictZ = entity.posZ + (entity.posZ - entity.lastTickPosZ) * (distanceToEnt * 0.8);
        double x2 = predictX - BowAimbot.mc.thePlayer.posX;
        double z = predictZ - BowAimbot.mc.thePlayer.posZ;
        double h = entity.posY + 1.0 - (BowAimbot.mc.thePlayer.posY + (double)BowAimbot.mc.thePlayer.getEyeHeight());
        double h1 = Math.sqrt(x2 * x2 + z * z);
        float yaw = (float)(Math.atan2(z, x2) * 180.0 / Math.PI) - 90.0f;
        float pitch = -RotationUtil.getTrajAngleSolutionLow((float)h1, (float)h, 1.0f);
        return new float[]{yaw, pitch};
    }

    public static boolean isFovInRange(Entity entity, float fov) {
        fov = (float)((double)fov * 0.5);
        double v = ((double)(BowAimbot.mc.thePlayer.rotationYaw - KillAura.getPlayerRotation(entity)) % 360.0 + 540.0) % 360.0 - 180.0;
        return v > 0.0 && v < (double)fov || (double)(-fov) < v && v < 0.0;
    }

    private EntityLivingBase getTarget() {
        Stream<EntityPlayer> stream = BowAimbot.mc.theWorld.playerEntities.stream().filter(e -> !Teams.isSameTeam(e)).filter(e -> !AntiBot.isServerBot(e)).filter(BowAimbot.mc.thePlayer::canEntityBeSeen).filter(e -> BowAimbot.isFovInRange(e, fov.getValue().floatValue()));
        if (((PRIORITY)((Object)priority.getValue())).name().equals("Range")) {
            stream = stream.sorted(Comparator.comparingDouble(e -> e.getDistanceToEntity(BowAimbot.mc.thePlayer)));
        } else if (((PRIORITY)((Object)priority.getValue())).name().equals("Angle")) {
            stream = stream.sorted(Comparator.comparingDouble(RotationUtil::getBowRot));
        }
        List list = stream.collect(Collectors.toList());
        if (list.size() <= 0) {
            return null;
        }
        return (EntityLivingBase)list.get(0);
    }

    static {
        fov = new NumberValue("FoV", 180.0, 10.0, 360.0, 10.0);
        range = new NumberValue("Range", 100.0, 1.0, 200.0, 10.0);
        priority = new ModeValue("Priority", (Enum[])PRIORITY.values(), (Enum)PRIORITY.Angle);
    }

    private static enum PRIORITY {
        Angle,
        Range;

    }
}

