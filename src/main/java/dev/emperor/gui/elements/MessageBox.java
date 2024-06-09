package dev.emperor.gui.elements;

import dev.emperor.gui.clickgui.book.RippleAnimation;
import dev.emperor.utils.render.RenderUtil;
import dev.emperor.utils.render.RoundedUtils;
import dev.emperor.utils.render.animation.Animation;
import dev.emperor.utils.render.animation.Direction;
import dev.emperor.utils.render.animation.impl.DecelerateAnimation;
import dev.emperor.utils.render.fontRender.FontManager;
import dev.emperor.utils.render.shader.KawaseBloom;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class MessageBox {
    public float x;
    public float y;
    public String message;
    public float width;
    public float height;
    public Animation anim = new DecelerateAnimation(200, 1.0);
    private Animation hoverAnimation = new DecelerateAnimation(300, 1.0);
    private RippleAnimation ripple = new RippleAnimation();

    public MessageBox(String message, float width, float height) {
        this.message = message;
        this.width = width;
        this.height = height;
    }

    public void initGui() {
        this.anim.setDirection(Direction.BACKWARDS);
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        this.x = (float)sr.getScaledWidth() / 2.0f - this.width / 2.0f;
        this.y = (float)sr.getScaledHeight() / 2.0f - this.height / 2.0f;
    }

    public void draw(int mouseX, int mouseY) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        RenderUtil.scaleStart((float)sr.getScaledWidth() / 2.0f, (float)sr.getScaledHeight() / 2.0f, (float)this.anim.getOutput());
        KawaseBloom.shadow2(() -> RoundedUtils.drawRound(this.x, this.y, this.width, this.height, 4.0f, new Color(0, 0, 0, 230)));
        RoundedUtils.drawRound(this.x, this.y, this.width, this.height, 4.0f, new Color(255, 255, 255, 255));
        FontManager.arial16.drawString("Window", this.x + 5.0f, this.y + 4.0f, 0);
        FontManager.arial16.drawString(this.message, this.x + this.width / 2.0f - (float)(FontManager.arial16.getStringWidth(this.message) / 2), this.y + this.height / 2.0f - 7.0f, 0);
        boolean hovered = RenderUtil.isHovering(this.x + 4.0f, this.y + this.height - 20.0f, this.width - 8.0f, 14.0f, mouseX, mouseY);
        this.hoverAnimation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);
        RoundedUtils.drawRound(this.x + 4.0f, this.y + this.height - 20.0f, this.width - 8.0f, 14.0f, 3.0f, new Color(0, 0, 0, (int)(this.hoverAnimation.getOutput() * 50.0)));
        this.ripple.draw(() -> RoundedUtils.drawRound(this.x + 4.0f, this.y + this.height - 20.0f, this.width - 8.0f, 14.0f, 3.0f, new Color(255, 255, 255, 255)));
        FontManager.arial16.drawString("\u786e\u5b9a", this.x + this.width / 2.0f - (float)(FontManager.arial16.getStringWidth("\u786e\u5b9a") / 2), this.y + this.height - 16.0f, 0);
        RenderUtil.scaleEnd();
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        this.ripple.mouseClicked(mouseX, mouseY);
        if (this.anim.finished(Direction.FORWARDS) && RenderUtil.isHovering(this.x + this.width / 2.0f - this.width / 4.0f, this.y + this.height - 18.0f, this.x + this.width / 2.0f + this.width / 4.0f, this.y + this.height - 3.0f, mouseX, mouseY) && mouseButton == 0) {
            this.anim.setDirection(Direction.BACKWARDS);
        }
    }
}

