package dev.robin.gui.ui;

import dev.robin.event.EventManager;
import dev.robin.event.EventTarget;
import dev.robin.event.rendering.EventRender2D;
import dev.robin.gui.ui.UiModule;
import dev.robin.gui.ui.modules.Debug;
import dev.robin.gui.ui.modules.Information;
import dev.robin.gui.ui.modules.ModuleList;
import dev.robin.gui.ui.modules.PotionsInfo;
import dev.robin.gui.ui.modules.TargetHud;
import dev.robin.utils.render.RenderUtil;
import java.awt.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;

public class UiManager {
    public final List<UiModule> UiModules = new ArrayList<UiModule>();
    private final Minecraft mc = Minecraft.getMinecraft();
    public double mouseX;
    public double mouseY;

    public void init() {
        EventManager.register(this);
        System.out.println("Init UiModules...");
        this.addModule(new ModuleList());
        this.addModule(new Debug());
        this.addModule(new TargetHud());
        this.addModule(new Information());
        this.addModule(new PotionsInfo());
    }

    @EventTarget
    public void moveUi(EventRender2D event) {
        if (!this.mc.ingameGUI.getChatGUI().getChatOpen()) {
            return;
        }
        for (UiModule m : this.UiModules) {
            if (!m.getState()) continue;
            double xpos = m.getPosX();
            double ypos = m.getPosY();
            double mwidth = m.getWidth();
            double mheight = m.getHeight();
            double mousex = this.mouseX;
            double mousey = this.mouseY;
            if (mousex > xpos && mousey > ypos && mousex < xpos + mwidth && mousey < ypos + mheight && Mouse.isButtonDown(0)) {
                RenderUtil.drawBorderedRect((float)xpos, (float)ypos, (float)(xpos + mwidth), (float)(ypos + mheight), 2.0f, new Color(225, 225, 225).getRGB(), 0);
                if (m.moveX == 0.0 && m.moveY == 0.0) {
                    m.moveX = (double)((float)this.mouseX) - xpos;
                    m.moveY = (double)((float)this.mouseY) - ypos;
                    continue;
                }
                double setX = this.mouseX - m.moveX;
                double setY = this.mouseY - m.moveY;
                setX = Math.min(Math.max(0.0, setX), (double)RenderUtil.width() - m.width);
                setY = Math.min(Math.max(0.0, setY), (double)RenderUtil.height() - m.height);
                m.setPosX(setX);
                m.setPosY(setY);
                continue;
            }
            if (m.moveX == 0.0 && m.moveY == 0.0) continue;
            m.moveX = 0.0;
            m.moveY = 0.0;
        }
    }

    public void addModule(UiModule module) {
        for (Field field : module.getClass().getDeclaredFields()) {
            field.setAccessible(true);
        }
        this.UiModules.add(module);
    }

    public UiModule getModule(String name) {
        for (UiModule m : this.UiModules) {
            if (!m.getName().equalsIgnoreCase(name)) continue;
            return m;
        }
        return null;
    }

    public UiModule getModule(Class<?> cls) {
        for (UiModule m : this.UiModules) {
            if (m.getClass() != cls) continue;
            return m;
        }
        return null;
    }

    public List<UiModule> getModules() {
        return this.UiModules;
    }
}

