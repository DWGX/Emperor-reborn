package net.minecraft.client.gui;

import dev.emperor.utils.render.GLUtil;
import dev.emperor.utils.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class Gui {
    public static final ResourceLocation optionsBackground = new ResourceLocation("textures/gui/options_background.png");
    public static final ResourceLocation statIcons = new ResourceLocation("textures/gui/container/stats_icons.png");
    public static final ResourceLocation icons = new ResourceLocation("textures/gui/icons.png");
    protected static float zLevel;

    protected void drawHorizontalLine(int startX, int endX, int y2, int color) {
        if (endX < startX) {
            int i = startX;
            startX = endX;
            endX = i;
        }
        Gui.drawRect(startX, y2, endX + 1, y2 + 1, color);
    }

    public static void drawRect3(double x2, double y2, double width, double height, int color) {
        RenderUtil.resetColor();
        RenderUtil.setAlphaLimit(0.0f);
        GLUtil.setup2DRendering(true);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(x2, y2, 0.0).color(color).endVertex();
        worldrenderer.pos(x2, y2 + height, 0.0).color(color).endVertex();
        worldrenderer.pos(x2 + width, y2 + height, 0.0).color(color).endVertex();
        worldrenderer.pos(x2 + width, y2, 0.0).color(color).endVertex();
        tessellator.draw();
        GLUtil.end2DRendering();
    }

    protected void drawVerticalLine(int x2, int startY, int endY, int color) {
        if (endY < startY) {
            int i = startY;
            startY = endY;
            endY = i;
        }
        Gui.drawRect(x2, startY + 1, x2 + 1, endY, color);
    }

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        double minX = Math.min(left, right);
        double maxX = Math.max(left, right);
        double minY = Math.min(top, bottom);
        double maxY = Math.max(top, bottom);
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(red, green, blue, alpha);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(minX, maxY, 0.0).endVertex();
        worldrenderer.pos(maxX, maxY, 0.0).endVertex();
        worldrenderer.pos(maxX, minY, 0.0).endVertex();
        worldrenderer.pos(minX, minY, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawRect2(float left, float top, float right, float bottom, int color) {
        int alpha = color >> 24 & 0xFF;
        boolean needBlend = alpha < 255;
        GL11.glDisable((int)3553);
        if (needBlend) {
            GL11.glEnable((int)3042);
            GL11.glBlendFunc((int)770, (int)771);
            GlStateManager.enableBlend();
            GL11.glColor4ub((byte)((byte)(color >> 16 & 0xFF)), (byte)((byte)(color >> 8 & 0xFF)), (byte)((byte)(color & 0xFF)), (byte)((byte)alpha));
        } else {
            GL11.glColor3ub((byte)((byte)(color >> 16 & 0xFF)), (byte)((byte)(color >> 8 & 0xFF)), (byte)((byte)(color & 0xFF)));
        }
        GL11.glBegin((int)7);
        GL11.glVertex2f((float)left, (float)top);
        GL11.glVertex2f((float)left, (float)bottom);
        GL11.glVertex2f((float)right, (float)bottom);
        GL11.glVertex2f((float)right, (float)top);
        GL11.glEnd();
        if (needBlend) {
            GL11.glDisable((int)3042);
            GlStateManager.disableBlend();
        }
        GL11.glEnable((int)3553);
    }

    protected void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        float f = (float)(startColor >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(startColor >> 16 & 0xFF) / 255.0f;
        float f2 = (float)(startColor >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(startColor & 0xFF) / 255.0f;
        float f4 = (float)(endColor >> 24 & 0xFF) / 255.0f;
        float f5 = (float)(endColor >> 16 & 0xFF) / 255.0f;
        float f6 = (float)(endColor >> 8 & 0xFF) / 255.0f;
        float f7 = (float)(endColor & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(right, top, zLevel).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, top, zLevel).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, bottom, zLevel).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos(right, bottom, zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawCenteredString(FontRenderer fontRendererIn, String text, int x2, int y2, int color) {
        fontRendererIn.drawStringWithShadow(text, x2 - fontRendererIn.getStringWidth(text) / 2, y2, color);
    }

    public void drawString(FontRenderer fontRendererIn, String text, int x2, int y2, int color) {
        fontRendererIn.drawStringWithShadow(text, x2, y2, color);
    }

    public void drawTexturedModalRect(int x2, int y2, int textureX, int textureY, int width, int height) {
        float f = 0.00390625f;
        float f1 = 0.00390625f;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x2 + 0, y2 + height, zLevel).tex((float)(textureX + 0) * f, (float)(textureY + height) * f1).endVertex();
        worldrenderer.pos(x2 + width, y2 + height, zLevel).tex((float)(textureX + width) * f, (float)(textureY + height) * f1).endVertex();
        worldrenderer.pos(x2 + width, y2 + 0, zLevel).tex((float)(textureX + width) * f, (float)(textureY + 0) * f1).endVertex();
        worldrenderer.pos(x2 + 0, y2 + 0, zLevel).tex((float)(textureX + 0) * f, (float)(textureY + 0) * f1).endVertex();
        tessellator.draw();
    }

    public static void drawTexturedModalRect(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV) {
        float f = 0.00390625f;
        float f1 = 0.00390625f;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(xCoord + 0.0f, yCoord + (float)maxV, zLevel).tex((float)minU * 0.00390625f, (float)(minV + maxV) * 0.00390625f).endVertex();
        worldrenderer.pos(xCoord + (float)maxU, yCoord + (float)maxV, zLevel).tex((float)(minU + maxU) * 0.00390625f, (float)(minV + maxV) * 0.00390625f).endVertex();
        worldrenderer.pos(xCoord + (float)maxU, yCoord + 0.0f, zLevel).tex((float)(minU + maxU) * 0.00390625f, (float)minV * 0.00390625f).endVertex();
        worldrenderer.pos(xCoord + 0.0f, yCoord + 0.0f, zLevel).tex((float)minU * 0.00390625f, (float)minV * 0.00390625f).endVertex();
        tessellator.draw();
    }

    public void drawTexturedModalRect(int xCoord, int yCoord, TextureAtlasSprite textureSprite, int widthIn, int heightIn) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(xCoord + 0, yCoord + heightIn, zLevel).tex(textureSprite.getMinU(), textureSprite.getMaxV()).endVertex();
        worldrenderer.pos(xCoord + widthIn, yCoord + heightIn, zLevel).tex(textureSprite.getMaxU(), textureSprite.getMaxV()).endVertex();
        worldrenderer.pos(xCoord + widthIn, yCoord + 0, zLevel).tex(textureSprite.getMaxU(), textureSprite.getMinV()).endVertex();
        worldrenderer.pos(xCoord + 0, yCoord + 0, zLevel).tex(textureSprite.getMinU(), textureSprite.getMinV()).endVertex();
        tessellator.draw();
    }

    public static void drawModalRectWithCustomSizedTexture(float x2, float y2, float u2, float v, float width, float height, float textureWidth, float textureHeight) {
        float f = 1.0f / textureWidth;
        float f1 = 1.0f / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x2, y2 + height, 0.0).tex(u2 * f, (v + height) * f1).endVertex();
        worldrenderer.pos(x2 + width, y2 + height, 0.0).tex((u2 + width) * f, (v + height) * f1).endVertex();
        worldrenderer.pos(x2 + width, y2, 0.0).tex((u2 + width) * f, v * f1).endVertex();
        worldrenderer.pos(x2, y2, 0.0).tex(u2 * f, v * f1).endVertex();
        tessellator.draw();
    }

    public static void drawModalRectWithCustomSizedTexture(int x2, int y2, float u2, float v, int width, int height, float textureWidth, float textureHeight) {
        float f = 1.0f / textureWidth;
        float f1 = 1.0f / textureHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x2, y2 + height, 0.0).tex(u2 * f, (v + (float)height) * f1).endVertex();
        worldrenderer.pos(x2 + width, y2 + height, 0.0).tex((u2 + (float)width) * f, (v + (float)height) * f1).endVertex();
        worldrenderer.pos(x2 + width, y2, 0.0).tex((u2 + (float)width) * f, v * f1).endVertex();
        worldrenderer.pos(x2, y2, 0.0).tex(u2 * f, v * f1).endVertex();
        tessellator.draw();
    }

    public static void drawScaledCustomSizeModalRect(float x2, float y2, float u2, float v, int uWidth, int vHeight, float width, float height, float tileWidth, float tileHeight) {
        float f = 1.0f / tileWidth;
        float f1 = 1.0f / tileHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x2, y2 + height, 0.0).tex(u2 * f, (v + (float)vHeight) * f1).endVertex();
        worldrenderer.pos(x2 + width, y2 + height, 0.0).tex((u2 + (float)uWidth) * f, (v + (float)vHeight) * f1).endVertex();
        worldrenderer.pos(x2 + width, y2, 0.0).tex((u2 + (float)uWidth) * f, v * f1).endVertex();
        worldrenderer.pos(x2, y2, 0.0).tex(u2 * f, v * f1).endVertex();
        tessellator.draw();
    }

    public static void drawScaledCustomSizeModalRect(int x2, int y2, float u2, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight) {
        float f = 1.0f / tileWidth;
        float f1 = 1.0f / tileHeight;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(x2, y2 + height, 0.0).tex(u2 * f, (v + (float)vHeight) * f1).endVertex();
        worldrenderer.pos(x2 + width, y2 + height, 0.0).tex((u2 + (float)uWidth) * f, (v + (float)vHeight) * f1).endVertex();
        worldrenderer.pos(x2 + width, y2, 0.0).tex((u2 + (float)uWidth) * f, v * f1).endVertex();
        worldrenderer.pos(x2, y2, 0.0).tex(u2 * f, v * f1).endVertex();
        tessellator.draw();
    }
}

