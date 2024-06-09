package dev.robin.utils.render.shader;

import dev.robin.Client;
import dev.robin.utils.client.MathUtil;
import dev.robin.utils.render.RenderUtil;
import dev.robin.utils.render.ShaderUtil;
import dev.robin.utils.render.StencilUtil;
import java.nio.FloatBuffer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

public class GaussianBlur {
    private static final ShaderUtil gaussianBlur = new ShaderUtil("Tenacity/Shaders/gaussian.frag");
    private static Framebuffer framebuffer = new Framebuffer(1, 1, false);

    private static void setupUniforms(float dir1, float dir2, float radius) {
        gaussianBlur.setUniformi("textureIn", 0);
        gaussianBlur.setUniformf("texelSize", 1.0f / (float)Client.mc.displayWidth, 1.0f / (float)Client.mc.displayHeight);
        gaussianBlur.setUniformf("direction", dir1, dir2);
        gaussianBlur.setUniformf("radius", radius);
        FloatBuffer weightBuffer = BufferUtils.createFloatBuffer((int)256);
        int i = 0;
        while ((float)i <= radius) {
            weightBuffer.put(MathUtil.calculateGaussianValue(i, radius / 2.0f));
            ++i;
        }
        weightBuffer.rewind();
        GL20.glUniform1fv((int)gaussianBlur.getUniform("weights"), (FloatBuffer)weightBuffer);
    }

    public static void startBlur() {
        StencilUtil.initStencilToWrite();
    }

    public static void endBlur(float radius, float compression) {
        StencilUtil.readStencilBuffer(1);
        framebuffer = RenderUtil.createFrameBuffer(framebuffer);
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(false);
        gaussianBlur.init();
        GaussianBlur.setupUniforms(compression, 0.0f, radius);
        RenderUtil.bindTexture(Client.mc.getFramebuffer().framebufferTexture);
        ShaderUtil.drawQuads();
        framebuffer.unbindFramebuffer();
        gaussianBlur.unload();
        Client.mc.getFramebuffer().bindFramebuffer(false);
        gaussianBlur.init();
        GaussianBlur.setupUniforms(0.0f, compression, radius);
        RenderUtil.bindTexture(GaussianBlur.framebuffer.framebufferTexture);
        ShaderUtil.drawQuads();
        gaussianBlur.unload();
        StencilUtil.uninitStencilBuffer();
        RenderUtil.resetColor();
        GlStateManager.bindTexture(0);
    }
}

