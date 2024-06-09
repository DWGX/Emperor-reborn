package dev.emperor.module.modules.render;

import dev.emperor.Client;
import dev.emperor.event.EventTarget;
import dev.emperor.event.misc.EventKey;
import dev.emperor.event.rendering.EventRender2D;
import dev.emperor.event.rendering.EventShader;
import dev.emperor.gui.notification.Notification;
import dev.emperor.gui.notification.NotificationManager;
import dev.emperor.gui.ui.UiModule;
import dev.emperor.gui.ui.modules.Debug;
import dev.emperor.gui.ui.modules.Information;
import dev.emperor.gui.ui.modules.ModuleList;
import dev.emperor.gui.ui.modules.PotionsInfo;
import dev.emperor.gui.ui.modules.TargetHud;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.ModuleManager;
import dev.emperor.module.values.BoolValue;
import dev.emperor.module.values.ColorValue;
import dev.emperor.module.values.ModeValue;
import dev.emperor.module.values.NumberValue;
import dev.emperor.utils.render.AnimationUtil;
import dev.emperor.utils.render.RenderUtil;
import dev.emperor.utils.render.RoundedUtils;
import dev.emperor.utils.render.animation.Animation;
import dev.emperor.utils.render.animation.Direction;
import dev.emperor.utils.render.fontRender.FontManager;
import dev.emperor.utils.render.fontRender.RapeMasterFontManager;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public class HUD
extends Module {
    public final ModeValue<HUDmode> hudModeValue = new ModeValue("HUD Mode", (Enum[])HUDmode.values(), (Enum)HUDmode.Neon);
    public static BoolValue arraylist = new BoolValue("Arraylist", true);
    public static final BoolValue importantModules = new BoolValue("Arraylist-Important", false, () -> (Boolean)arraylist.getValue());
    public static final BoolValue hLine = new BoolValue("Arraylist-HLine", true, () -> (Boolean)arraylist.getValue());
    public static final NumberValue height = new NumberValue("Arraylist-Height", 11.0, 9.0, 20.0, 1.0, () -> (Boolean)arraylist.getValue());
    public static final ModeValue<ModuleList.ANIM> animation = new ModeValue("Arraylist-Animation", (Enum[])ModuleList.ANIM.values(), (Enum)ModuleList.ANIM.ScaleIn, () -> (Boolean)arraylist.getValue());
    public static final BoolValue background = new BoolValue("Arraylist-Background", true, () -> (Boolean)arraylist.getValue());
    public static final NumberValue backgroundAlpha = new NumberValue("Arraylist-Background Alpha", 0.35, 0.01, 1.0, 0.01, () -> (Boolean)arraylist.getValue());
    public static BoolValue tab = new BoolValue("TabGUI", false);
    public static BoolValue notifications = new BoolValue("Notification", false);
    public static BoolValue Debug = new BoolValue("Debug", false);
    public static BoolValue potionInfo = new BoolValue("Potion", false);
    public static BoolValue targetHud = new BoolValue("TargetHUD", true);
    public static ModeValue<THUDMode> thudmodeValue = new ModeValue("THUD Style", (Enum[])THUDMode.values(), (Enum)THUDMode.Neon);
    public static BoolValue multi_targetHUD = new BoolValue("Multi TargetHUD", true);
    public static BoolValue titleBar = new BoolValue("TitleBar", true);
    private final ModeValue<TitleMode> modeValue = new ModeValue("Title Mode", (Enum[])TitleMode.values(), (Enum)TitleMode.Simple);
    public static BoolValue Information = new BoolValue("Information", false);
    public static NumberValue animationSpeed = new NumberValue("Animation Speed", 4.0, 1.0, 10.0, 0.1);
    public static NumberValue scoreBoardHeightValue = new NumberValue("Scoreboard Height", 0.0, 0.0, 300.0, 1.0);
    public static ColorValue mainColor = new ColorValue("First Color", Color.white.getRGB());
    public static ColorValue mainColor2 = new ColorValue("Second Color", Color.white.getRGB());
    private final TabGUI tabGUI = new TabGUI();
    private float leftY;
    public int offsetValue = 0;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public HUD() {
        super("HUD", Category.Render);
        this.setState(true);
    }

    public static Color color(int tick) {
        Color textColor = new Color(RenderUtil.colorSwitch(mainColor.getColorC(), mainColor2.getColorC(), 2000.0f, -(tick *= 200) / 40, 75L, 2.0));
        return textColor;
    }

    public static RapeMasterFontManager getFont() {
        return FontManager.arial18;
    }

    @EventTarget
    public void onShader(EventShader e) {
        String fps = String.valueOf(Minecraft.getDebugFPS());
        if (((Boolean)titleBar.getValue()).booleanValue()) {
            switch (((TitleMode)((Object)this.modeValue.getValue())).toString().toLowerCase()) {
                case "simple": {
                    switch ((HUDmode)((Object)this.hudModeValue.getValue())) {
                        case Shit: {
                            String str = (Object)((Object)EnumChatFormatting.DARK_GRAY) + " | " + (Object)((Object)EnumChatFormatting.WHITE) + Client.instance.USER + (Object)((Object)EnumChatFormatting.DARK_GRAY) + " | " + (Object)((Object)EnumChatFormatting.WHITE) + Minecraft.getDebugFPS() + "fps" + (Object)((Object)EnumChatFormatting.DARK_GRAY) + " | " + (Object)((Object)EnumChatFormatting.WHITE) + (mc.isSingleplayer() ? "SinglePlayer" : HUD.mc.getCurrentServerData().serverIP);
                            RoundedUtils.drawRound(6.0f, 6.0f, FontManager.arial16.getStringWidth(str) + 8 + FontManager.bold22.getStringWidth(Client.NAME.toUpperCase()), 15.0f, 0.0f, new Color(0, 0, 0));
                            RoundedUtils.drawRound(6.0f, 6.0f, FontManager.arial16.getStringWidth(str) + 8 + FontManager.bold22.getStringWidth(Client.NAME.toUpperCase()), 1.0f, 1.0f, new Color(0, 0, 0));
                            break;
                        }
                        case Neon: {
                            String title = String.format("| v%s | %s | %sfps", Client.instance.getVersion(), Client.instance.USER, Minecraft.getDebugFPS());
                            String mark = Client.NAME;
                            float width = HUD.getFont().getStringWidth(title.toUpperCase()) + FontManager.bold22.getStringWidth(mark.toUpperCase()) + 6;
                            Gui.drawRect3(4.0, 4.0, width + 6.0f, FontManager.bold22.getHeight() + 4, new Color(0, 0, 0, 255).getRGB());
                        }
                    }
                    break;
                }
                case "logo": {
                    GlStateManager.resetColor();
                    GlStateManager.enableAlpha();
                    GlStateManager.disableBlend();
                    RenderUtil.drawImage(0.0f, 0.0f, 64, 64, new ResourceLocation("express/ico.png"), mainColor.getColorC());
                }
            }
        }
        this.drawNotificationsEffects(e.isBloom());
        if (((Boolean)tab.getValue()).booleanValue()) {
            this.tabGUI.blur(this, this.leftY);
        }
    }

    @EventTarget
    public void onKey(EventKey e) {
        if (((Boolean)tab.getValue()).booleanValue()) {
            this.tabGUI.onKey(e.getKey());
        }
    }

    @EventTarget
    public void onRender(EventRender2D e) {
        String name = Client.instance.USER;
        String fps = String.valueOf(Minecraft.getDebugFPS());
        int lengthName = FontManager.Tahoma16.getStringWidth(name);
        int nlRectX = lengthName + 74;
        UiModule DebugHud = Client.instance.uiManager.getModule(Debug.class);
        DebugHud.setState((Boolean)Debug.getValue());
        UiModule Arraylist = Client.instance.uiManager.getModule(ModuleList.class);
        Arraylist.setState((Boolean)arraylist.getValue());
        UiModule InformationHUD = Client.instance.uiManager.getModule(Information.class);
        InformationHUD.setState((Boolean)Information.getValue());
        UiModule potionHUD = Client.instance.uiManager.getModule(PotionsInfo.class);
        potionHUD.setState((Boolean)potionInfo.getValue());
        this.drawNotifications();
        if (((Boolean)titleBar.getValue()).booleanValue()) {
            switch (((TitleMode)((Object)this.modeValue.getValue())).toString().toLowerCase()) {
                case "simple": {
                    switch ((HUDmode)((Object)this.hudModeValue.getValue())) {
                        case Shit: {
                            String str = (Object)((Object)EnumChatFormatting.DARK_GRAY) + " | " + (Object)((Object)EnumChatFormatting.WHITE) + Client.instance.USER + (Object)((Object)EnumChatFormatting.DARK_GRAY) + " | " + (Object)((Object)EnumChatFormatting.WHITE) + Minecraft.getDebugFPS() + "fps" + (Object)((Object)EnumChatFormatting.DARK_GRAY) + " | " + (Object)((Object)EnumChatFormatting.WHITE) + (mc.isSingleplayer() ? "SinglePlayer" : HUD.mc.getCurrentServerData().serverIP);
                            RoundedUtils.drawRound(6.0f, 6.0f, FontManager.arial16.getStringWidth(str) + 8 + FontManager.bold22.getStringWidth(Client.NAME.toUpperCase()), 15.0f, 0.0f, new Color(19, 19, 19, 230));
                            RoundedUtils.drawRound(6.0f, 6.0f, FontManager.arial16.getStringWidth(str) + 8 + FontManager.bold22.getStringWidth(Client.NAME.toUpperCase()), 1.0f, 1.0f, HUD.color(8));
                            FontManager.arial16.drawString(str, 11 + FontManager.bold22.getStringWidth(Client.NAME.toUpperCase()), 11.5f, Color.WHITE.getRGB());
                            FontManager.bold22.drawString(Client.NAME.toUpperCase(), 9.5f, 12.0f, HUD.color(8).getRGB());
                            FontManager.bold22.drawString(Client.NAME.toUpperCase(), 10.0f, 12.5f, Color.WHITE.getRGB());
                            break;
                        }
                        case Neon: {
                            String title = String.format("| v%s | %s | %sfps", Client.instance.getVersion(), Client.instance.USER, Minecraft.getDebugFPS());
                            String mark = Client.NAME;
                            float width = HUD.getFont().getStringWidth(title.toUpperCase()) + FontManager.bold22.getStringWidth(mark.toUpperCase()) + 6;
                            RenderUtil.drawRect(4.0, 4.0, width + 10.0f, FontManager.bold22.getHeight() + 8, new Color(0, 0, 0, 100).getRGB());
                            FontManager.bold22.drawStringDynamic(mark.toUpperCase(), 8.0, 10.0, 1, 6);
                            HUD.getFont().drawString(title.toUpperCase(), 12 + FontManager.bold22.getStringWidth(mark.toUpperCase()), 9.0f, -1);
                        }
                    }
                    this.leftY = AnimationUtil.animate(this.leftY, 24.0f, 0.3f);
                    break;
                }
                case "neverlose": {
                    Gui.drawRect(5.0, 5.0, nlRectX, 20.0, new Color(0, 0, 0, 190).getRGB());
                    FontManager.Tahoma18.drawString(Client.NAME, 11.2f, 10.2f, new Color(71, 166, 253).getRGB());
                    FontManager.Tahoma18.drawString(Client.NAME, 10.0f, 9.5f, new Color(255, 255, 255).getRGB());
                    FontManager.Tahoma18.drawString("|", 43.0f, 9.5f, new Color(255, 255, 255).getRGB());
                    FontManager.Tahoma16.drawString(fps, 48.5f, 10.3f, new Color(255, 255, 255).getRGB());
                    FontManager.Tahoma18.drawString("|", 64.0f, 9.5f, new Color(255, 255, 255).getRGB());
                    FontManager.Tahoma16.drawString(name, 70.0f, 10.3f, new Color(255, 255, 255).getRGB());
                    RenderUtil.drawShadow(5.0f, 5.0f, nlRectX, 20.0f);
                    break;
                }
                case "logo": {
                    GlStateManager.resetColor();
                    GlStateManager.enableAlpha();
                    GlStateManager.disableBlend();
                    RenderUtil.drawImage(0.0f, 0.0f, 64, 64, new ResourceLocation("express/ico.png"), HUD.color(1));
                    break;
                }
                case "onetap": {
                    String text = Client.NAME + " | " + fps + " | " + name + " | Yaw: " + (int)HUD.mc.thePlayer.rotationYaw % 360 + " | Pitch: " + (int)HUD.mc.thePlayer.rotationPitch;
                    int otRectX = FontManager.Tahoma14.getStringWidth(text) + 11;
                    RenderUtil.drawRoundedRect(5.0f, 6.0f, otRectX, 19.0f, 4, new Color(0, 0, 0, 200).getRGB());
                    RenderUtil.drawRoundedRect(5.0f, 5.0f, otRectX, 7.0f, 4, mainColor.getColor());
                    FontManager.Tahoma14.drawStringWithShadow(text, 8.0f, 11.0f, new Color(255, 255, 255).getRGB());
                }
            }
        }
        UiModule Thud = Client.instance.uiManager.getModule(TargetHud.class);
        Thud.setState((Boolean)targetHud.getValue());
        if (((Boolean)tab.getValue()).booleanValue()) {
            this.tabGUI.renderTabGUI(this, this.leftY);
        }
    }

    public void drawNotifications() {
        ScaledResolution sr = new ScaledResolution(mc);
        float yOffset = 0.0f;
        NotificationManager.setToggleTime(2.0f);
        for (Notification notification : NotificationManager.getNotifications()) {
            Animation animation = notification.getAnimation();
            animation.setDirection(notification.getTimerUtil().hasTimeElapsed((long)notification.getTime()) ? Direction.BACKWARDS : Direction.FORWARDS);
            if (animation.finished(Direction.BACKWARDS)) {
                NotificationManager.getNotifications().remove(notification);
                continue;
            }
            animation.setDuration(200);
            int actualOffset = 3;
            int notificationHeight = 23;
            int notificationWidth = FontManager.arial20.getStringWidth(notification.getDescription()) + 25;
            float x2 = (float)((double)sr.getScaledWidth() - (double)(notificationWidth + 5) * animation.getOutput());
            float y2 = (float)sr.getScaledHeight() - (yOffset + 18.0f + (float)this.offsetValue + (float)notificationHeight + 15.0f);
            notification.drawLettuce(x2, y2, notificationWidth, notificationHeight);
            yOffset = (float)((double)yOffset + (double)(notificationHeight + actualOffset) * animation.getOutput());
        }
    }

    public void drawNotificationsEffects(boolean bloom) {
        ScaledResolution sr = new ScaledResolution(mc);
        float yOffset = 0.0f;
        for (Notification notification : NotificationManager.getNotifications()) {
            Animation animation = notification.getAnimation();
            animation.setDirection(notification.getTimerUtil().hasTimeElapsed((long)notification.getTime()) ? Direction.BACKWARDS : Direction.FORWARDS);
            if (animation.finished(Direction.BACKWARDS)) {
                NotificationManager.getNotifications().remove(notification);
                continue;
            }
            animation.setDuration(200);
            int actualOffset = 3;
            int notificationHeight = 23;
            int notificationWidth = FontManager.arial20.getStringWidth(notification.getDescription()) + 25;
            float x2 = (float)((double)sr.getScaledWidth() - (double)(notificationWidth + 5) * animation.getOutput());
            float y2 = (float)sr.getScaledHeight() - (yOffset + 18.0f + (float)this.offsetValue + (float)notificationHeight + 15.0f);
            notification.blurLettuce(x2, y2, notificationWidth, notificationHeight, bloom);
            yOffset = (float)((double)yOffset + (double)(notificationHeight + actualOffset) * animation.getOutput());
        }
    }

    public static enum THUDMode {
        Neon,
        Novoline,
        Exhibition,
        ThunderHack,
        Raven,
        Sils,
        WTFNovo,
        Exire,
        Moon,
        RiseNew;

    }

    public static enum HUDmode {
        Neon,
        Shit;

    }

    public static enum TitleMode {
        Simple,
        NeverLose,
        OneTap,
        Logo;

    }

    public static enum Section {
        TYPES,
        MODULES;

    }

    public class TabGUI {
        private Section section = Section.TYPES;
        private Category selectedType = Category.values()[0];
        private Module selectedModule = null;
        private int maxType;
        private int maxModule;
        private float horizonAnimation = 0.0f;
        private int currentType = 0;
        private int currentModule = 0;

        public void init() {
            Category[] arrCategory = Category.values();
            FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
            for (Category category : arrCategory) {
                int categoryWidth = fontRenderer.getStringWidth(category.name().toUpperCase()) + 4;
                this.maxType = Math.max(this.maxType, categoryWidth);
            }
            ModuleManager moduleManager = Client.instance.moduleManager;
            for (Module module : Client.instance.moduleManager.getModules()) {
                int moduleWidth = fontRenderer.getStringWidth(module.getName().toUpperCase()) + 4;
                this.maxModule = Math.max(this.maxModule, moduleWidth);
            }
            this.maxModule += 12;
            this.maxType = Math.max(this.maxType, this.maxModule);
            this.maxModule += this.maxType;
        }

        public void blur(HUD hud, float y2) {
            float categoryY;
            float moduleY = categoryY = y2;
            int moduleWidth = 60;
            int moduleX = moduleWidth + 1;
            Gui.drawRect(4.0, categoryY, moduleWidth - 3, categoryY + (float)(12 * Category.values().length), new Color(0, 0, 0, 255).getRGB());
            for (Category category : Category.values()) {
                boolean isSelected;
                boolean bl = isSelected = this.selectedType == category;
                if (isSelected) {
                    Gui.drawRect(5.0, categoryY + 2.0f, 6.5, (float)((double)(categoryY + (float)HUD.getFont().getHeight()) + 1.5), HUD.color(0).getRGB());
                    moduleY = categoryY;
                }
                categoryY += 12.0f;
            }
            if (this.section == Section.MODULES || this.horizonAnimation > 1.0f) {
                int moduleHeight = 12 * Client.instance.moduleManager.getModsByCategory(this.selectedType).size();
                if (this.horizonAnimation < (float)moduleWidth) {
                    this.horizonAnimation = (float)((double)this.horizonAnimation + (double)((float)moduleWidth - this.horizonAnimation) / 20.0);
                }
                Gui.drawRect(moduleX, moduleY, (float)moduleX + this.horizonAnimation, moduleY + (float)moduleHeight, new Color(0, 0, 0, 255).getRGB());
                for (Module module : Client.instance.moduleManager.getModsByCategory(this.selectedType)) {
                    boolean isSelected;
                    boolean bl = isSelected = this.selectedModule == module;
                    if (isSelected) {
                        Gui.drawRect((float)moduleX + 1.0f, moduleY + 2.0f, (float)moduleX + 2.5f, moduleY + (float)HUD.getFont().getHeight() + 1.0f, new Color(0, 0, 0, 255).getRGB());
                    }
                    moduleY += 12.0f;
                }
            }
            if (this.horizonAnimation > 0.0f && this.section != Section.MODULES) {
                this.horizonAnimation -= 5.0f;
            }
        }

        public void renderTabGUI(HUD hud, float y2) {
            float categoryY;
            float moduleY = categoryY = y2;
            int moduleWidth = 60;
            int moduleX = moduleWidth + 1;
            Gui.drawRect(4.0, categoryY, moduleWidth - 3, categoryY + (float)(12 * Category.values().length), new Color(0, 0, 0, 100).getRGB());
            for (Category category : Category.values()) {
                boolean isSelected = this.selectedType == category;
                int color = isSelected ? -1 : new Color(150, 150, 150).getRGB();
                HUD.getFont().drawString(category.name(), 8.0f, categoryY + 2.0f, color);
                if (isSelected) {
                    Gui.drawRect(5.0, categoryY + 2.0f, 6.5, (float)((double)(categoryY + (float)HUD.getFont().getHeight()) + 1.5), HUD.color(0).getRGB());
                    moduleY = categoryY;
                }
                categoryY += 12.0f;
            }
            if (this.section == Section.MODULES || this.horizonAnimation > 1.0f) {
                int moduleHeight = 12 * Client.instance.moduleManager.getModsByCategory(this.selectedType).size();
                if (this.horizonAnimation < (float)moduleWidth) {
                    this.horizonAnimation = (float)((double)this.horizonAnimation + (double)((float)moduleWidth - this.horizonAnimation) / 20.0);
                }
                Gui.drawRect(moduleX, moduleY, (float)moduleX + this.horizonAnimation, moduleY + (float)moduleHeight, new Color(0, 0, 0, 100).getRGB());
                for (Module module : Client.instance.moduleManager.getModsByCategory(this.selectedType)) {
                    boolean isSelected;
                    boolean bl = isSelected = this.selectedModule == module;
                    int color = isSelected ? new Color(-1).getRGB() : (module.getState() ? -1 : 0xAAAAAA);
                    HUD.getFont().drawString(module.getName(), moduleX + 3, moduleY + 2.0f, color);
                    if (isSelected) {
                        Gui.drawRect((float)moduleX + 1.0f, moduleY + 2.0f, (float)moduleX + 2.5f, moduleY + (float)HUD.getFont().getHeight() + 1.0f, HUD.color(0).getRGB());
                    }
                    moduleY += 12.0f;
                }
            }
            if (this.horizonAnimation > 0.0f && this.section != Section.MODULES) {
                this.horizonAnimation -= 5.0f;
            }
        }

        public void onKey(int key) {
            Minecraft mc = Minecraft.getMinecraft();
            ModuleManager moduleManager = Client.instance.moduleManager;
            Category[] values = Category.values();
            if (mc.gameSettings.showDebugInfo) {
                return;
            }
            int KEY_DOWN = Keyboard.KEY_DOWN;
            int KEY_UP = Keyboard.KEY_UP;
            int KEY_RIGHT = Keyboard.KEY_RIGHT;
            int KEY_RETURN = Keyboard.KEY_RETURN;
            int KEY_LEFT = Keyboard.KEY_LEFT;
            switch (key) {
                case 208: {
                    if (this.section == Section.TYPES) {
                        this.currentType = (this.currentType + 1) % values.length;
                        this.selectedType = values[this.currentType];
                        break;
                    }
                    if (this.section != Section.MODULES) break;
                    List<Module> modulesByCategory = moduleManager.getModsByCategory(this.selectedType);
                    this.currentModule = (this.currentModule + 1) % modulesByCategory.size();
                    this.selectedModule = modulesByCategory.get(this.currentModule);
                    break;
                }
                case 200: {
                    if (this.section == Section.TYPES) {
                        this.currentType = (this.currentType + values.length - 1) % values.length;
                        this.selectedType = values[this.currentType];
                        break;
                    }
                    if (this.section != Section.MODULES) break;
                    List<Module> modulesByCategory = moduleManager.getModsByCategory(this.selectedType);
                    this.currentModule = (this.currentModule + modulesByCategory.size() - 1) % modulesByCategory.size();
                    this.selectedModule = modulesByCategory.get(this.currentModule);
                    break;
                }
                case 205: {
                    if (this.section != Section.TYPES) break;
                    this.currentModule = 0;
                    this.selectedModule = moduleManager.getModsByCategory(this.selectedType).get(0);
                    this.section = Section.MODULES;
                    this.horizonAnimation = 0.0f;
                    break;
                }
                case 28: {
                    if (this.section == Section.MODULES) {
                        this.selectedModule.toggle();
                        break;
                    }
                    if (this.section != Section.TYPES) break;
                    this.currentModule = 0;
                    this.section = Section.MODULES;
                    this.selectedModule = moduleManager.getModsByCategory(this.selectedType).get(0);
                    break;
                }
                case 203: {
                    if (this.section != Section.MODULES) break;
                    this.section = Section.TYPES;
                    this.currentModule = 0;
                }
            }
        }
    }
}

