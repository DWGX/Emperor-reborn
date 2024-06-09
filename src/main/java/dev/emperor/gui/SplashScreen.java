package dev.emperor.gui;

import dev.emperor.Client;
import dev.emperor.utils.render.AnimationUtil;
import dev.emperor.utils.render.RenderUtil;
import dev.emperor.utils.render.fontRender.FontManager;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;

public final class SplashScreen {
    private static int PROGRESS;
    private static String CURRENT;
    private static TextureManager ctm;
    private static float animated;
    static float hue2;

    public static void update() {
        if (Minecraft.getMinecraft() == null || Minecraft.getMinecraft().getLanguageManager() == null) {
            return;
        }
        SplashScreen.drawSplash(Minecraft.getMinecraft().getTextureManager());
    }

    public static void setProgress(int givenProgress, String givenSplash) {
        PROGRESS = givenProgress;
        CURRENT = givenSplash;
        SplashScreen.update();
    }

    public static void drawSplash(TextureManager tm) {
        if (ctm == null) {
            ctm = tm;
        }
        ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
        int scaleFactor = scaledresolution.getScaleFactor();
        Framebuffer framebuffer = new Framebuffer(scaledresolution.getScaledWidth() * scaleFactor, scaledresolution.getScaledHeight() * scaleFactor, true);
        framebuffer.bindFramebuffer(false);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), 0.0, 1000.0, 3000.0);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0f, 0.0f, -2000.0f);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();
        Gui.drawRect(0.0, 0.0, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), new Color(32, 32, 32).getRGB());
        float h_ = hue2;
        //float h2 = hue2 + 85.0f;
        hue2 = (float)((double)hue2 + 0.5);
        Color c = Color.getHSBColor(h_ / 255.0f, 0.9f, 1.0f);
        //Color c2 = Color.getHSBColor(h2 / 255.0f, 0.9f, 1.0f);
        int color1 = c.getRGB();
        RenderUtil.drawRect(0.0, 0.0, scaledresolution.getScaledWidth(), 1.0, color1);
        SplashScreen.drawProgress();
        framebuffer.unbindFramebuffer();
        framebuffer.framebufferRender(scaledresolution.getScaledWidth() * scaleFactor, scaledresolution.getScaledHeight() * scaleFactor);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1f);
        Minecraft.getMinecraft().updateDisplay();
    }

    private static void drawProgress() {
        if (Minecraft.getMinecraft().gameSettings == null || Minecraft.getMinecraft().getTextureManager() == null) {
            return;
        }
        final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        final float startX = sr.getScaledWidth() / 2.0f - 255.0f;
        final float endX = sr.getScaledWidth() / 2.0f + 255.0f;
        final double nProgress = SplashScreen.PROGRESS;
        final double calc = nProgress / 14.0 * 510.0;
        SplashScreen.animated = (float)new AnimationUtil().animateNoFast(startX + calc, SplashScreen.animated, 0.10000000149011612);
        RenderUtil.drawImage(new ResourceLocation("express/splash.png"), 0, 0, sr.getScaledWidth(), sr.getScaledHeight());
        Gui.drawRect(startX, sr.getScaledHeight() - 35.0f, endX, sr.getScaledHeight() - 34.0f, new Color(255, 255, 255, 100).getRGB());
        Gui.drawRect(startX, sr.getScaledHeight() - 35.0f, SplashScreen.animated, sr.getScaledHeight() - 34.0f, new Color(255, 255, 255, 255).getRGB());
        FontManager.arial16.drawStringWithShadow("正在初始化" + SplashScreen.CURRENT + "...", startX, sr.getScaledHeight() - 44.0f, new Color(255, 255, 255, 255).getRGB());
        FontManager.arial12.drawString("CNPROD" + Client.NAME.toUpperCase() + Client.VERSION, 10.0f, sr.getScaledHeight() - 10.0f, new Color(255, 255, 255, 200).getRGB());
    }

    static {
        CURRENT = "";
        animated = 0.0f;
    }
}

