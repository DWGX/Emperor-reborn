package dev.emperor.module.modules.render;

import dev.emperor.event.EventTarget;
import dev.emperor.event.rendering.EventRender3D;
import dev.emperor.event.world.EventMotion;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.modules.combat.KillAura;
import dev.emperor.module.values.BoolValue;
import dev.emperor.module.values.ColorValue;
import dev.emperor.module.values.ModeValue;
import dev.emperor.module.values.NumberValue;
import dev.emperor.utils.render.RenderUtil;
import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.opengl.GL11;

public class ChinaHat
extends Module {
    private final ModeValue<ColorMode> colorModeValue = new ModeValue("Color", (Enum[])ColorMode.values(), (Enum)ColorMode.Astolfo);
    private final BoolValue renderInFirstPersonValue = new BoolValue("First Person", false);
    private final BoolValue target = new BoolValue("Target", false);
    private final NumberValue sizeValue = new NumberValue("Size", 0.5, 0.0, 2.0, 0.1);
    private final NumberValue pointsValue = new NumberValue("Points", 30.0, 3.0, 180.0, 1.0);
    private final NumberValue offSetValue = new NumberValue("OffSet", 2000.0, 0.0, 5000.0, 100.0);
    public final ColorValue colorValue = new ColorValue("Color", new Color(255, 255, 255).getRGB());
    public final ColorValue secondColorValue = new ColorValue("SecondColor", new Color(0, 0, 0).getRGB());
    public final ColorValue thirdColorValue = new ColorValue("ThirdColor", new Color(0, 0, 0).getRGB());
    private final double[][] pointsCache = new double[181][2];
    private int lastPoints;
    private double lastSize;
    private float yaw;
    private float prevYaw;
    private float pitch;
    private float prevPitch;
    private final Color[] gradient = new Color[]{new Color(255, 150, 255), new Color(255, 132, 199), new Color(211, 101, 187), new Color(160, 80, 158), new Color(120, 63, 160), new Color(123, 65, 168), new Color(104, 52, 152), new Color(142, 74, 175), new Color(160, 83, 179), new Color(255, 110, 189), new Color(255, 150, 255)};
    private final Color[] cherry = new Color[]{new Color(35, 255, 145), new Color(35, 255, 145), new Color(35, 255, 145), new Color(35, 255, 145), new Color(35, 255, 145), new Color(155, 155, 155), new Color(255, 50, 130), new Color(255, 50, 130), new Color(255, 50, 130), new Color(255, 50, 130), new Color(255, 50, 130), new Color(200, 200, 200)};
    private final Color[] rainbow = new Color[]{new Color(30, 250, 215), new Color(0, 200, 255), new Color(50, 100, 255), new Color(100, 50, 255), new Color(255, 50, 240), new Color(255, 0, 0), new Color(255, 150, 0), new Color(255, 255, 0), new Color(0, 255, 0), new Color(80, 240, 155)};
    private final Color[] astolfo = new Color[]{new Color(252, 106, 140), new Color(252, 106, 213), new Color(218, 106, 252), new Color(145, 106, 252), new Color(106, 140, 252), new Color(106, 213, 252), new Color(106, 213, 252), new Color(106, 140, 252), new Color(145, 106, 252), new Color(218, 106, 252), new Color(252, 106, 213), new Color(252, 106, 140)};
    private final Color[] metrix = new Color[]{new Color(RandomUtils.nextInt((int)0, (int)255), RandomUtils.nextInt((int)0, (int)255), RandomUtils.nextInt((int)0, (int)255)), new Color(RandomUtils.nextInt((int)0, (int)255), RandomUtils.nextInt((int)0, (int)255), RandomUtils.nextInt((int)0, (int)255)), new Color(RandomUtils.nextInt((int)0, (int)255), RandomUtils.nextInt((int)0, (int)255), RandomUtils.nextInt((int)0, (int)255)), new Color(RandomUtils.nextInt((int)0, (int)255), RandomUtils.nextInt((int)0, (int)255), RandomUtils.nextInt((int)0, (int)255)), new Color(RandomUtils.nextInt((int)0, (int)255), RandomUtils.nextInt((int)0, (int)255), RandomUtils.nextInt((int)0, (int)255)), new Color(RandomUtils.nextInt((int)0, (int)255), RandomUtils.nextInt((int)0, (int)255), RandomUtils.nextInt((int)0, (int)255)), new Color(RandomUtils.nextInt((int)0, (int)255), RandomUtils.nextInt((int)0, (int)255), RandomUtils.nextInt((int)0, (int)255)), new Color(RandomUtils.nextInt((int)0, (int)255), RandomUtils.nextInt((int)0, (int)255), RandomUtils.nextInt((int)0, (int)255)), new Color(RandomUtils.nextInt((int)0, (int)255), RandomUtils.nextInt((int)0, (int)255), RandomUtils.nextInt((int)0, (int)255)), new Color(RandomUtils.nextInt((int)0, (int)255), RandomUtils.nextInt((int)0, (int)255), RandomUtils.nextInt((int)0, (int)255)), new Color(RandomUtils.nextInt((int)0, (int)255), RandomUtils.nextInt((int)0, (int)255), RandomUtils.nextInt((int)0, (int)255)), new Color(RandomUtils.nextInt((int)0, (int)255), RandomUtils.nextInt((int)0, (int)255), RandomUtils.nextInt((int)0, (int)255))};

    public ChinaHat() {
        super("ChinaHat", Category.Render);
    }

    @EventTarget
    public void onUpdatePosition(EventMotion event) {
        if (event.isPre()) {
            this.yaw = event.getYaw();
            this.prevYaw = event.getPrevYaw();
            this.pitch = event.getPitch();
            this.prevPitch = event.getPrevPitch();
        }
    }

    @EventTarget
    public void onRender3D(EventRender3D event) {
        Iterator<EntityPlayer> iterator = ChinaHat.getLoadedPlayers().iterator();
        if (this.lastSize != (Double)this.sizeValue.getValue() || (double)this.lastPoints != (Double)this.pointsValue.getValue()) {
            this.lastSize = (Double)this.sizeValue.getValue();
            this.lastPoints = ((Double)this.pointsValue.getValue()).intValue();
            this.genPoints(this.lastPoints, this.lastSize);
        }
        while (iterator.hasNext()) {
            EntityPlayer entity = iterator.next();
            if (entity == ChinaHat.mc.thePlayer && ChinaHat.mc.gameSettings.thirdPersonView != 0) {
                this.drawHat(event, entity);
            }
            if (!((Boolean)this.target.getValue()).booleanValue() || entity != KillAura.target) continue;
            this.drawHat(event, entity);
        }
    }

    public static List<EntityPlayer> getLoadedPlayers() {
        return ChinaHat.mc.theWorld.playerEntities;
    }

    private void drawHat(EventRender3D event, EntityLivingBase entity) {
        Color[] colorMode;
        boolean isPlayerSP = entity.isEntityEqual(ChinaHat.mc.thePlayer);
        if (ChinaHat.mc.gameSettings.thirdPersonView == 0 && isPlayerSP && !((Boolean)this.renderInFirstPersonValue.getValue()).booleanValue()) {
            return;
        }
        GL11.glDisable((int)3553);
        GL11.glDisable((int)2884);
        GL11.glDepthMask((boolean)false);
        GL11.glDisable((int)2929);
        GL11.glShadeModel((int)7425);
        GL11.glEnable((int)3042);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        double x2 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)event.getPartialTicks() - Minecraft.getMinecraft().getRenderManager().renderPosX;
        double y2 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)event.getPartialTicks() - Minecraft.getMinecraft().getRenderManager().renderPosY;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)event.getPartialTicks() - Minecraft.getMinecraft().getRenderManager().renderPosZ;
        Color[] colors = new Color[181];
        switch ((ColorMode)((Object)this.colorModeValue.getValue())) {
            case Purple: {
                colorMode = this.gradient;
                break;
            }
            case Astolfo: {
                colorMode = this.astolfo;
                break;
            }
            case Cherry: {
                colorMode = this.cherry;
                break;
            }
            case Matrix: {
                colorMode = this.metrix;
                break;
            }
            case Custom: {
                colorMode = new Color[]{new Color((Integer)this.colorValue.getValue()), new Color((Integer)this.colorValue.getValue()), new Color((Integer)this.colorValue.getValue()).darker(), new Color((Integer)this.colorValue.getValue()).darker().darker(), new Color((Integer)this.colorValue.getValue()), new Color((Integer)this.colorValue.getValue()).darker(), new Color((Integer)this.colorValue.getValue()).darker().darker(), new Color((Integer)this.colorValue.getValue()), new Color((Integer)this.colorValue.getValue()).darker(), new Color((Integer)this.colorValue.getValue()).darker().darker(), new Color((Integer)this.colorValue.getValue()), new Color((Integer)this.colorValue.getValue())};
                break;
            }
            case Blend: {
                colorMode = new Color[]{new Color((Integer)this.colorValue.getValue()).darker().darker(), new Color((Integer)this.colorValue.getValue()), new Color((Integer)this.colorValue.getValue()), new Color((Integer)this.colorValue.getValue()), new Color((Integer)this.colorValue.getValue()).darker().darker(), new Color((Integer)this.secondColorValue.getValue()).darker().darker(), new Color((Integer)this.secondColorValue.getValue()), new Color((Integer)this.secondColorValue.getValue()), new Color((Integer)this.secondColorValue.getValue()), new Color((Integer)this.secondColorValue.getValue()).darker().darker(), new Color((Integer)this.thirdColorValue.getValue()).darker().darker(), new Color((Integer)this.thirdColorValue.getValue()), new Color((Integer)this.thirdColorValue.getValue()), new Color((Integer)this.thirdColorValue.getValue()), new Color((Integer)this.thirdColorValue.getValue()).darker().darker()};
                break;
            }
            default: {
                colorMode = this.rainbow;
            }
        }
        for (int i = 0; i < colors.length; ++i) {
            colors[i] = this.colorModeValue.getValue() == ColorMode.Rainbow ? this.fadeBetween(colorMode, 6000.0, (double)i * (6000.0 / (Double)this.pointsValue.getValue())) : this.fadeBetween(colorMode, ((Double)this.offSetValue.getValue()).longValue(), (double)i * ((double)((Double)this.offSetValue.getValue()).floatValue() / (Double)this.pointsValue.getValue()));
        }
        GL11.glPushMatrix();
        GL11.glTranslated((double)x2, (double)(y2 + 1.9), (double)z);
        if (entity.isSneaking()) {
            GL11.glTranslated((double)0.0, (double)-0.2, (double)0.0);
        }
        GL11.glRotatef((float)RenderUtil.interpolate(this.prevYaw, this.yaw, event.getPartialTicks()), (float)0.0f, (float)-1.0f, (float)0.0f);
        float interpolate = RenderUtil.interpolate(this.prevPitch, this.pitch, event.getPartialTicks());
        GL11.glRotatef((float)(interpolate / 3.0f), (float)1.0f, (float)0.0f, (float)0.0f);
        GL11.glTranslated((double)0.0, (double)0.0, (double)(interpolate / 270.0f));
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glLineWidth((float)2.0f);
        GL11.glBegin((int)2);
        this.drawCircle(((Double)this.pointsValue.getValue()).intValue() - 1, colors, 255);
        GL11.glEnd();
        GL11.glDisable((int)2848);
        GL11.glHint((int)3154, (int)4352);
        GL11.glBegin((int)6);
        GL11.glVertex3d((double)0.0, (double)((Double)this.sizeValue.getValue() / 2.0), (double)0.0);
        this.drawCircle(((Double)this.pointsValue.getValue()).intValue(), colors, 85);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glDisable((int)3042);
        GL11.glDepthMask((boolean)true);
        GL11.glShadeModel((int)7424);
        GL11.glEnable((int)2929);
        GL11.glEnable((int)2884);
        GL11.glEnable((int)3553);
    }

    private void drawCircle(int points, Color[] colors, int alpha) {
        for (int i = 0; i <= points; ++i) {
            double[] point = this.pointsCache[i];
            Color clr = colors[i];
            GL11.glColor4f((float)((float)clr.getRed() / 255.0f), (float)((float)clr.getGreen() / 255.0f), (float)((float)clr.getBlue() / 255.0f), (float)((float)alpha / 255.0f));
            GL11.glVertex3d((double)point[0], (double)0.0, (double)point[1]);
        }
    }

    private void genPoints(int points, double size) {
        for (int i = 0; i <= points; ++i) {
            double cos = size * StrictMath.cos((double)i * Math.PI * 2.0 / (double)points);
            double sin = size * StrictMath.sin((double)i * Math.PI * 2.0 / (double)points);
            this.pointsCache[i][0] = cos;
            this.pointsCache[i][1] = sin;
        }
    }

    public Color fadeBetween(Color[] table, double progress) {
        int i = table.length;
        if (progress == 1.0) {
            return table[0];
        }
        if (progress == 0.0) {
            return table[i - 1];
        }
        double max = Math.max(0.0, (1.0 - progress) * (double)(i - 1));
        int min = (int)max;
        return this.fadeBetween(table[min], table[min + 1], max - (double)min);
    }

    public Color fadeBetween(Color start, Color end, double progress) {
        if (progress > 1.0) {
            progress = 1.0 - progress % 1.0;
        }
        return this.gradient(start, end, progress);
    }

    public Color gradient(Color start, Color end, double progress) {
        double invert = 1.0 - progress;
        return new Color((int)((double)start.getRed() * invert + (double)end.getRed() * progress), (int)((double)start.getGreen() * invert + (double)end.getGreen() * progress), (int)((double)start.getBlue() * invert + (double)end.getBlue() * progress), (int)((double)start.getAlpha() * invert + (double)end.getAlpha() * progress));
    }

    public Color fadeBetween(Color[] table, double speed, double offset) {
        return this.fadeBetween(table, ((double)System.currentTimeMillis() + offset) % speed / speed);
    }

    public static enum ColorMode {
        Astolfo,
        Rainbow,
        Cherry,
        Custom,
        Purple,
        Blend,
        Matrix;

    }
}

