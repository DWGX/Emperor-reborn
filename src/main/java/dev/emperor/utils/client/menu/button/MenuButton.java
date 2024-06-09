package dev.emperor.utils.client.menu.button;

import dev.emperor.gui.altmanager.ColorUtils;
import dev.emperor.utils.client.menu.Screen;
import dev.emperor.utils.misc.MouseUtils;
import dev.emperor.utils.render.RoundedUtils;
import dev.emperor.utils.render.animation.Animation;
import dev.emperor.utils.render.animation.Direction;
import dev.emperor.utils.render.animation.impl.DecelerateAnimation;
import dev.emperor.utils.render.fontRender.FontManager;
import java.awt.Color;

public class MenuButton
implements Screen {
    public final String text;
    private Animation hoverAnimation;
    public float x;
    public float y;
    public float width;
    public float height;
    public Runnable clickAction;

    public MenuButton(String text) {
        this.text = text;
    }

    @Override
    public void initGui() {
        this.hoverAnimation = new DecelerateAnimation(500, 1.0);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        boolean hovered = MouseUtils.isHovering(this.x, this.y, this.width, this.height, mouseX, mouseY);
        this.hoverAnimation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);
        Color rectColor = new Color(35, 37, 43, 102);
        rectColor = ColorUtils.interpolateColorC(rectColor, ColorUtils.brighter(rectColor, 0.4f), (float)this.hoverAnimation.getOutput());
        RoundedUtils.drawRoundOutline(this.x, this.y, this.width, this.height, 12.0f, 1.0f, rectColor, new Color(30, 30, 30, 100));
        FontManager.arial20.drawCenteredString(this.text, this.x + this.width / 2.0f, this.y + FontManager.arial20.getMiddleOfBox(this.height) + 2.0f, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean hovered = MouseUtils.isHovering(this.x, this.y, this.width, this.height, mouseX, mouseY);
        if (hovered) {
            this.clickAction.run();
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
    }
}

