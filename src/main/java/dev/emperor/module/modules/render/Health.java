package dev.emperor.module.modules.render;

import dev.emperor.event.EventTarget;
import dev.emperor.event.rendering.EventRender2D;
import dev.emperor.event.world.EventUpdate;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.utils.render.fontRender.FontManager;
import dev.emperor.utils.render.fontRender.RapeMasterFontManager;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.EntityLivingBase;

import java.awt.*;

public final class Health extends Module {
    private int prevHealth;
    private int prevTargetHealth;
    RapeMasterFontManager f16 = FontManager.arial16;
    public Health() {
        super("Health", Category.Render);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (Health.mc.currentScreen instanceof GuiInventory || Health.mc.currentScreen instanceof GuiChest || Health.mc.currentScreen instanceof GuiContainerCreative) {
            this.renderPlayerHealth();
            this.renderTargetHealth();
        }
    }

    @EventTarget
    public void onRender2D(EventRender2D event) {
        if (!(Health.mc.currentScreen instanceof GuiInventory) && !(Health.mc.currentScreen instanceof GuiChest)) {
            this.renderPlayerHealth();
            this.renderTargetHealth();
        }
    }

    private void renderPlayerHealth() {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        float healthPercent = Health.mc.thePlayer.getHealth() / Health.mc.thePlayer.getMaxHealth();
        int barWidth = 100;
        int barHeight = 10;
        int barX = (scaledResolution.getScaledWidth() - barWidth) / 2;
        int barY = scaledResolution.getScaledHeight() / 2 + 40;

        drawRect(barX, barY, barX + barWidth, barY + barHeight, new Color(32, 32, 32, 200).getRGB());

        int healthBarWidth = (int) (barWidth * healthPercent);
        int animatedHealthBarWidth = interpolate(prevHealth, healthBarWidth);
        Color healthColor = interpolateColor(healthPercent);
        drawRect(barX, barY, barX + animatedHealthBarWidth, barY + barHeight, healthColor.getRGB());

        String healthText = (int)(healthPercent * 100) + "%";
        f16.drawStringWithShadow(healthText, barX + (float) (barWidth - Health.mc.fontRendererObj.getStringWidth(healthText)) / 2, barY + 2, Color.WHITE.getRGB());

        prevHealth = animatedHealthBarWidth;
    }

    private void renderTargetHealth() {
        EntityLivingBase target = Health.mc.pointedEntity instanceof EntityLivingBase ? (EntityLivingBase) Health.mc.pointedEntity : null;

        if (target != null && target != Health.mc.thePlayer) {
            ScaledResolution scaledResolution = new ScaledResolution(mc);
            float targetHealthPercent = target.getHealth() / target.getMaxHealth();
            int barWidth = 100;
            int barHeight = 10;
            int barX = (scaledResolution.getScaledWidth() - barWidth) / 2;
            int barY = scaledResolution.getScaledHeight() / 2 - 10;

            drawRect(barX, barY, barX + barWidth, barY + barHeight, new Color(32, 32, 32, 200).getRGB());

            int healthBarWidth = (int) (barWidth * targetHealthPercent);
            int animatedHealthBarWidth = interpolate(prevTargetHealth, healthBarWidth);
            Color healthColor = interpolateColor(targetHealthPercent);
            drawRect(barX, barY, barX + animatedHealthBarWidth, barY + barHeight, healthColor.getRGB());

            String healthText = (int)(targetHealthPercent * 100) + "%";
            f16.drawStringWithShadow(healthText, barX + (float) (barWidth - Health.mc.fontRendererObj.getStringWidth(healthText)) / 2, barY + 2, Color.WHITE.getRGB());

            prevTargetHealth = animatedHealthBarWidth;
        }
    }

    private void drawRect(int left, int top, int right, int bottom, int color) {
        Gui.drawRect(left, top, right, bottom, color);
    }

    private Color interpolateColor(float ratio) {
        int r = (int) (Color.RED.getRed() + (Color.GREEN.getRed() - Color.RED.getRed()) * ratio);
        int g = (int) (Color.RED.getGreen() + (Color.GREEN.getGreen() - Color.RED.getGreen()) * ratio);
        int b = (int) (Color.RED.getBlue() + (Color.GREEN.getBlue() - Color.RED.getBlue()) * ratio);
        return new Color(r, g, b);
    }

    private int interpolate(int prevValue, int targetValue) {
        if (prevValue < targetValue) {
            prevValue += 1;
        } else if (prevValue > targetValue) {
            prevValue -= 1;
        }
        return prevValue;
    }
}
