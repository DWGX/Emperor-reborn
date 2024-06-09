package dev.emperor.gui.elements;

import dev.emperor.gui.clickgui.book.RippleAnimation;
import dev.emperor.utils.client.TimeUtil;
import dev.emperor.utils.client.menu.Screen;
import dev.emperor.utils.misc.MouseUtils;
import dev.emperor.utils.render.RoundedUtils;
import dev.emperor.utils.render.animation.Animation;
import dev.emperor.utils.render.animation.Direction;
import dev.emperor.utils.render.animation.impl.DecelerateAnimation;
import dev.emperor.utils.render.fontRender.FontManager;
import dev.emperor.utils.render.shader.KawaseBloom;
import java.awt.Color;

public class MDUIButton
implements Screen {
    public final String text;
    public final int id;
    private Animation hoverAnimation;
    public float x;
    public float y;
    public float width;
    public float height;
    public Runnable clickAction;
    private RippleAnimation ripple = new RippleAnimation();
    public final Color color;
    private TimeUtil timer = new TimeUtil();

    public MDUIButton(String text, int id, Color color) {
        this.text = text;
        this.id = id;
        this.color = color;
    }

    @Override
    public void initGui() {
        this.hoverAnimation = new DecelerateAnimation(300, 1.0);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        boolean hovered = MouseUtils.isHovering(this.x, this.y, this.width, this.height, mouseX, mouseY);
        this.hoverAnimation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);
        KawaseBloom.shadow(() -> RoundedUtils.drawRound(this.x, this.y + 2.0f, this.width, this.height - 2.0f, 4.0f, new Color(0, 0, 0, (int)(200.0 * this.hoverAnimation.getOutput()))));
        RoundedUtils.drawRound(this.x, this.y, this.width, this.height, 4.0f, this.color);
        this.ripple.draw(() -> RoundedUtils.drawRound(this.x, this.y, this.width, this.height, 4.0f, new Color(255, 255, 255)));
        FontManager.arial20.drawCenteredString(this.text, this.x + this.width / 2.0f, this.y + FontManager.arial20.getMiddleOfBox(this.height) + 2.0f, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean hovered = MouseUtils.isHovering(this.x, this.y, this.width, this.height, mouseX, mouseY);
        this.ripple.mouseClicked(mouseX, mouseY);
        if (hovered && this.timer.hasReached(200.0)) {
            this.clickAction.run();
            this.timer.reset();
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
    }
}

