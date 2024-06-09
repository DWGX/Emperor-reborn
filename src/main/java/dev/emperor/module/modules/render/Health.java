package dev.emperor.module.modules.render;

import dev.emperor.event.EventTarget;
import dev.emperor.event.rendering.EventRender2D;
import dev.emperor.event.world.EventUpdate;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.utils.render.Colors;
import java.awt.Color;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Random;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public final class Health
extends Module {
    private final DecimalFormat decimalFormat = new DecimalFormat("0.#", new DecimalFormatSymbols(Locale.ENGLISH));
    private final Random random = new Random();
    private int width;

    public Health() {
        super("Health", Category.Render);
    }

    @EventTarget
    public void onRenderGuiEvent(EventUpdate event) {
        if (Health.mc.currentScreen instanceof GuiInventory || Health.mc.currentScreen instanceof GuiChest || Health.mc.currentScreen instanceof GuiContainerCreative) {
            this.renderHealth();
        }
    }

    @EventTarget
    public void onRender2DEvent(EventRender2D event) {
        if (!(Health.mc.currentScreen instanceof GuiInventory) && !(Health.mc.currentScreen instanceof GuiChest)) {
            this.renderHealth();
        }
    }

    private void renderHealth() {
        boolean flag;
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        GuiScreen screen = Health.mc.currentScreen;
        float absorptionHealth = Health.mc.thePlayer.getAbsorptionAmount();
        String string = this.decimalFormat.format(Health.mc.thePlayer.getHealth() / 2.0f) + "\u00a7c\u2764 " + (absorptionHealth <= 0.0f ? "" : "\u00a7e" + this.decimalFormat.format(absorptionHealth / 2.0f) + "\u00a76\u2764");
        int offsetY = 0;
        if (Health.mc.thePlayer.getHealth() >= 0.0f && Health.mc.thePlayer.getHealth() < 10.0f || Health.mc.thePlayer.getHealth() >= 10.0f && Health.mc.thePlayer.getHealth() < 100.0f) {
            this.width = 3;
        }
        if (screen instanceof GuiInventory) {
            offsetY = 70;
        } else if (screen instanceof GuiContainerCreative) {
            offsetY = 80;
        } else if (screen instanceof GuiChest) {
            offsetY = ((GuiChest)screen).ySize / 2 - 15;
        }
        int x2 = new ScaledResolution(mc).getScaledWidth() / 2 - this.width;
        int y2 = new ScaledResolution(mc).getScaledHeight() / 2 + 25 + offsetY;
        Color color = Colors.blendColors(new float[]{0.0f, 0.5f, 1.0f}, new Color[]{new Color(255, 37, 0), Color.YELLOW, Color.GREEN}, Health.mc.thePlayer.getHealth() / Health.mc.thePlayer.getMaxHealth());
        Health.mc.fontRendererObj.drawString(string, absorptionHealth > 0.0f ? (float)x2 - 15.5f : (float)x2 - 3.5f, y2, color.getRGB(), true);
        GL11.glPushMatrix();
        mc.getTextureManager().bindTexture(Gui.icons);
        this.random.setSeed((long)Health.mc.ingameGUI.updateCounter * 312871L);
        float width = (float)scaledResolution.getScaledWidth() / 2.0f - Health.mc.thePlayer.getMaxHealth() / 2.5f * 10.0f / 2.0f;
        float maxHealth = Health.mc.thePlayer.getMaxHealth();
        int lastPlayerHealth = Health.mc.ingameGUI.lastPlayerHealth;
        int healthInt = MathHelper.ceiling_float_int(Health.mc.thePlayer.getHealth());
        int l2 = -1;
        boolean bl = flag = Health.mc.ingameGUI.healthUpdateCounter > (long)Health.mc.ingameGUI.updateCounter && (Health.mc.ingameGUI.healthUpdateCounter - (long)Health.mc.ingameGUI.updateCounter) / 3L % 2L == 1L;
        if (Health.mc.thePlayer.isPotionActive(Potion.regeneration)) {
            l2 = Health.mc.ingameGUI.updateCounter % MathHelper.ceiling_float_int(maxHealth + 5.0f);
        }
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        for (int i6 = MathHelper.ceiling_float_int(maxHealth / 2.0f) - 1; i6 >= 0; --i6) {
            int xOffset = 16;
            if (Health.mc.thePlayer.isPotionActive(Potion.poison)) {
                xOffset += 36;
            } else if (Health.mc.thePlayer.isPotionActive(Potion.wither)) {
                xOffset += 72;
            }
            int k3 = 0;
            if (flag) {
                k3 = 1;
            }
            float renX = width + (float)(i6 % 10 * 8);
            float renY = (float)scaledResolution.getScaledHeight() / 2.0f + 15.0f + (float)offsetY;
            if (healthInt <= 4) {
                renY += (float)this.random.nextInt(2);
            }
            if (i6 == l2) {
                renY -= 2.0f;
            }
            int yOffset = 0;
            if (Health.mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
                yOffset = 5;
            }
            Gui.drawTexturedModalRect(renX, renY, 16 + k3 * 9, 9 * yOffset, 9, 9);
            if (flag) {
                if (i6 * 2 + 1 < lastPlayerHealth) {
                    Gui.drawTexturedModalRect(renX, renY, xOffset + 54, 9 * yOffset, 9, 9);
                }
                if (i6 * 2 + 1 == lastPlayerHealth) {
                    Gui.drawTexturedModalRect(renX, renY, xOffset + 63, 9 * yOffset, 9, 9);
                }
            }
            if (i6 * 2 + 1 < healthInt) {
                Gui.drawTexturedModalRect(renX, renY, xOffset + 36, 9 * yOffset, 9, 9);
            }
            if (i6 * 2 + 1 != healthInt) continue;
            Gui.drawTexturedModalRect(renX, renY, xOffset + 45, 9 * yOffset, 9, 9);
        }
        GL11.glPopMatrix();
    }
}

