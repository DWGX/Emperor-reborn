package dev.emperor.utils.novoshader;

import dev.emperor.Client;
import org.lwjgl.opengl.GL20;

public final class OutlineShader
extends FramebufferShader {
    public static final OutlineShader OUTLINE_SHADER = new OutlineShader();

    public OutlineShader() {
        super("outline.frag");
    }

    @Override
    public void setupUniforms() {
        this.setupUniform("texture");
        this.setupUniform("texelSize");
        this.setupUniform("color");
        this.setupUniform("divider");
        this.setupUniform("radius");
        this.setupUniform("maxSample");
    }

    @Override
    public void updateUniforms() {
        GL20.glUniform1i((int)this.getUniform("texture"), (int)0);
        GL20.glUniform2f((int)this.getUniform("texelSize"), (float)(1.0f / (float)Client.mc.displayWidth * (this.radius * this.quality)), (float)(1.0f / (float)Client.mc.displayHeight * (this.radius * this.quality)));
        GL20.glUniform4f((int)this.getUniform("color"), (float)this.red, (float)this.green, (float)this.blue, (float)this.alpha);
        GL20.glUniform1f((int)this.getUniform("radius"), (float)this.radius);
    }
}

