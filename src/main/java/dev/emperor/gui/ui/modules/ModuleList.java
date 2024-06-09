package dev.emperor.gui.ui.modules;

import dev.emperor.Client;
import dev.emperor.event.EventTarget;
import dev.emperor.event.rendering.EventRender2D;
import dev.emperor.event.rendering.EventShader;
import dev.emperor.gui.ui.UiModule;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.modules.render.HUD;
import dev.emperor.utils.render.ColorUtil;
import dev.emperor.utils.render.RenderUtil;
import dev.emperor.utils.render.animation.Animation;
import dev.emperor.utils.render.animation.Direction;
import dev.emperor.utils.render.fontRender.FontManager;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;

public class ModuleList
extends UiModule {
    public List<Module> modules;

    public ModuleList() {
        super("ModuleList", RenderUtil.width(), 0.0, 100.0, 0.0);
    }

    @EventTarget
    public void blur(EventShader event) {
        double yOffset = 0.0;
        ScaledResolution sr = new ScaledResolution(mc);
        for (Module module : this.modules) {
            if (((Boolean)HUD.importantModules.getValue()).booleanValue() && module.getCategory() == Category.Render) continue;
            Animation moduleAnimation = module.getAnimation();
            if (!module.getState() && moduleAnimation.finished(Direction.BACKWARDS)) continue;
            String displayText = this.formatModule(module);
            double textWidth = FontManager.arial16.getStringWidth(displayText);
            double xValue = sr.getScaledWidth() - 10;
            boolean flip = xValue <= (double)((float)sr.getScaledWidth() / 2.0f);
            double x2 = flip ? xValue : (double)sr.getScaledWidth() - (textWidth + 3.0);
            double y2 = yOffset + 4.0;
            double heightVal = (Double)HUD.height.getValue() + 1.0;
            switch (((ANIM)((Object)HUD.animation.getValue())).name()) {
                case "MoveIn": {
                    if (flip) {
                        x2 -= Math.abs((moduleAnimation.getOutput() - 1.0) * ((double)sr.getScaledWidth() - (2.0 + textWidth)));
                        break;
                    }
                    x2 += Math.abs((moduleAnimation.getOutput() - 1.0) * (2.0 + textWidth));
                    break;
                }
                case "ScaleIn": {
                    RenderUtil.scaleStart((float)(x2 + (double)((float)FontManager.arial16.getStringWidth(displayText) / 2.0f)), (float)(y2 + heightVal / 2.0 - (double)((float)FontManager.arial16.getHeight() / 2.0f)), (float)moduleAnimation.getOutput());
                }
            }
            if (((Boolean)HUD.background.getValue()).booleanValue()) {
                Gui.drawRect3((float)(x2 - 2.0), (float)(y2 - 3.0), FontManager.arial16.getStringWidth(displayText) + 5, (float)heightVal, new Color(0, 0, 0).getRGB());
            }
            if (((ANIM)((Object)HUD.animation.getValue())).name() == "ScaleIn") {
                RenderUtil.scaleEnd();
            }
            yOffset += moduleAnimation.getOutput() * heightVal;
        }
    }

    private String formatModule(Module module) {
        String name = module.getName();
        name = name.replaceAll(" ", "");
        String formatText = "%s %s%s";
        String suffix = module.getSuffix();
        if (suffix == null || suffix.isEmpty()) {
            return name;
        }
        return String.format(formatText, new Object[]{name, EnumChatFormatting.GRAY, suffix});
    }

    @EventTarget
    public void onRender2D(EventRender2D event) {
        ArrayList<Module> moduleList = new ArrayList<Module>();
        moduleList.addAll(Client.instance.moduleManager.getModules());
        if (this.modules == null) {
            this.modules = moduleList;
            this.modules.removeIf(module -> module.getCategory() == Category.Render && (Boolean)HUD.importantModules.getValue() != false);
        }
        this.modules.sort(Comparator.comparingDouble(m -> {
            for (Module mod : moduleList) {
                String name = mod.getName() + (mod.getSuffix() != "" ? " " + mod.getSuffix() : "");
                return FontManager.arial16.getStringWidth(name);
            }
            return 0;
        }).reversed());
        double yOffset = 0.0;
        ScaledResolution sr = new ScaledResolution(mc);
        int count = 0;
        for (Module module2 : this.modules) {
            if (((Boolean)HUD.importantModules.getValue()).booleanValue() && module2.getCategory() == Category.Render) continue;
            Animation moduleAnimation = module2.getAnimation();
            moduleAnimation.setDirection(module2.getState() ? Direction.FORWARDS : Direction.BACKWARDS);
            if (!module2.getState() && moduleAnimation.finished(Direction.BACKWARDS)) continue;
            String displayText = this.formatModule(module2);
            double textWidth = FontManager.arial16.getStringWidth(displayText);
            double xValue = sr.getScaledWidth() - 10;
            boolean flip = xValue <= (double)((float)sr.getScaledWidth() / 2.0f);
            double x = flip ? xValue : (double)sr.getScaledWidth() - (textWidth + 3.0);
            float alphaAnimation = 1.0f;
            double y = yOffset + 4.0;
            double heightVal = (Double)HUD.height.getValue() + 1.0;
            switch (((ANIM)(HUD.animation.getValue())).name()) {
                case "MoveIn": {
                    if (flip) {
                        x -= Math.abs((moduleAnimation.getOutput() - 1.0) * ((double)sr.getScaledWidth() - (2.0 - textWidth)));
                        break;
                    }
                    x += Math.abs((moduleAnimation.getOutput() - 1.0) * (2.0 + textWidth));
                    break;
                }
                case "ScaleIn": {
                    RenderUtil.scaleStart((float)(x + (double)((float)FontManager.arial16.getStringWidth(displayText) / 2.0f)), (float)(y + heightVal / 2.0 - (double)((float)FontManager.arial16.getHeight() / 2.0f)), (float)moduleAnimation.getOutput());
                    alphaAnimation = (float)moduleAnimation.getOutput();
                }
            }
            if (((Boolean)HUD.background.getValue()).booleanValue()) {
                Gui.drawRect3((float)(x - 2.0), (float)(y - 3.0), FontManager.arial16.getStringWidth(displayText) + 5, (float)heightVal, ColorUtil.applyOpacity(new Color(20, 20, 20), ((Double)HUD.backgroundAlpha.getValue()).floatValue() * alphaAnimation).getRGB());
            }
            if (((Boolean)HUD.hLine.getValue()).booleanValue()) {
                Gui.drawRect3((float)RenderUtil.width() - 1.0f, (float)(y - 3.0), 1.0, (float)heightVal, HUD.color(count).getRGB());
            }
            int textcolor = HUD.color(count).getRGB();
            FontManager.arial16.drawStringWithShadow(displayText, (float)x, (float)(y - 1.0 + (double)FontManager.arial16.getMiddleOfBox((float)heightVal)), ColorUtil.applyOpacity(textcolor, alphaAnimation));
            if (((ANIM)(HUD.animation.getValue())).name() == "ScaleIn") {
                RenderUtil.scaleEnd();
            }
            yOffset += moduleAnimation.getOutput() * heightVal;
            ++count;
        }
    }

    public static enum ANIM {
        MoveIn,
        ScaleIn;

    }
}

