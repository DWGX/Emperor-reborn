package dev.emperor.module.modules.render;

import dev.emperor.event.EventTarget;
import dev.emperor.event.rendering.EventRender3D;
import dev.emperor.event.world.EventTick;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.utils.mobends.AnimatedEntity;
import dev.emperor.utils.mobends.client.renderer.entity.RenderBendsPlayer;
import dev.emperor.utils.mobends.client.renderer.entity.RenderBendsSpider;
import dev.emperor.utils.mobends.client.renderer.entity.RenderBendsZombie;
import dev.emperor.utils.mobends.data.Data_Player;
import dev.emperor.utils.mobends.data.Data_Spider;
import dev.emperor.utils.mobends.data.Data_Zombie;
import dev.emperor.utils.mobends.data.EntityData;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.compatibility.util.vector.ReadableVector3f;
import org.lwjgl.compatibility.util.vector.Vector3f;

public class MoBendsMod
extends Module {
    public static float partialTicks = 0.0f;
    public static float ticks = 0.0f;
    public static float ticksPerFrame = 0.0f;
    public static final ResourceLocation texture_NULL = new ResourceLocation("mobends/textures/white.png");

    public MoBendsMod() {
        super("MoreBends", Category.Render);
    }

    @Override
    public void onEnable() {
        AnimatedEntity.register();
    }

    @EventTarget
    public void onRender3D(EventRender3D e) {
        int i;
        if (MoBendsMod.mc.theWorld == null) {
            return;
        }
        for (i = 0; i < Data_Player.dataList.size(); ++i) {
            Data_Player.dataList.get(i).update(e.getPartialTicks());
        }
        for (i = 0; i < Data_Zombie.dataList.size(); ++i) {
            Data_Zombie.dataList.get(i).update(e.getPartialTicks());
        }
        for (i = 0; i < Data_Spider.dataList.size(); ++i) {
            Data_Spider.dataList.get(i).update(e.getPartialTicks());
        }
        if (MoBendsMod.mc.thePlayer != null) {
            float newTicks = (float)MoBendsMod.mc.thePlayer.ticksExisted + e.getPartialTicks();
            if (!MoBendsMod.mc.theWorld.isRemote || !mc.isGamePaused()) {
                ticksPerFrame = Math.min(Math.max(0.0f, newTicks - ticks), 1.0f);
                ticks = newTicks;
            } else {
                ticksPerFrame = 0.0f;
            }
        }
    }

    @EventTarget
    public void onTick(EventTick event) {
        Entity entity;
        EntityData data;
        int i;
        if (MoBendsMod.mc.theWorld == null) {
            return;
        }
        for (i = 0; i < Data_Player.dataList.size(); ++i) {
            data = Data_Player.dataList.get(i);
            entity = MoBendsMod.mc.theWorld.getEntityByID(data.entityID);
            if (entity != null) {
                if (!data.entityType.equalsIgnoreCase(entity.getName())) {
                    Data_Player.dataList.remove(data);
                    Data_Player.add(new Data_Player(entity.getEntityId()));
                    continue;
                }
                data.motion_prev.set((ReadableVector3f)data.motion);
                data.motion.x = (float)entity.posX - data.position.x;
                data.motion.y = (float)entity.posY - data.position.y;
                data.motion.z = (float)entity.posZ - data.position.z;
                data.position = new Vector3f((float)entity.posX, (float)entity.posY, (float)entity.posZ);
                continue;
            }
            Data_Player.dataList.remove(data);
        }
        for (i = 0; i < Data_Zombie.dataList.size(); ++i) {
            data = Data_Zombie.dataList.get(i);
            entity = MoBendsMod.mc.theWorld.getEntityByID(((Data_Zombie)data).entityID);
            if (entity != null) {
                if (!((Data_Zombie)data).entityType.equalsIgnoreCase(entity.getName())) {
                    Data_Zombie.dataList.remove(data);
                    Data_Zombie.add(new Data_Zombie(entity.getEntityId()));
                    continue;
                }
                ((Data_Zombie)data).motion_prev.set((ReadableVector3f)((Data_Zombie)data).motion);
                ((Data_Zombie)data).motion.x = (float)entity.posX - ((Data_Zombie)data).position.x;
                ((Data_Zombie)data).motion.y = (float)entity.posY - ((Data_Zombie)data).position.y;
                ((Data_Zombie)data).motion.z = (float)entity.posZ - ((Data_Zombie)data).position.z;
                ((Data_Zombie)data).position = new Vector3f((float)entity.posX, (float)entity.posY, (float)entity.posZ);
                continue;
            }
            Data_Zombie.dataList.remove(data);
        }
        for (i = 0; i < Data_Spider.dataList.size(); ++i) {
            data = Data_Spider.dataList.get(i);
            entity = MoBendsMod.mc.theWorld.getEntityByID(((Data_Spider)data).entityID);
            if (entity != null) {
                if (!((Data_Spider)data).entityType.equalsIgnoreCase(entity.getName())) {
                    Data_Spider.dataList.remove(data);
                    Data_Spider.add(new Data_Spider(entity.getEntityId()));
                    continue;
                }
                ((Data_Spider)data).motion_prev.set((ReadableVector3f)((Data_Spider)data).motion);
                ((Data_Spider)data).motion.x = (float)entity.posX - ((Data_Spider)data).position.x;
                ((Data_Spider)data).motion.y = (float)entity.posY - ((Data_Spider)data).position.y;
                ((Data_Spider)data).motion.z = (float)entity.posZ - ((Data_Spider)data).position.z;
                ((Data_Spider)data).position = new Vector3f((float)entity.posX, (float)entity.posY, (float)entity.posZ);
                continue;
            }
            Data_Spider.dataList.remove(data);
        }
    }

    public boolean onRenderLivingEvent(RendererLivingEntity renderer, EntityLivingBase entity, double x2, double y2, double z, float entityYaw, float partialTicks) {
        if (!this.getState() || renderer instanceof RenderBendsPlayer || renderer instanceof RenderBendsZombie || renderer instanceof RenderBendsSpider) {
            return false;
        }
        AnimatedEntity animatedEntity = AnimatedEntity.getByEntity(entity);
        if (animatedEntity != null && (entity instanceof EntityPlayer || entity instanceof EntityZombie) || entity instanceof EntitySpider) {
            if (entity instanceof EntityPlayer) {
                AbstractClientPlayer player = (AbstractClientPlayer)entity;
                AnimatedEntity.getPlayerRenderer(player).doRender(player, x2, y2, z, entityYaw, partialTicks);
            } else if (entity instanceof EntityZombie) {
                EntityZombie zombie = (EntityZombie)entity;
                AnimatedEntity.zombieRenderer.doRender(zombie, x2, y2, z, entityYaw, partialTicks);
            } else {
                EntitySpider spider = (EntitySpider)entity;
                AnimatedEntity.spiderRenderer.doRender(spider, x2, y2, z, entityYaw, partialTicks);
            }
            return true;
        }
        return false;
    }
}

