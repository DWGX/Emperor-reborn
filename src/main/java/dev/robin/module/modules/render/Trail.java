package dev.robin.module.modules.render;

import dev.robin.event.EventTarget;
import dev.robin.event.rendering.EventRender3D;
import dev.robin.event.world.EventMotion;
import dev.robin.module.Category;
import dev.robin.module.Module;
import dev.robin.module.modules.render.HUD;
import dev.robin.module.values.BoolValue;
import dev.robin.module.values.ColorValue;
import dev.robin.module.values.ModeValue;
import dev.robin.module.values.NumberValue;
import dev.robin.utils.render.ColorUtil;
import dev.robin.utils.render.GLUtil;
import dev.robin.utils.render.Pair;
import dev.robin.utils.render.RenderUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class Trail
extends Module {
    private final ModeValue<MODE> mode = new ModeValue("Mode", (Enum[])MODE.values(), (Enum)MODE.Line);
    private final NumberValue particleAmount = new NumberValue("Particle Amount", 15.0, 1.0, 500.0, 1.0);
    private final BoolValue seeThroughWalls = new BoolValue("Walls", true);
    private final ModeValue<COLORMODE> colorMode = new ModeValue("Color Mode", (Enum[])COLORMODE.values(), (Enum)COLORMODE.Sync);
    private final ColorValue color = new ColorValue("Color", Color.WHITE.getRGB());
    private final List<Vec3> path = new ArrayList<Vec3>();

    public Trail() {
        super("Trail", Category.Render);
    }

    @EventTarget
    public void onMotionEvent(EventMotion e) {
        if (e.isPre()) {
            if (Trail.mc.thePlayer.lastTickPosX != Trail.mc.thePlayer.posX || Trail.mc.thePlayer.lastTickPosY != Trail.mc.thePlayer.posY || Trail.mc.thePlayer.lastTickPosZ != Trail.mc.thePlayer.posZ) {
                this.path.add(new Vec3(Trail.mc.thePlayer.posX, Trail.mc.thePlayer.posY, Trail.mc.thePlayer.posZ));
            }
            while ((double)this.path.size() > (Double)this.particleAmount.getValue()) {
                this.path.remove(0);
            }
        }
    }

    @EventTarget
    public void onRender3DEvent(EventRender3D event) {
        int i = 0;
        Pair<Color, Color> colors = ((COLORMODE)((Object)this.colorMode.getValue())).name().equals("Custom") ? Pair.of(this.color.getColorC(), this.color.getColorC()) : Pair.of(HUD.mainColor.getColorC());
        switch (((MODE)((Object)this.mode.getValue())).name()) {
            case "Rise": {
                if (((Boolean)this.seeThroughWalls.getValue()).booleanValue()) {
                    GlStateManager.disableDepth();
                }
                GL11.glEnable((int)3042);
                GL11.glDisable((int)3553);
                GL11.glEnable((int)2848);
                GL11.glBlendFunc((int)770, (int)771);
                for (Vec3 v : this.path) {
                    ++i;
                    boolean draw = true;
                    double x2 = v.xCoord - Trail.mc.getRenderManager().renderPosX;
                    double y2 = v.yCoord - Trail.mc.getRenderManager().renderPosY;
                    double z = v.zCoord - Trail.mc.getRenderManager().renderPosZ;
                    double distanceFromPlayer = Trail.mc.thePlayer.getDistance(v.xCoord, v.yCoord - 1.0, v.zCoord);
                    int quality = (int)(distanceFromPlayer * 4.0 + 10.0);
                    if (quality > 350) {
                        quality = 350;
                    }
                    if (i % 10 != 0 && distanceFromPlayer > 25.0) {
                        draw = false;
                    }
                    if (i % 3 == 0 && distanceFromPlayer > 15.0) {
                        draw = false;
                    }
                    if (!draw) continue;
                    GL11.glPushMatrix();
                    GL11.glTranslated((double)x2, (double)y2, (double)z);
                    float scale = 0.06f;
                    GL11.glScalef((float)-0.06f, (float)-0.06f, (float)-0.06f);
                    GL11.glRotated((double)(-Trail.mc.getRenderManager().playerViewY), (double)0.0, (double)1.0, (double)0.0);
                    GL11.glRotated((double)Trail.mc.getRenderManager().playerViewX, (double)1.0, (double)0.0, (double)0.0);
                    Color c = ColorUtil.interpolateColorsBackAndForth(7, 3 + i * 20, colors.getFirst(), colors.getSecond(), false);
                    RenderUtil.drawFilledCircleNoGL(0, -2, 0.7, ColorUtil.applyOpacity(c.getRGB(), 0.6f), quality);
                    if (distanceFromPlayer < 4.0) {
                        RenderUtil.drawFilledCircleNoGL(0, -2, 1.4, ColorUtil.applyOpacity(c.getRGB(), 0.25f), quality);
                    }
                    if (distanceFromPlayer < 20.0) {
                        RenderUtil.drawFilledCircleNoGL(0, -2, 2.3, ColorUtil.applyOpacity(c.getRGB(), 0.15f), quality);
                    }
                    GL11.glScalef((float)0.8f, (float)0.8f, (float)0.8f);
                    GL11.glPopMatrix();
                }
                GL11.glDisable((int)2848);
                GL11.glEnable((int)3553);
                GL11.glDisable((int)3042);
                if (((Boolean)this.seeThroughWalls.getValue()).booleanValue()) {
                    GlStateManager.enableDepth();
                }
                GL11.glColor3d((double)255.0, (double)255.0, (double)255.0);
                break;
            }
            case "Line": {
                this.renderLine(this.path, colors);
            }
        }
    }

    @Override
    public void onEnable() {
        this.path.clear();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        this.path.clear();
        super.onDisable();
    }

    public void renderLine(List<Vec3> path) {
        this.renderLine(path, Pair.of(Color.WHITE));
    }

    public void renderLine(List<Vec3> path, Pair<Color, Color> colors) {
        GlStateManager.disableDepth();
        RenderUtil.setAlphaLimit(0.0f);
        RenderUtil.resetColor();
        GLUtil.setup2DRendering();
        GLUtil.startBlend();
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glShadeModel((int)7425);
        GL11.glLineWidth((float)3.0f);
        GL11.glBegin((int)3);
        int count = 0;
        int alpha = 200;
        int fadeOffset = 15;
        for (Vec3 v : path) {
            if (fadeOffset > count) {
                alpha = count * (200 / fadeOffset);
            }
            RenderUtil.resetColor();
            RenderUtil.color(RenderUtil.reAlpha(ColorUtil.interpolateColorsBackAndForth(15, count * 5, colors.getFirst(), colors.getSecond(), false), alpha).getRGB());
            double x2 = v.xCoord - Trail.mc.getRenderManager().renderPosX;
            double y2 = v.yCoord - Trail.mc.getRenderManager().renderPosY;
            double z = v.zCoord - Trail.mc.getRenderManager().renderPosZ;
            GL11.glVertex3d((double)x2, (double)y2, (double)z);
            ++count;
        }
        GL11.glEnd();
        GL11.glDisable((int)2848);
        GLUtil.end2DRendering();
        GlStateManager.enableDepth();
    }

    private static enum COLORMODE {
        Sync,
        Custom;

    }

    private static enum MODE {
        Line,
        Rise;

    }
}

