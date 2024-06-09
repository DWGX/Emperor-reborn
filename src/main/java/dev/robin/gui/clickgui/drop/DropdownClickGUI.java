package dev.robin.gui.clickgui.drop;

import dev.robin.Client;
import dev.robin.module.Category;
import dev.robin.module.Module;
import dev.robin.module.values.BoolValue;
import dev.robin.module.values.ModeValue;
import dev.robin.module.values.NumberValue;
import dev.robin.module.values.Value;
import dev.robin.utils.client.MathUtil;
import dev.robin.utils.render.BlurUtil;
import dev.robin.utils.render.RenderUtil;
import dev.robin.utils.render.fontRender.FontManager;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

public class DropdownClickGUI
extends GuiScreen {
    List<Float> posX = new ArrayList<Float>();
    List<Float> posY = new ArrayList<Float>();
    List<Module> inSetting = new ArrayList<Module>();
    List<Value<?>> inMode = new ArrayList();

    @Override
    public void initGui() {
        this.posX = Client.instance.cGUIPosX;
        this.posY = Client.instance.cGUIPosY;
        this.inSetting = Client.instance.cGUIInSetting;
        this.inMode = Client.instance.cGUIInMode;
    }

    @Override
    public void onGuiClosed() {
        Client.instance.configManager.saveAllConfig();
        Client.instance.cGUIPosX = this.posX;
        Client.instance.cGUIPosY = this.posY;
        Client.instance.cGUIInSetting = this.inSetting;
        Client.instance.cGUIInMode = this.inMode;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        BlurUtil.blurArea(0.0f, 0.0f, this.width, this.height, 10.0f);
        RenderUtil.drawRect(0.0, 0.0, this.width, this.height, new Color(0, 0, 0, 100).getRGB());
        int cOrder = -1;
        for (Category c : Category.values()) {
            boolean isOnCategory;
            ++cOrder;
            String category = c.toString();
            try {
                this.posY.get(cOrder);
            }
            catch (Exception e) {
                this.posY.add(Float.valueOf(5.0f));
            }
            try {
                this.posX.get(cOrder);
            }
            catch (Exception e) {
                this.posX.add(Float.valueOf((float)(cOrder * 120) + 80.0f));
            }
            float x2 = this.posX.get(cOrder).floatValue();
            float y2 = this.posY.get(cOrder).floatValue();
            RenderUtil.drawRect(x2, y2, x2 + 110.0f, y2 + 15.0f, new Color(255, 255, 255).getRGB());
            FontManager.arial18.drawCenteredString(category, x2 + 55.0f, y2 + 5.0f, new Color(30, 30, 30).getRGB());
            int mOrder = -1;
            float settingY = 0.0f;
            for (Module m : Client.instance.moduleManager.getModsByCategory(c)) {
                boolean isOnModule;
                boolean bl = isOnModule = (float)mouseX > x2 && (float)mouseY > y2 + 15.0f + settingY + (float)(++mOrder * 20) && (float)mouseX < x2 + 110.0f && (float)mouseY < y2 + (float)(mOrder * 20) + 35.0f + settingY;
                int backgroundColor = isOnModule ? (m.getState() ? new Color(140, 190, 240).getRGB() : new Color(240, 240, 240).getRGB()) : (m.getState() ? new Color(150, 200, 250).getRGB() : new Color(250, 250, 250).getRGB());
                RenderUtil.drawRect(x2, y2 + 15.0f + (float)(mOrder * 20) + settingY, x2 + 110.0f, y2 + (float)(mOrder * 20) + 35.0f + settingY, backgroundColor);
                FontManager.Tahoma18.drawCenteredString(m.name, x2 + 55.0f, y2 + 22.0f + (float)(mOrder * 20) + settingY, new Color(30, 30, 30).getRGB());
                if (!this.inSetting.contains(m)) continue;
                int currentSettingY = 0;
                for (Value<?> v : m.getValues()) {
                    if (!v.isAvailable()) continue;
                    RenderUtil.drawRect(x2, y2 + (float)(mOrder * 20) + 35.0f + settingY + (float)currentSettingY, x2 + 110.0f, y2 + (float)(mOrder * 20) + 35.0f + settingY + (float)currentSettingY + 15.0f, m.getState() ? new Color(140, 190, 240).getRGB() : new Color(240, 240, 240).getRGB());
                    FontManager.Tahoma18.drawString(v.getName(), x2 + 1.0f, y2 + (float)(mOrder * 20) + 35.0f + settingY + (float)currentSettingY + 5.0f, new Color(50, 50, 50).getRGB());
                    if (v instanceof ModeValue) {
                        RenderUtil.drawRect(x2 + 50.0f, y2 + (float)(mOrder * 20) + 37.0f + settingY + (float)currentSettingY, x2 + 108.0f, y2 + (float)(mOrder * 20) + 48.0f + settingY + (float)currentSettingY, m.getState() ? new Color(155, 205, 255).getRGB() : new Color(255, 255, 255).getRGB());
                        if (this.inMode.contains(v)) {
                            RenderUtil.drawTriangle(x2 + 100.0f, y2 + (float)(mOrder * 20) + 39.0f + settingY + (float)currentSettingY, x2 + 100.0f, y2 + (float)(mOrder * 20) + 46.0f + settingY + (float)currentSettingY, x2 + 106.0f, (double)(y2 + (float)(mOrder * 20)) + 42.5 + (double)settingY + (double)currentSettingY, new Color(70, 70, 70).getRGB());
                        } else {
                            RenderUtil.drawTriangle(x2 + 100.0f, y2 + (float)(mOrder * 20) + 40.0f + settingY + (float)currentSettingY, x2 + 103.0f, y2 + (float)(mOrder * 20) + 46.0f + settingY + (float)currentSettingY, x2 + 106.0f, y2 + (float)(mOrder * 20) + 40.0f + settingY + (float)currentSettingY, new Color(70, 70, 70).getRGB());
                        }
                        FontManager.Tahoma16.drawCenteredString(v.getValue().toString(), x2 + 75.0f, y2 + (float)(mOrder * 20) + 40.0f + settingY + (float)currentSettingY, new Color(30, 30, 30).getRGB());
                        if (this.inMode.contains(v)) {
                            RenderUtil.drawShadow(x2 + 115.0f, y2 + (float)(mOrder * 20) + 37.0f + settingY + (float)currentSettingY, x2 + 160.0f, y2 + (float)(mOrder * 20) + 37.0f + settingY + (float)currentSettingY + (float)(((ModeValue)v).getModes().length * 10));
                            int modeOrder = 0;
                            for (Enum e : ((ModeValue)v).getModes()) {
                                RenderUtil.drawRect(x2 + 115.0f, y2 + (float)(mOrder * 20) + 37.0f + settingY + (float)currentSettingY + (float)(modeOrder * 10), x2 + 160.0f, y2 + (float)(mOrder * 20) + 47.0f + settingY + (float)currentSettingY + (float)(modeOrder * 10), m.getState() ? new Color(140, 190, 240).getRGB() : new Color(240, 240, 240).getRGB());
                                if (v.getValue() == e) {
                                    FontManager.Tahoma14.drawString(e.toString(), x2 + 116.0f, y2 + (float)(mOrder * 20) + 40.5f + settingY + (float)currentSettingY + (float)(modeOrder * 10), new Color(30, 30, 30).getRGB());
                                } else {
                                    FontManager.Tahoma14.drawString(e.toString(), x2 + 116.0f, y2 + (float)(mOrder * 20) + 40.5f + settingY + (float)currentSettingY + (float)(modeOrder * 10), new Color(150, 150, 150).getRGB());
                                }
                                ++modeOrder;
                            }
                        }
                    }
                    if (v instanceof NumberValue) {
                        NumberValue n = (NumberValue)v;
                        double diffMaxMin = ((NumberValue)v).getMax() - ((NumberValue)v).getMin();
                        double diffValMin = (Double)n.getValue() - ((NumberValue)v).getMin();
                        RenderUtil.drawRect(x2 + 50.0f, y2 + (float)(mOrder * 20) + 37.0f + settingY + (float)currentSettingY, x2 + 108.0f, y2 + (float)(mOrder * 20) + 48.0f + settingY + (float)currentSettingY, m.getState() ? new Color(155, 205, 255).getRGB() : new Color(255, 255, 255).getRGB());
                        RenderUtil.drawRect(x2 + 50.0f, y2 + (float)(mOrder * 20) + 37.0f + settingY + (float)currentSettingY, (double)(x2 + 50.0f) + 58.0 * diffValMin / diffMaxMin, y2 + (float)(mOrder * 20) + 48.0f + settingY + (float)currentSettingY, !m.getState() ? new Color(155, 205, 255).getRGB() : new Color(255, 255, 255).getRGB());
                        FontManager.Tahoma16.drawString(v.getValue().toString(), x2 + 107.0f - (float)FontManager.Tahoma16.getStringWidth(v.getValue().toString()), y2 + (float)(mOrder * 20) + 41.0f + settingY + (float)currentSettingY, new Color(30, 30, 30).getRGB());
                        if (Mouse.isButtonDown((int)0) && (float)mouseX >= x2 + 50.0f && (float)mouseY >= y2 + (float)(mOrder * 20) + 37.0f + settingY + (float)currentSettingY && (float)mouseX <= x2 + 108.0f && (float)mouseY <= y2 + (float)(mOrder * 20) + 48.0f + settingY + (float)currentSettingY) {
                            n.setValue(MathUtil.round((double)(((float)mouseX - x2 - 50.0f) / 58.0f) * (diffMaxMin + 1.0) + n.getMin(), 1));
                            if ((Double)n.getValue() > n.getMax()) {
                                n.setValue(n.getMax());
                            }
                            if ((Double)n.getValue() < n.getMin()) {
                                n.setValue(n.getMin());
                            }
                        }
                    }
                    if (v instanceof BoolValue) {
                        BoolValue o2 = (BoolValue)v;
                        RenderUtil.drawRect(x2 + 97.0f, y2 + (float)(mOrder * 20) + 37.0f + settingY + (float)currentSettingY, x2 + 108.0f, y2 + (float)(mOrder * 20) + 48.0f + settingY + (float)currentSettingY, !m.getState() ? (!((Boolean)o2.getValue()).booleanValue() ? new Color(210, 210, 210).getRGB() : new Color(255, 255, 255).getRGB()) : ((Boolean)o2.getValue() == false ? new Color(110, 160, 210).getRGB() : new Color(160, 210, 255).getRGB()));
                    }
                    currentSettingY += 15;
                }
                RenderUtil.drawTexturedRect(x2, y2 + (float)(mOrder * 20) + 35.0f + settingY, 110.0f, 9.0f, new ResourceLocation("shaders/panelbottom.png"), Color.white.getRGB());
                settingY += (float)currentSettingY;
            }
            RenderUtil.drawShadow(x2, y2, x2 + 110.0f, y2 + (float)(mOrder * 20) + 35.0f + settingY);
            RenderUtil.drawTexturedRect(x2, y2 + 15.0f, 110.0f, 9.0f, new ResourceLocation("shaders/panelbottom.png"), Color.white.getRGB());
            boolean bl = isOnCategory = (float)mouseX > x2 && (float)mouseY > y2 && (float)mouseX < x2 + 110.0f && (float)mouseY < y2 + 15.0f;
            if (!Mouse.isButtonDown((int)0) || !isOnCategory) continue;
            this.posX.set(cOrder, Float.valueOf(x2 + (float)Mouse.getDX() / 2.0f));
            this.posY.set(cOrder, Float.valueOf(y2 - (float)Mouse.getDY() / 2.0f));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int cOrder = -1;
        for (Category c : Category.values()) {
            boolean isOnCategory;
            float x2 = this.posX.get(++cOrder).floatValue();
            float y2 = this.posY.get(cOrder).floatValue();
            int mOrder = -1;
            float settingY = 0.0f;
            for (Module m : Client.instance.moduleManager.getModsByCategory(c)) {
                boolean isOnModule;
                boolean bl = isOnModule = (float)mouseX > x2 && (float)mouseY > y2 + 15.0f + settingY + (float)(++mOrder * 20) && (float)mouseX < x2 + 110.0f && (float)mouseY < y2 + (float)(mOrder * 20) + 35.0f + settingY;
                if (isOnModule && mouseButton == 0) {
                    m.setState(!m.getState());
                }
                if (isOnModule && mouseButton == 1 && !m.getValues().isEmpty()) {
                    if (this.inSetting.contains(m)) {
                        this.inSetting.remove(m);
                    } else {
                        this.inSetting.add(m);
                    }
                }
                if (!this.inSetting.contains(m)) continue;
                int currentSettingY = 0;
                for (Value<?> v : m.getValues()) {
                    if (!v.isAvailable()) continue;
                    if (v instanceof ModeValue) {
                        if ((float)mouseX > x2 + 50.0f && (float)mouseY > y2 + (float)(mOrder * 20) + 37.0f + settingY + (float)currentSettingY && (float)mouseX < x2 + 108.0f && (float)mouseY < y2 + (float)(mOrder * 20) + 48.0f + settingY + (float)currentSettingY) {
                            if (this.inMode.contains(v)) {
                                this.inMode.remove(v);
                            } else {
                                this.inMode.add(v);
                            }
                        }
                        if (this.inMode.contains(v)) {
                            int modeOrder = 0;
                            for (Enum e : ((ModeValue)v).getModes()) {
                                if ((float)mouseX > x2 + 115.0f && (float)mouseY > y2 + (float)(mOrder * 20) + 37.0f + settingY + (float)currentSettingY + (float)(modeOrder * 10) && (float)mouseX < x2 + 160.0f && (float)mouseY < y2 + (float)(mOrder * 20) + 47.0f + settingY + (float)currentSettingY + (float)(modeOrder * 10)) {
                                    ((ModeValue)v).setMode(e.name());
                                }
                                ++modeOrder;
                            }
                        }
                    }
                    if (v instanceof NumberValue) {
                        // empty if block
                    }
                    if (v instanceof BoolValue) {
                        BoolValue o2 = (BoolValue)v;
                        if ((float)mouseX > x2 + 97.0f && (float)mouseY > y2 + (float)(mOrder * 20) + 37.0f + settingY + (float)currentSettingY && (float)mouseX < x2 + 108.0f && (float)mouseY < y2 + (float)(mOrder * 20) + 48.0f + settingY + (float)currentSettingY) {
                            o2.setValue((Boolean)o2.getValue() == false);
                        }
                    }
                    currentSettingY += 15;
                }
                settingY += (float)currentSettingY;
            }
            boolean bl = isOnCategory = (float)mouseX > x2 && (float)mouseY > y2 && (float)mouseX < x2 + 110.0f && (float)mouseY < y2 + 15.0f;
            if (!Mouse.isButtonDown((int)0) || !isOnCategory) continue;
            this.posX.set(cOrder, Float.valueOf(x2 + (float)Mouse.getDX() / 2.0f));
            this.posY.set(cOrder, Float.valueOf(y2 - (float)Mouse.getDY() / 2.0f));
        }
    }
}

