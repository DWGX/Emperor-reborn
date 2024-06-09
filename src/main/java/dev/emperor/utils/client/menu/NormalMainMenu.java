package dev.emperor.utils.client.menu;

import dev.emperor.Client;
import dev.emperor.gui.altmanager.GuiAltManager;
import dev.emperor.utils.render.RenderUtil;
import dev.emperor.utils.render.fontRender.FontManager;
import java.awt.Color;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class NormalMainMenu
extends GuiScreen {
    private float currentX;
    private float currentY;
    private static String str;
    private int alpha;

    public NormalMainMenu() {
        if (str == null) {
            str = "i like fadouse";
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        ScaledResolution sr = new ScaledResolution(this.mc);
        Gui.drawRect(10.0, 10.0, sr.getScaledWidth() + 10, sr.getScaledHeight() + 10, Color.BLACK.getAlpha());
        int w2 = sr.getScaledWidth();
        int h = sr.getScaledHeight();
        double halfW = sr.getScaledWidth_double() / 2.0;
        double halfH = sr.getScaledHeight_double() / 2.0;
        GlStateManager.pushMatrix();
        this.currentX = (float)((double)this.currentX + ((double)mouseX - halfW - (double)this.currentX) / (double)sr.getScaleFactor() * (double)0.3f);
        this.currentY = (float)((double)this.currentY + ((double)mouseY - halfH - (double)this.currentY) / (double)sr.getScaleFactor() * (double)0.3f);
        GlStateManager.translate(this.currentX / 40.0f, this.currentY / 40.0f, 0.0f);
        ScaledResolution res = new ScaledResolution(this.mc);
        RenderUtil.drawImage(new ResourceLocation("express/Background.jpg"), 0, 0, res.getScaledWidth(), res.getScaledHeight());
        int PZ0 = 20;
        int PZ1 = PZ0 * 2;
        Gui.drawScaledCustomSizeModalRect(-PZ0, -PZ0, 0.0f, 0.0f, w2 + PZ1, h + PZ1, w2 + PZ1, h + PZ1, (float)(w2 + PZ1), (float)(h + PZ1));
        GlStateManager.bindTexture(0);
        for (int i = 0; i < 4; ++i) {
            int xd = i * 22;
            boolean hover = mouseX > this.width / 2 - 60 && mouseX < this.width / 2 + 60 && mouseY > this.height / 2 - 40 + xd && mouseY < this.height / 2 - 20 + xd;
            String str = "";
            switch (i) {
                case 0: {
                    str = "Single Player";
                    break;
                }
                case 1: {
                    str = "Multi Player";
                    break;
                }
                case 2: {
                    str = "Options";
                    break;
                }
                case 3: {
                    str = "AltManager";
                }
            }
            RenderUtil.drawRect(this.width / 2 - 60, this.height / 2 - 40 + xd, this.width / 2 + 60, this.height / 2 - 20 + xd, new Color(0, 0, 0, hover ? 120 : 80).getAlpha());
            FontManager.Tahoma20.drawCenteredString(str, this.width / 2, this.height / 2 - 33 + xd, -1);
        }
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GL11.glEnable((int)2881);
        GL11.glDisable((int)2881);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        FontManager.Tahoma18.drawCenteredString("Null " + Client.instance.getUser(), this.width / 2, 5.0f, -1);
        FontManager.Tahoma18.drawString("01smX3 " + Client.instance.getVersion() + " <" + str + "\u00a7r>", 5.0f, this.height - 13, -1);
        CopyOnWriteArrayList<String> strs = new CopyOnWriteArrayList<String>();
        strs.add("none");
        strs.sort((o1, o2) -> FontManager.Tahoma14.getStringWidth((String)o2) - FontManager.Tahoma14.getStringWidth((String)o1));
        FontManager.Tahoma14.drawString(String.valueOf(Client.instance.getVersion()) + " SkidLog:", 5.0f, 5.0f, -1);
        int y2 = 15;
        for (String str : strs) {
            FontManager.Tahoma14.drawString(str, 5.0f, y2, -1);
            y2 += 9;
        }
        GlStateManager.popMatrix();
        this.alpha -= 0;
        if (this.alpha < 0) {
            this.alpha = 0;
        }
        RenderUtil.drawRect(0.0, 0.0, this.width, this.height, new Color(0, 0, 0, this.alpha).getRGB());
        if (!Client.instance.isLogged()) {
            Client.instance.setLogged(true);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        block6: for (int i = 0; i < 4; ++i) {
            int xd = i * 22;
            boolean hover = mouseX > this.width / 2 - 60 && mouseX < this.width / 2 + 60 && mouseY > this.height / 2 - 40 + xd && mouseY < this.height / 2 - 20 + xd;
            boolean bl = hover;
            if (!hover || mouseButton != 0) continue;
            switch (i) {
                case 0: {
                    this.mc.displayGuiScreen(new GuiSelectWorld(this));
                    continue block6;
                }
                case 1: {
                    this.mc.displayGuiScreen(new GuiMultiplayer(this));
                    continue block6;
                }
                case 2: {
                    this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                }
                case 3: {
                    this.mc.displayGuiScreen(new GuiAltManager(this));
                }
            }
        }
    }

    public static void setStr(String state) {
        str = state;
    }

    public void drawImage(ResourceLocation image, int x2, int y2, int width, int height) {
        this.mc.getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x2, y2, 0.0f, 0.0f, width, height, (float)width, (float)height);
        GlStateManager.bindTexture(0);
    }
}

