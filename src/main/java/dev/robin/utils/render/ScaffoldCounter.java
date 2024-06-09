package dev.robin.utils.render;

import dev.robin.Client;
import dev.robin.module.modules.world.Scaffold;
import dev.robin.utils.render.RenderUtil;
import dev.robin.utils.render.RoundedUtils;
import dev.robin.utils.render.animation.Animation;
import dev.robin.utils.render.animation.Direction;
import dev.robin.utils.render.animation.impl.EaseBackIn;
import dev.robin.utils.render.fontRender.FontManager;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class ScaffoldCounter {
    private static final Animation introAnimation = new EaseBackIn(450, 1.0, 1.5f);
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void drawDefault() {
        Scaffold Scaffold2 = Client.instance.moduleManager.getModule(Scaffold.class);
        if (!Scaffold2.getState()) {
            return;
        }
        introAnimation.setDirection(Scaffold2.getState() ? Direction.FORWARDS : Direction.BACKWARDS);
        GL11.glPushMatrix();
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        ScaffoldCounter.scale(mc);
        ItemStack stack = ScaffoldCounter.mc.thePlayer.inventory.getStackInSlot(ScaffoldCounter.mc.thePlayer.inventory.currentItem);
        try {
            if (ScaffoldCounter.mc.thePlayer.inventory.getStackInSlot(Scaffold2.getBlockSlot()) != null) {
                stack = ScaffoldCounter.mc.thePlayer.inventory.getStackInSlot(Scaffold2.getBlockSlot());
            }
        }
        catch (RuntimeException e) {
            stack = new ItemStack(Blocks.barrier);
        }
        int height = sr.getScaledHeight() - 90;
        float x2 = (float)sr.getScaledWidth() / 2.0f - 1.0f;
        float y2 = height + 19;
        float y1 = height;
        if (Scaffold2.getBlockCount() >= 10 && Scaffold2.getBlockCount() <= 99) {
            x2 = (float)((double)x2 - 3.5);
        } else if (Scaffold2.getBlockCount() >= 100 && Scaffold2.getBlockCount() <= 999) {
            x2 -= 4.0f;
        } else if (Scaffold2.getBlockCount() >= 1 && Scaffold2.getBlockCount() <= 9) {
            x2 -= 1.0f;
        }
        RenderUtil.scaleStart((float)sr.getScaledWidth() / 2.0f - 13.0f - (float)(FontManager.arial20.getStringWidth("Blocks: " + Integer.toString(Scaffold2.getBlockCount())) / 2) + (float)((FontManager.arial20.getStringWidth("Blocks: " + Integer.toString(Scaffold2.getBlockCount())) / 2 + 35) / 2), y1 - 6.0f + 10.0f, (float)introAnimation.getOutput());
        if (stack != null && stack.getItem() instanceof ItemBlock && Scaffold2.isValid(stack.getItem()) && !introAnimation.finished(Direction.BACKWARDS)) {
            RoundedUtils.drawRound((float)sr.getScaledWidth() / 2.0f - 13.0f - (float)(FontManager.arial20.getStringWidth("Blocks: " + Integer.toString(Scaffold2.getBlockCount())) / 2), y1 - 6.0f, 35 + FontManager.arial20.getStringWidth("Blocks: " + Integer.toString(Scaffold2.getBlockCount())) / 2, 20.0f, 2.0f, new Color(0, 0, 0, 80));
            FontManager.arial20.drawCenteredStringWithShadow("Blocks: " + Integer.toString(Scaffold2.getBlockCount()), (float)sr.getScaledWidth() / 2.0f - 8.0f, y2 - 19.0f, -1);
        }
        RenderUtil.resetColor();
        RenderUtil.scaleEnd();
        GL11.glPopMatrix();
    }

    public static void drawDefaultBloom() {
    }

    public static void drawClassic() {
    }

    public static void drawClassicBloom() {
    }

    public static void scale(Minecraft mc) {
        switch (mc.gameSettings.guiScale) {
            case 0: {
                GlStateManager.scale(0.5, 0.5, 0.5);
                break;
            }
            case 1: {
                GlStateManager.scale(2.0f, 2.0f, 2.0f);
                break;
            }
            case 3: {
                GlStateManager.scale(0.6666666666666667, 0.6666666666666667, 0.6666666666666667);
            }
        }
    }
}

