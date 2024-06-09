package dev.emperor.gui.ui.modules;

import dev.emperor.event.EventTarget;
import dev.emperor.event.rendering.EventRender2D;
import dev.emperor.gui.ui.UiModule;
import dev.emperor.utils.render.RenderUtil;
import dev.emperor.utils.render.fontRender.FontManager;
import dev.emperor.utils.render.fontRender.RapeMasterFontManager;
import java.awt.Color;
import net.minecraft.client.entity.EntityPlayerSP;

public class Debug
extends UiModule {
    public Debug() {
        super("Debug", 20.0, 60.0, 150.0, 200.0);
    }

    @EventTarget
    public void onRender2D(EventRender2D event) {
        EntityPlayerSP player = Debug.mc.thePlayer;
        double x2 = this.posX * (double)RenderUtil.width();
        double y2 = this.posY * (double)RenderUtil.height();
        RapeMasterFontManager A16 = FontManager.arial16;
        RenderUtil.drawRect(x2, y2, x2 + 120.0, y2 + 200.0, new Color(20, 20, 20, 180).getRGB());
        y2 -= 5.0;
        A16.drawStringWithShadow("Health: " + this.toFloat(player.getHealth()), x2 += 5.0, y2 += 10.0, new Color(255, 255, 255).getRGB());
        A16.drawStringWithShadow("X:" + this.toFloat(player.posX) + " Y:" + this.toFloat(player.posY) + " Z:" + this.toFloat(player.posZ), x2, y2 += 10.0, new Color(255, 255, 255).getRGB());
        A16.drawStringWithShadow("Motion X:" + this.toDouble(player.motionX) + " Y:" + this.toDouble(player.motionY) + " Z:" + this.toDouble(player.motionZ), x2, y2 += 10.0, new Color(255, 255, 255).getRGB());
        A16.drawStringWithShadow("Hurt Time: " + player.hurtTime, x2, y2 += 10.0, new Color(255, 255, 255).getRGB());
        A16.drawStringWithShadow("Hurt ResistantTime Time: " + player.hurtResistantTime, x2, y2 += 10.0, new Color(255, 255, 255).getRGB());
        A16.drawStringWithShadow("Yaw: " + this.toFloat(player.rotationYaw) + " Pitch" + this.toFloat(player.rotationPitch), x2, y2 += 10.0, new Color(255, 255, 255).getRGB());
        A16.drawStringWithShadow("Head: " + this.toFloat(player.rotationYawHead) + " Body: " + this.toFloat(player.renderYawOffset), x2, y2 += 10.0, new Color(255, 255, 255).getRGB());
    }

    private float toFloat(double value) {
        return (float)((int)value) + (float)((int)(value * 10.0) % 10) / 10.0f;
    }

    private float toDouble(double value) {
        return (float)((int)value) + (float)((int)(value * 100.0) % 100) / 100.0f;
    }
}

