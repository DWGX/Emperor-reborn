package dev.emperor.module.modules.render;

import dev.emperor.event.EventTarget;
import dev.emperor.event.rendering.EventRender3D;
import dev.emperor.event.world.EventMotion;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.utils.render.RenderUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSnowball;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.compatibility.util.glu.Cylinder;
import org.lwjgl.opengl.GL11;

public class Projectile
extends Module {
    float yaw;
    float pitch;

    public Projectile() {
        super("Projectile", Category.Render);
    }

    @EventTarget
    public void onMotion(EventMotion e) {
        if (e.isPost()) {
            return;
        }
        this.yaw = e.getYaw();
        this.pitch = e.getPitch();
    }

    @EventTarget
    public void onR3D(EventRender3D e) {
        boolean isBow = false;
        float pitchDifference = 0.0f;
        float motionFactor = 1.5f;
        float motionSlowdown = 0.99f;
        if (Projectile.mc.thePlayer.getCurrentEquippedItem() != null) {
            float size;
            float gravity;
            Item heldItem = Projectile.mc.thePlayer.getCurrentEquippedItem().getItem();
            if (heldItem instanceof ItemBow) {
                isBow = true;
                gravity = 0.05f;
                size = 0.3f;
                float power = (float)Projectile.mc.thePlayer.getItemInUseDuration() / 20.0f;
                if ((double)(power = (power * power + power * 2.0f) / 3.0f) < 0.1) {
                    return;
                }
                if (power > 1.0f) {
                    power = 1.0f;
                }
                motionFactor = power * 3.0f;
            } else if (heldItem instanceof ItemFishingRod) {
                gravity = 0.04f;
                size = 0.25f;
                motionSlowdown = 0.92f;
            } else if (ItemPotion.isSplash(Projectile.mc.thePlayer.getCurrentEquippedItem().getMetadata())) {
                gravity = 0.05f;
                size = 0.25f;
                pitchDifference = -20.0f;
                motionFactor = 0.5f;
            } else {
                if (!(heldItem instanceof ItemSnowball || heldItem instanceof ItemEnderPearl || heldItem instanceof ItemEgg || heldItem.equals(Item.getItemById(46)))) {
                    return;
                }
                gravity = 0.03f;
                size = 0.25f;
            }
            double posX = Projectile.mc.getRenderManager().renderPosX - (double)(MathHelper.cos(this.yaw / 180.0f * (float)Math.PI) * 0.16f);
            double posY = mc.getRenderManager().getRenderPosY() + (double)Projectile.mc.thePlayer.getEyeHeight() - (double)0.1f;
            double posZ = mc.getRenderManager().getRenderPosZ() - (double)(MathHelper.sin(this.yaw / 180.0f * (float)Math.PI) * 0.16f);
            double motionX = (double)(-MathHelper.sin(this.yaw / 180.0f * (float)Math.PI) * MathHelper.cos(this.pitch / 180.0f * (float)Math.PI)) * (isBow ? 1.0 : 0.4);
            double motionY = (double)(-MathHelper.sin((this.pitch + pitchDifference) / 180.0f * (float)Math.PI)) * (isBow ? 1.0 : 0.4);
            double motionZ = (double)(MathHelper.cos(this.yaw / 180.0f * (float)Math.PI) * MathHelper.cos(this.pitch / 180.0f * (float)Math.PI)) * (isBow ? 1.0 : 0.4);
            float distance = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
            motionX /= (double)distance;
            motionY /= (double)distance;
            motionZ /= (double)distance;
            motionX *= (double)motionFactor;
            motionY *= (double)motionFactor;
            motionZ *= (double)motionFactor;
            MovingObjectPosition landingPosition = null;
            boolean hasLanded = false;
            boolean hitEntity = false;
            RenderUtil.enableRender3D(true);
            RenderUtil.color(new Color(206, 89, 255, 255).getRGB());
            GL11.glLineWidth((float)2.0f);
            GL11.glBegin((int)3);
            while (!hasLanded && posY > 0.0) {
                Vec3 posBefore = new Vec3(posX, posY, posZ);
                Vec3 posAfter = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
                landingPosition = Projectile.mc.theWorld.rayTraceBlocks(posBefore, posAfter, false, true, false);
                posBefore = new Vec3(posX, posY, posZ);
                posAfter = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
                if (landingPosition != null) {
                    hasLanded = true;
                    posAfter = new Vec3(landingPosition.hitVec.xCoord, landingPosition.hitVec.yCoord, landingPosition.hitVec.zCoord);
                }
                AxisAlignedBB arrowBox = new AxisAlignedBB(posX - (double)size, posY - (double)size, posZ - (double)size, posX + (double)size, posY + (double)size, posZ + (double)size);
                List entityList = this.getEntitiesWithinAABB(arrowBox.addCoord(motionX, motionY, motionZ).expand(1.0, 1.0, 1.0));
                for (int i = 0; i < entityList.size(); ++i) {
                    AxisAlignedBB var2;
                    MovingObjectPosition possibleEntityLanding;
                    Entity var18 = (Entity)entityList.get(i);
                    if (!var18.canBeCollidedWith() || var18 == Projectile.mc.thePlayer || (possibleEntityLanding = (var2 = var18.getEntityBoundingBox().expand(size, size, size)).calculateIntercept(posBefore, posAfter)) == null) continue;
                    hitEntity = true;
                    hasLanded = true;
                    landingPosition = possibleEntityLanding;
                }
                BlockPos var35 = new BlockPos(posX += motionX, posY += motionY, posZ += motionZ);
                Block var36 = Projectile.mc.theWorld.getBlockState(var35).getBlock();
                if (var36.getMaterial() == Material.water) {
                    motionX *= 0.6;
                    motionY *= 0.6;
                    motionZ *= 0.6;
                } else {
                    motionX *= (double)motionSlowdown;
                    motionY *= (double)motionSlowdown;
                    motionZ *= (double)motionSlowdown;
                }
                motionY -= (double)gravity;
                GL11.glVertex3d((double)(posX - mc.getRenderManager().getRenderPosX()), (double)(posY - mc.getRenderManager().getRenderPosY()), (double)(posZ - mc.getRenderManager().getRenderPosZ()));
            }
            GL11.glEnd();
            GL11.glPushMatrix();
            GL11.glTranslated((double)(posX - mc.getRenderManager().getRenderPosX()), (double)(posY - mc.getRenderManager().getRenderPosY()), (double)(posZ - mc.getRenderManager().getRenderPosZ()));
            if (landingPosition != null) {
                int side = landingPosition.sideHit.getIndex();
                if (side == 1 && heldItem instanceof ItemEnderPearl) {
                    RenderUtil.color(new Color(255, 248, 0, 255).getRGB());
                } else if (side == 2) {
                    GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
                } else if (side == 3) {
                    GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
                } else if (side == 4) {
                    GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f);
                } else if (side == 5) {
                    GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f);
                }
                if (hitEntity) {
                    RenderUtil.color(new Color(255, 248, 0, 255).getRGB());
                }
            }
            this.renderPoint();
            GL11.glPopMatrix();
            RenderUtil.disableRender3D(true);
        }
    }

    private void renderPoint() {
        GL11.glBegin((int)1);
        GL11.glVertex3d((double)-0.5, (double)0.0, (double)0.0);
        GL11.glVertex3d((double)0.0, (double)0.0, (double)0.0);
        GL11.glVertex3d((double)0.0, (double)0.0, (double)-0.5);
        GL11.glVertex3d((double)0.0, (double)0.0, (double)0.0);
        GL11.glVertex3d((double)0.5, (double)0.0, (double)0.0);
        GL11.glVertex3d((double)0.0, (double)0.0, (double)0.0);
        GL11.glVertex3d((double)0.0, (double)0.0, (double)0.5);
        GL11.glVertex3d((double)0.0, (double)0.0, (double)0.0);
        GL11.glEnd();
        Cylinder c = new Cylinder();
        GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
        c.setDrawStyle(100011);
        c.draw(0.5f, 0.5f, 0.0f, 256, 27);
    }

    private List getEntitiesWithinAABB(AxisAlignedBB axisalignedBB) {
        ArrayList<Entity> list = new ArrayList<Entity>();
        int chunkMinX = MathHelper.floor_double((axisalignedBB.minX - 2.0) / 16.0);
        int chunkMaxX = MathHelper.floor_double((axisalignedBB.maxX + 2.0) / 16.0);
        int chunkMinZ = MathHelper.floor_double((axisalignedBB.minZ - 2.0) / 16.0);
        int chunkMaxZ = MathHelper.floor_double((axisalignedBB.maxZ + 2.0) / 16.0);
        for (int x2 = chunkMinX; x2 <= chunkMaxX; ++x2) {
            for (int z = chunkMinZ; z <= chunkMaxZ; ++z) {
                if (!Projectile.mc.theWorld.getChunkProvider().chunkExists(x2, z)) continue;
                Projectile.mc.theWorld.getChunkFromChunkCoords(x2, z).getEntitiesWithinAABBForEntity(Projectile.mc.thePlayer, axisalignedBB, list, EntitySelectors.selectAnything);
            }
        }
        return list;
    }
}

