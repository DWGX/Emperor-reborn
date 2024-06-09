package dev.robin.gui;

import dev.robin.utils.render.RenderUtil;
import dev.robin.utils.render.RoundedUtils;
import dev.robin.utils.render.animation.Animation;
import dev.robin.utils.render.animation.Direction;
import dev.robin.utils.render.animation.impl.DecelerateAnimation;
import dev.robin.utils.render.fontRender.FontManager;
import dev.robin.utils.render.fontRender.RapeMasterFontManager;
import java.awt.Color;
import net.minecraft.client.gui.GuiScreen;

public class CustomMenuButton
extends GuiScreen {
    public final String text;
    private Animation displayAnimation;
    private Animation hoverAnimation = new DecelerateAnimation(500, 1.0);
    public float x;
    public float y;
    public float width;
    public float height;
    public Runnable clickAction;
    public RapeMasterFontManager font = FontManager.arial20;

    public CustomMenuButton(String text, Runnable clickAction) {
        this.text = text;
        this.displayAnimation = new DecelerateAnimation(1000, 255.0);
        this.font = FontManager.arial20;
        this.clickAction = clickAction;
    }

    public CustomMenuButton(String text) {
        this.text = text;
        this.displayAnimation = new DecelerateAnimation(1000, 255.0);
        this.font = FontManager.arial20;
    }

    @Override
    public void initGui() {
        this.hoverAnimation = new DecelerateAnimation(500, 1.0);
        this.displayAnimation.setDirection(Direction.FORWARDS);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float ticks) {
        boolean hovered = RenderUtil.isHovering(this.x, this.y, this.width, this.height, mouseX, mouseY);
        this.hoverAnimation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);
        Color rectColor = new Color(32, 32, 32, (int)(this.displayAnimation.getOutput() * Math.max(0.7, this.hoverAnimation.getOutput())));
        RoundedUtils.drawRound(this.x, this.y, this.width, this.height, 4.0f, rectColor);
        this.font.drawCenteredString(this.text, this.x + this.width / 2.0f, this.y + this.font.getMiddleOfBox(this.height) + 2.0f, new Color(255, 255, 255, (int)this.displayAnimation.getOutput()).getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean hovered = RenderUtil.isHovering(this.x, this.y, this.width, this.height, mouseX, mouseY);
        if (hovered) {
            this.clickAction.run();
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
    }

    @Override
    public void onGuiClosed() {
        this.displayAnimation.setDirection(Direction.BACKWARDS);
    }

    public String getText() {
        return this.text;
    }

    public Animation getDisplayAnimation() {
        return this.displayAnimation;
    }

    public Animation getHoverAnimation() {
        return this.hoverAnimation;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public Runnable getClickAction() {
        return this.clickAction;
    }

    public RapeMasterFontManager getFont() {
        return this.font;
    }

    public void setDisplayAnimation(Animation displayAnimation) {
        this.displayAnimation = displayAnimation;
    }

    public void setHoverAnimation(Animation hoverAnimation) {
        this.hoverAnimation = hoverAnimation;
    }

    public void setX(float x2) {
        this.x = x2;
    }

    public void setY(float y2) {
        this.y = y2;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setClickAction(Runnable clickAction) {
        this.clickAction = clickAction;
    }

    public void setFont(RapeMasterFontManager font) {
        this.font = font;
    }
}

