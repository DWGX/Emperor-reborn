package dev.robin.gui;

import dev.robin.utils.RegionalAbuseUtil;
import dev.robin.utils.client.menu.BetterMainMenu;
import dev.robin.utils.client.menu.utils.MainMenuBackground;
import dev.robin.utils.render.RenderUtil;
import dev.robin.utils.render.fontRender.FontManager;
import java.io.IOException;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

public class GuiConnectIRC
extends GuiScreen {
    private boolean started = false;

    public GuiConnectIRC() {
        try {
            this.shaderBackground = new MainMenuBackground("/assets/minecraft/express/shader/load.fsh");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initGui() {
        RegionalAbuseUtil.getAddressByIP();
        String country = RegionalAbuseUtil.country + "\u4eba";
    }

    @Override
    public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_) {
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        this.renderBackground();
        float defX = scaledResolution.getScaledWidth() / 2;
        FontManager.icontestFont90.drawCenteredString("T", defX, scaledResolution.getScaledHeight() / 2 - 100, -1);
        FontManager.arial20.drawCenteredString("Connecting to IRC server...", defX, scaledResolution.getScaledHeight() / 2 - 50, -1);
        RenderUtil.drawLoadingCircle(defX - 5.0f, scaledResolution.getScaledHeight() / 2);
        this.mc.displayGuiScreen(new BetterMainMenu());
        super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }
}

