package dev.robin.gui.ui.modules;

import dev.robin.event.EventTarget;
import dev.robin.event.rendering.EventRender2D;
import dev.robin.gui.ui.UiModule;
import dev.robin.module.modules.render.HUD;
import dev.robin.utils.render.fontRender.FontManager;
import dev.robin.utils.render.fontRender.RapeMasterFontManager;
import java.awt.Color;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;

public class Information
extends UiModule {
    public Information() {
        super("Information", 50.0, 20.0, 100.0, 20.0);
    }

    @EventTarget
    public void onRender2D(EventRender2D event) {
        RapeMasterFontManager f18 = FontManager.arial18;
        double positionX = this.getPosX();
        double positionY = this.getPosY() + 12.0;
        ArrayList<Integer> positionLongList = new ArrayList<Integer>();
        positionLongList.add(f18.getStringWidth("X: " + (int)Information.mc.thePlayer.posX + " "));
        positionLongList.add(f18.getStringWidth("Y: " + (int)Information.mc.thePlayer.posY + " "));
        positionLongList.add(f18.getStringWidth("Z: " + (int)Information.mc.thePlayer.posZ + " "));
        int mainTextColor = HUD.color(1).getRGB();
        f18.drawStringWithShadow("X", positionX, positionY, mainTextColor);
        f18.drawStringWithShadow(": " + (int)Information.mc.thePlayer.posX, positionX + (double)f18.getStringWidth("X"), positionY, Color.white.getRGB());
        f18.drawStringWithShadow("Y", positionX + (double)((Integer)positionLongList.get(0)).intValue(), positionY, mainTextColor);
        f18.drawStringWithShadow(": " + (int)Information.mc.thePlayer.posY, positionX + (double)f18.getStringWidth("Y") + (double)((Integer)positionLongList.get(0)).intValue(), positionY, Color.white.getRGB());
        f18.drawStringWithShadow("Z", positionX + (double)((Integer)positionLongList.get(1)).intValue() + (double)((Integer)positionLongList.get(0)).intValue(), positionY, mainTextColor);
        f18.drawStringWithShadow(": " + (int)Information.mc.thePlayer.posZ, positionX + (double)f18.getStringWidth("Z") + (double)((Integer)positionLongList.get(1)).intValue() + (double)((Integer)positionLongList.get(0)).intValue(), positionY, Color.white.getRGB());
        f18.drawStringWithShadow("Fps", positionX, positionY -= f18.getStringHeight() + 2.0, mainTextColor);
        f18.drawStringWithShadow(": " + Minecraft.getDebugFPS(), positionX + (double)f18.getStringWidth("Fps"), positionY, Color.white.getRGB());
    }
}

