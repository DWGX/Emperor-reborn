package dev.robin.gui.notification;

import dev.robin.gui.notification.NotificationManager;
import dev.robin.gui.notification.NotificationType;
import dev.robin.utils.misc.MinecraftInstance;
import dev.robin.utils.render.ColorUtil;
import dev.robin.utils.render.RenderUtil;
import dev.robin.utils.render.animation.AnimTimeUtil;
import dev.robin.utils.render.animation.Animation;
import dev.robin.utils.render.animation.impl.DecelerateAnimation;
import dev.robin.utils.render.fontRender.FontManager;
import java.awt.Color;
import net.minecraft.client.gui.Gui;

public class Notification
implements MinecraftInstance {
    private final NotificationType notificationType;
    private final String title;
    private final String description;
    private final float time;
    private final AnimTimeUtil timerUtil;
    private final Animation animation;

    public Notification(NotificationType type, String title, String description) {
        this(type, title, description, NotificationManager.getToggleTime());
    }

    public Notification(NotificationType type, String title, String description, float time) {
        this.title = title;
        this.description = description;
        this.time = (long)(time * 1000.0f);
        this.timerUtil = new AnimTimeUtil();
        this.notificationType = type;
        this.animation = new DecelerateAnimation(300, 1.0);
    }

    public void drawLettuce(float x2, float y2, float width, float height) {
        Color color = ColorUtil.applyOpacity(ColorUtil.interpolateColorC(Color.BLACK, this.getNotificationType().getColor(), 0.65f), 70.0f);
        float percentage = Math.min((float)this.timerUtil.getTime() / this.getTime(), 1.0f);
        Gui.drawRect(x2, y2, x2 + width, y2 + height, new Color(0, 0, 0, 70).getRGB());
        Gui.drawRect(x2, y2, x2 + width * percentage, y2 + height, color.getRGB());
        Color notificationColor = ColorUtil.applyOpacity(this.getNotificationType().getColor(), 70.0f);
        Color textColor = ColorUtil.applyOpacity(Color.WHITE, 80.0f);
        FontManager.icontestFont35.drawString(this.getNotificationType().getIcon(), x2 + 3.0f, y2 + FontManager.icontestFont35.getMiddleOfBox(height), notificationColor.getRGB());
        FontManager.arial20.drawString(this.getDescription(), x2 + 2.8f + (float)FontManager.icontestFont35.getStringWidth(this.getNotificationType().getIcon()) + 2.0f, y2 + 8.0f, textColor.getRGB());
    }

    public void blurLettuce(float x2, float y2, float width, float height, boolean glow) {
        Color color = ColorUtil.applyOpacity(ColorUtil.interpolateColorC(Color.BLACK, this.getNotificationType().getColor(), glow ? 0.65f : 0.0f), 70.0f);
        float percentage = Math.min((float)this.timerUtil.getTime() / this.getTime(), 1.0f);
        Gui.drawRect(x2, y2, x2 + width, y2 + height, Color.BLACK.getRGB());
        Gui.drawRect(x2, y2, x2 + width * percentage, y2 + height, color.getRGB());
        RenderUtil.resetColor();
    }

    public NotificationType getNotificationType() {
        return this.notificationType;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public float getTime() {
        return this.time;
    }

    public AnimTimeUtil getTimerUtil() {
        return this.timerUtil;
    }

    public Animation getAnimation() {
        return this.animation;
    }
}

