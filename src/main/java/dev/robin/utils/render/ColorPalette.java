package dev.robin.utils.render;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class ColorPalette {
    public static void draw(int x2, int y2, int width, int height) {
        int x1;
        BufferedImage image = new BufferedImage(360, 100, 2);
        for (int H2 = 0; H2 <= 360; H2 += 10) {
            for (int B = 0; B <= 100; B += 10) {
                int color = Color.getHSBColor((float)H2 / 360.0f, 1.0f, (float)B / 100.0f).getRGB();
                x1 = H2 / 10;
                int y1 = 10 - B / 10;
                image.setRGB(x1, y1, color);
            }
        }
        int textureId = TextureUtil.glGenTextures();
        GL11.glBindTexture((int)3553, (int)textureId);
        ByteBuffer buffer = BufferUtils.createByteBuffer((int)(image.getWidth() * image.getHeight() * 4));
        for (int y1 = 0; y1 < image.getHeight(); ++y1) {
            for (x1 = 0; x1 < image.getWidth(); ++x1) {
                int pixel = image.getRGB(x1, y1);
                buffer.put((byte)(pixel >> 16 & 0xFF));
                buffer.put((byte)(pixel >> 8 & 0xFF));
                buffer.put((byte)(pixel & 0xFF));
                buffer.put((byte)(pixel >> 24 & 0xFF));
            }
        }
        buffer.flip();
        GL11.glTexImage2D((int)3553, (int)0, (int)6408, (int)image.getWidth(), (int)image.getHeight(), (int)0, (int)6408, (int)5121, (ByteBuffer)buffer);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x2, (float)y2, (float)0.0f);
        GL11.glBindTexture((int)3553, (int)textureId);
        GL11.glBegin((int)7);
        GL11.glTexCoord2f((float)0.0f, (float)0.0f);
        GL11.glVertex3f((float)0.0f, (float)0.0f, (float)0.0f);
        GL11.glTexCoord2f((float)1.0f, (float)0.0f);
        GL11.glVertex3f((float)width, (float)0.0f, (float)0.0f);
        GL11.glTexCoord2f((float)1.0f, (float)1.0f);
        GL11.glVertex3f((float)width, (float)height, (float)0.0f);
        GL11.glTexCoord2f((float)0.0f, (float)1.0f);
        GL11.glVertex3f((float)0.0f, (float)height, (float)0.0f);
        GL11.glEnd();
        GL11.glPopMatrix();
    }
}

