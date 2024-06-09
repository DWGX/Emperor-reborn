package dev.emperor.utils.render.shader;

import dev.emperor.Client;
import dev.emperor.utils.client.MathUtil;
import dev.emperor.utils.render.RenderUtil;
import dev.emperor.utils.render.ShaderUtil;
import java.nio.FloatBuffer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

public class BloomUtil {
    public static ShaderUtil gaussianBloom = new ShaderUtil("express/shader/fragment/bloom.frag");
    public static Framebuffer framebuffer = new Framebuffer(1, 1, false);

    public static void renderBlur(int sourceTexture, int radius, int offset) {
        framebuffer = RenderUtil.createFrameBuffer(framebuffer);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.0f);
        GlStateManager.enableBlend();
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        FloatBuffer weightBuffer = BufferUtils.createFloatBuffer((int)256);
        for (int i = 0; i <= radius; ++i) {
            weightBuffer.put(MathUtil.calculateGaussianValue(i, radius));
        }
        weightBuffer.rewind();
        RenderUtil.setAlphaLimit(0.0f);
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        gaussianBloom.init();
        BloomUtil.setupUniforms(radius, offset, 0, weightBuffer);
        RenderUtil.bindTexture(sourceTexture);
        ShaderUtil.drawQuads();
        gaussianBloom.unload();
        framebuffer.unbindFramebuffer();
        Client.mc.getFramebuffer().bindFramebuffer(true);
        gaussianBloom.init();
        BloomUtil.setupUniforms(radius, 0, offset, weightBuffer);
        GL13.glActiveTexture((int)34000);
        RenderUtil.bindTexture(sourceTexture);
        GL13.glActiveTexture((int)33984);
        RenderUtil.bindTexture(BloomUtil.framebuffer.framebufferTexture);
        ShaderUtil.drawQuads();
        gaussianBloom.unload();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.enableAlpha();
        GlStateManager.bindTexture(0);
    }

    public static void setupUniforms(int radius, int directionX, int directionY, FloatBuffer weights) {
        gaussianBloom.setUniformi("inTexture", 0);
        gaussianBloom.setUniformi("textureToCheck", 16);
        gaussianBloom.setUniformf("radius", radius);
        gaussianBloom.setUniformf("texelSize", 1.0f / (float)Client.mc.displayWidth, 1.0f / (float)Client.mc.displayHeight);
        gaussianBloom.setUniformf("direction", directionX, directionY);
        GL20.glUniform1fv((int)gaussianBloom.getUniform("weights"), (FloatBuffer)weights);
    }
}

