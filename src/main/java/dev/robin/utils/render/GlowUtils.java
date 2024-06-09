package dev.robin.utils.render;

import dev.robin.utils.render.StencilUtil;
import dev.robin.utils.render.img.GaussianFilter;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.lwjgl.opengl.GL11;

public class GlowUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static HashMap<Integer, Integer> shadowCache = new HashMap();

    public static void drawGlow(float x2, float y2, float width, float height, int blurRadius, Color color, Runnable cutMethod) {
        GL11.glPushMatrix();
        GlStateManager.alphaFunc(516, 0.01f);
        float _X = (x2 -= (float)blurRadius) - 0.25f;
        float _Y = (y2 -= (float)blurRadius) + 0.25f;
        int identifier = (int)((width += (float)(blurRadius * 2)) * (height += (float)(blurRadius * 2)) + width + (float)(color.hashCode() * blurRadius) + (float)blurRadius);
        StencilUtil.write(false);
        cutMethod.run();
        StencilUtil.erase(false);
        GL11.glEnable((int)3553);
        GL11.glDisable((int)2884);
        GL11.glEnable((int)3008);
        GlStateManager.enableBlend();
        int texId = -1;
        if (shadowCache.containsKey(identifier)) {
            texId = shadowCache.get(identifier);
            GlStateManager.bindTexture(texId);
        } else {
            if (width <= 0.0f) {
                width = 1.0f;
            }
            if (height <= 0.0f) {
                height = 1.0f;
            }
            BufferedImage original = new BufferedImage((int)width, (int)height, 3);
            Graphics g2 = original.getGraphics();
            g2.setColor(color);
            g2.fillRect(blurRadius, blurRadius, (int)(width - (float)(blurRadius * 2)), (int)(height - (float)(blurRadius * 2)));
            g2.dispose();
            GaussianFilter op = new GaussianFilter(blurRadius);
            BufferedImage blurred = op.filter(original, null);
            texId = TextureUtil.uploadTextureImageAllocate(TextureUtil.glGenTextures(), blurred, true, false);
            shadowCache.put(identifier, texId);
        }
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glBegin((int)7);
        GL11.glTexCoord2f((float)0.0f, (float)0.0f);
        GL11.glVertex2f((float)_X, (float)_Y);
        GL11.glTexCoord2f((float)0.0f, (float)1.0f);
        GL11.glVertex2f((float)_X, (float)(_Y + height));
        GL11.glTexCoord2f((float)1.0f, (float)1.0f);
        GL11.glVertex2f((float)(_X + width), (float)(_Y + height));
        GL11.glTexCoord2f((float)1.0f, (float)0.0f);
        GL11.glVertex2f((float)(_X + width), (float)_Y);
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
        StencilUtil.dispose();
        GL11.glEnable((int)2884);
        GL11.glPopMatrix();
    }

    public static void drawGlow(float x2, float y2, float width, float height, int blurRadius, Color color) {
        GL11.glPushMatrix();
        GlStateManager.alphaFunc(516, 0.01f);
        float _X = (x2 -= (float)blurRadius) - 0.25f;
        float _Y = (y2 -= (float)blurRadius) + 0.25f;
        int identifier = (int)((width += (float)(blurRadius * 2)) * (height += (float)(blurRadius * 2)) + width + (float)(color.hashCode() * blurRadius) + (float)blurRadius);
        GL11.glEnable((int)3553);
        GL11.glDisable((int)2884);
        GL11.glEnable((int)3008);
        GlStateManager.enableBlend();
        int texId = -1;
        if (shadowCache.containsKey(identifier)) {
            texId = shadowCache.get(identifier);
            GlStateManager.bindTexture(texId);
        } else {
            if (width <= 0.0f) {
                width = 1.0f;
            }
            if (height <= 0.0f) {
                height = 1.0f;
            }
            BufferedImage original = new BufferedImage((int)width, (int)height, 3);
            Graphics g2 = original.getGraphics();
            g2.setColor(color);
            g2.fillRect(blurRadius, blurRadius, (int)(width - (float)(blurRadius * 2)), (int)(height - (float)(blurRadius * 2)));
            g2.dispose();
            GaussianFilter op = new GaussianFilter(blurRadius);
            BufferedImage blurred = op.filter(original, null);
            texId = TextureUtil.uploadTextureImageAllocate(TextureUtil.glGenTextures(), blurred, true, false);
            shadowCache.put(identifier, texId);
        }
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glBegin((int)7);
        GL11.glTexCoord2f((float)0.0f, (float)0.0f);
        GL11.glVertex2f((float)_X, (float)_Y);
        GL11.glTexCoord2f((float)0.0f, (float)1.0f);
        GL11.glVertex2f((float)_X, (float)(_Y + height));
        GL11.glTexCoord2f((float)1.0f, (float)1.0f);
        GL11.glVertex2f((float)(_X + width), (float)(_Y + height));
        GL11.glTexCoord2f((float)1.0f, (float)0.0f);
        GL11.glVertex2f((float)(_X + width), (float)_Y);
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
        GL11.glEnable((int)2884);
        GL11.glPopMatrix();
    }
}

