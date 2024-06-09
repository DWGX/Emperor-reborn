package dev.emperor.module.modules.render;

import dev.emperor.event.EventTarget;
import dev.emperor.event.rendering.EventRender3D;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.values.ColorValue;
import dev.emperor.utils.render.RenderUtil;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class Skeletal
extends Module {
    private static final Map<EntityPlayer, float[][]> entityModelRotations = new HashMap<EntityPlayer, float[][]>();
    public static ColorValue skeletonColor = new ColorValue("SkeletonColor", Color.WHITE.getRGB());

    public Skeletal() {
        super("Skeletal", Category.Render);
    }

    @EventTarget
    public void onRender3D(EventRender3D event) {
        GlStateManager.enableBlend();
        GL11.glEnable((int)2848);
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(770, 771);
        GL11.glHint((int)3154, (int)4354);
        GlStateManager.depthMask(false);
        GL11.glDisable((int)2848);
        entityModelRotations.keySet().removeIf(player -> !Skeletal.mc.theWorld.playerEntities.contains(player));
        Skeletal.mc.theWorld.playerEntities.forEach(player -> {
            if (player == Skeletal.mc.thePlayer || player.isInvisible()) {
                return;
            }
            float[][] modelRotations = entityModelRotations.get(player);
            if (modelRotations == null) {
                return;
            }
            GL11.glPushMatrix();
            GL11.glLineWidth((float)1.0f);
            int c = (Integer)skeletonColor.getValue();
            RenderUtil.glColor(c);
            Vec3 interp = this.interpolateRender((EntityPlayer)player);
            double x2 = interp.xCoord - Minecraft.getMinecraft().getRenderManager().renderPosX;
            double y2 = interp.yCoord - Minecraft.getMinecraft().getRenderManager().renderPosY;
            double z = interp.zCoord - Minecraft.getMinecraft().getRenderManager().renderPosZ;
            GL11.glTranslated((double)x2, (double)y2, (double)z);
            float bodyYawOffset = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * Skeletal.mc.timer.renderPartialTicks;
            GL11.glRotatef((float)(-bodyYawOffset), (float)0.0f, (float)1.0f, (float)0.0f);
            GL11.glTranslated((double)0.0, (double)0.0, (double)(player.isSneaking() ? -0.235 : 0.0));
            float legHeight = player.isSneaking() ? 0.6f : 0.75f;
            GL11.glPushMatrix();
            GL11.glTranslated((double)-0.125, (double)legHeight, (double)0.0);
            if (modelRotations[3][0] != 0.0f) {
                GL11.glRotatef((float)(modelRotations[3][0] * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
            }
            if (modelRotations[3][1] != 0.0f) {
                GL11.glRotatef((float)(modelRotations[3][1] * 57.295776f), (float)0.0f, (float)1.0f, (float)0.0f);
            }
            if (modelRotations[3][2] != 0.0f) {
                GL11.glRotatef((float)(modelRotations[3][2] * 57.295776f), (float)0.0f, (float)0.0f, (float)1.0f);
            }
            GL11.glBegin((int)3);
            GL11.glVertex3d((double)0.0, (double)0.0, (double)0.0);
            GL11.glVertex3d((double)0.0, (double)(-legHeight), (double)0.0);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslated((double)0.125, (double)legHeight, (double)0.0);
            if (modelRotations[4][0] != 0.0f) {
                GL11.glRotatef((float)(modelRotations[4][0] * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
            }
            if (modelRotations[4][1] != 0.0f) {
                GL11.glRotatef((float)(modelRotations[4][1] * 57.295776f), (float)0.0f, (float)1.0f, (float)0.0f);
            }
            if (modelRotations[4][2] != 0.0f) {
                GL11.glRotatef((float)(modelRotations[4][2] * 57.295776f), (float)0.0f, (float)0.0f, (float)1.0f);
            }
            GL11.glBegin((int)3);
            GL11.glVertex3d((double)0.0, (double)0.0, (double)0.0);
            GL11.glVertex3d((double)0.0, (double)(-legHeight), (double)0.0);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glTranslated((double)0.0, (double)0.0, (double)(player.isSneaking() ? 0.25 : 0.0));
            GL11.glPushMatrix();
            GL11.glTranslated((double)0.0, (double)(player.isSneaking() ? -0.05 : 0.0), (double)(player.isSneaking() ? -0.01725 : 0.0));
            GL11.glPushMatrix();
            GL11.glTranslated((double)-0.375, (double)((double)legHeight + 0.55), (double)0.0);
            if (modelRotations[1][0] != 0.0f) {
                GL11.glRotatef((float)(modelRotations[1][0] * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
            }
            if (modelRotations[1][1] != 0.0f) {
                GL11.glRotatef((float)(modelRotations[1][1] * 57.295776f), (float)0.0f, (float)1.0f, (float)0.0f);
            }
            if (modelRotations[1][2] != 0.0f) {
                GL11.glRotatef((float)(-modelRotations[1][2] * 57.295776f), (float)0.0f, (float)0.0f, (float)1.0f);
            }
            GL11.glBegin((int)3);
            GL11.glVertex3d((double)0.0, (double)0.0, (double)0.0);
            GL11.glVertex3d((double)0.0, (double)-0.5, (double)0.0);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslated((double)0.375, (double)((double)legHeight + 0.55), (double)0.0);
            if (modelRotations[2][0] != 0.0f) {
                GL11.glRotatef((float)(modelRotations[2][0] * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
            }
            if (modelRotations[2][1] != 0.0f) {
                GL11.glRotatef((float)(modelRotations[2][1] * 57.295776f), (float)0.0f, (float)1.0f, (float)0.0f);
            }
            if (modelRotations[2][2] != 0.0f) {
                GL11.glRotatef((float)(-modelRotations[2][2] * 57.295776f), (float)0.0f, (float)0.0f, (float)1.0f);
            }
            GL11.glBegin((int)3);
            GL11.glVertex3d((double)0.0, (double)0.0, (double)0.0);
            GL11.glVertex3d((double)0.0, (double)-0.5, (double)0.0);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glRotatef((float)(bodyYawOffset - player.rotationYawHead), (float)0.0f, (float)1.0f, (float)0.0f);
            GL11.glPushMatrix();
            GL11.glTranslated((double)0.0, (double)((double)legHeight + 0.55), (double)0.0);
            if (modelRotations[0][0] != 0.0f) {
                GL11.glRotatef((float)(modelRotations[0][0] * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
            }
            GL11.glBegin((int)3);
            GL11.glVertex3d((double)0.0, (double)0.0, (double)0.0);
            GL11.glVertex3d((double)0.0, (double)0.3, (double)0.0);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPopMatrix();
            GL11.glRotatef((float)(player.isSneaking() ? 25.0f : 0.0f), (float)1.0f, (float)0.0f, (float)0.0f);
            GL11.glTranslated((double)0.0, (double)(player.isSneaking() ? -0.16175 : 0.0), (double)(player.isSneaking() ? -0.48025 : 0.0));
            GL11.glPushMatrix();
            GL11.glTranslated((double)0.0, (double)legHeight, (double)0.0);
            GL11.glBegin((int)3);
            GL11.glVertex3d((double)-0.125, (double)0.0, (double)0.0);
            GL11.glVertex3d((double)0.125, (double)0.0, (double)0.0);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslated((double)0.0, (double)legHeight, (double)0.0);
            GL11.glBegin((int)3);
            GL11.glVertex3d((double)0.0, (double)0.0, (double)0.0);
            GL11.glVertex3d((double)0.0, (double)0.55, (double)0.0);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslated((double)0.0, (double)((double)legHeight + 0.55), (double)0.0);
            GL11.glBegin((int)3);
            GL11.glVertex3d((double)-0.375, (double)0.0, (double)0.0);
            GL11.glVertex3d((double)0.375, (double)0.0, (double)0.0);
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glPopMatrix();
        });
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GL11.glDisable((int)2848);
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
    }

    public static void updateModel(EntityPlayer player, ModelPlayer model) {
        entityModelRotations.put(player, new float[][]{{model.bipedHead.rotateAngleX, model.bipedHead.rotateAngleY, model.bipedHead.rotateAngleZ}, {model.bipedRightArm.rotateAngleX, model.bipedRightArm.rotateAngleY, model.bipedRightArm.rotateAngleZ}, {model.bipedLeftArm.rotateAngleX, model.bipedLeftArm.rotateAngleY, model.bipedLeftArm.rotateAngleZ}, {model.bipedRightLeg.rotateAngleX, model.bipedRightLeg.rotateAngleY, model.bipedRightLeg.rotateAngleZ}, {model.bipedLeftLeg.rotateAngleX, model.bipedLeftLeg.rotateAngleY, model.bipedLeftLeg.rotateAngleZ}});
    }

    public Vec3 interpolateRender(EntityPlayer player) {
        float part = Skeletal.mc.timer.renderPartialTicks;
        double interpX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)part;
        double interpY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)part;
        double interpZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)part;
        return new Vec3(interpX, interpY, interpZ);
    }
}

