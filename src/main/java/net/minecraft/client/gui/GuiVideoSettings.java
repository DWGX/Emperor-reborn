package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.src.Config;
import net.optifine.Lang;
import net.optifine.gui.GuiAnimationSettingsOF;
import net.optifine.gui.GuiDetailSettingsOF;
import net.optifine.gui.GuiOptionButtonOF;
import net.optifine.gui.GuiOptionSliderOF;
import net.optifine.gui.GuiOtherSettingsOF;
import net.optifine.gui.GuiPerformanceSettingsOF;
import net.optifine.gui.GuiQualitySettingsOF;
import net.optifine.gui.GuiScreenOF;
import net.optifine.gui.TooltipManager;
import net.optifine.gui.TooltipProviderOptions;
import net.optifine.shaders.gui.GuiShaders;

public class GuiVideoSettings
extends GuiScreenOF {
    private GuiScreen parentGuiScreen;
    protected String screenTitle = "Video Settings";
    private GameSettings guiGameSettings;
    private static GameSettings.Options[] videoOptions = new GameSettings.Options[]{GameSettings.Options.GRAPHICS, GameSettings.Options.RENDER_DISTANCE, GameSettings.Options.AMBIENT_OCCLUSION, GameSettings.Options.FRAMERATE_LIMIT, GameSettings.Options.AO_LEVEL, GameSettings.Options.VIEW_BOBBING, GameSettings.Options.GUI_SCALE, GameSettings.Options.USE_VBO, GameSettings.Options.GAMMA, GameSettings.Options.BLOCK_ALTERNATIVES, GameSettings.Options.DYNAMIC_LIGHTS, GameSettings.Options.DYNAMIC_FOV};
    private TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderOptions());

    public GuiVideoSettings(GuiScreen parentScreenIn, GameSettings gameSettingsIn) {
        this.parentGuiScreen = parentScreenIn;
        this.guiGameSettings = gameSettingsIn;
    }

    @Override
    public void initGui() {
        this.screenTitle = I18n.format("options.videoTitle", new Object[0]);
        this.buttonList.clear();
        for (int i = 0; i < videoOptions.length; ++i) {
            GameSettings.Options gamesettings$options = videoOptions[i];
            if (gamesettings$options == null) continue;
            int j2 = this.width / 2 - 155 + i % 2 * 160;
            int k2 = this.height / 6 + 21 * (i / 2) - 12;
            if (gamesettings$options.getEnumFloat()) {
                this.buttonList.add(new GuiOptionSliderOF(gamesettings$options.returnEnumOrdinal(), j2, k2, gamesettings$options));
                continue;
            }
            this.buttonList.add(new GuiOptionButtonOF(gamesettings$options.returnEnumOrdinal(), j2, k2, gamesettings$options, this.guiGameSettings.getKeyBinding(gamesettings$options)));
        }
        int l2 = this.height / 6 + 21 * (videoOptions.length / 2) - 12;
        int i1 = 0;
        i1 = this.width / 2 - 155 + 0;
        this.buttonList.add(new GuiOptionButton(231, i1, l2, Lang.get("of.options.shaders")));
        i1 = this.width / 2 - 155 + 160;
        this.buttonList.add(new GuiOptionButton(202, i1, l2, Lang.get("of.options.quality")));
        i1 = this.width / 2 - 155 + 0;
        this.buttonList.add(new GuiOptionButton(201, i1, l2 += 21, Lang.get("of.options.details")));
        i1 = this.width / 2 - 155 + 160;
        this.buttonList.add(new GuiOptionButton(212, i1, l2, Lang.get("of.options.performance")));
        i1 = this.width / 2 - 155 + 0;
        this.buttonList.add(new GuiOptionButton(211, i1, l2 += 21, Lang.get("of.options.animations")));
        i1 = this.width / 2 - 155 + 160;
        this.buttonList.add(new GuiOptionButton(222, i1, l2, Lang.get("of.options.other")));
        l2 += 21;
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168 + 11, I18n.format("gui.done", new Object[0])));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        this.actionPerformed(button, 1);
    }

    @Override
    protected void actionPerformedRightClick(GuiButton button) {
        if (button.id == GameSettings.Options.GUI_SCALE.ordinal()) {
            this.actionPerformed(button, -1);
        }
    }

    private void actionPerformed(GuiButton p_actionPerformed_1_, int p_actionPerformed_2_) {
        if (p_actionPerformed_1_.enabled) {
            int i = this.guiGameSettings.guiScale;
            if (p_actionPerformed_1_.id < 200 && p_actionPerformed_1_ instanceof GuiOptionButton) {
                this.guiGameSettings.setOptionValue(((GuiOptionButton)p_actionPerformed_1_).returnEnumOptions(), p_actionPerformed_2_);
                p_actionPerformed_1_.displayString = this.guiGameSettings.getKeyBinding(GameSettings.Options.getEnumOptions(p_actionPerformed_1_.id));
            }
            if (p_actionPerformed_1_.id == 200) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.parentGuiScreen);
            }
            if (this.guiGameSettings.guiScale != i) {
                ScaledResolution scaledresolution = new ScaledResolution(this.mc);
                int j2 = scaledresolution.getScaledWidth();
                int k2 = scaledresolution.getScaledHeight();
                this.setWorldAndResolution(this.mc, j2, k2);
            }
            if (p_actionPerformed_1_.id == 201) {
                this.mc.gameSettings.saveOptions();
                GuiDetailSettingsOF guidetailsettingsof = new GuiDetailSettingsOF(this, this.guiGameSettings);
                this.mc.displayGuiScreen(guidetailsettingsof);
            }
            if (p_actionPerformed_1_.id == 202) {
                this.mc.gameSettings.saveOptions();
                GuiQualitySettingsOF guiqualitysettingsof = new GuiQualitySettingsOF(this, this.guiGameSettings);
                this.mc.displayGuiScreen(guiqualitysettingsof);
            }
            if (p_actionPerformed_1_.id == 211) {
                this.mc.gameSettings.saveOptions();
                GuiAnimationSettingsOF guianimationsettingsof = new GuiAnimationSettingsOF(this, this.guiGameSettings);
                this.mc.displayGuiScreen(guianimationsettingsof);
            }
            if (p_actionPerformed_1_.id == 212) {
                this.mc.gameSettings.saveOptions();
                GuiPerformanceSettingsOF guiperformancesettingsof = new GuiPerformanceSettingsOF(this, this.guiGameSettings);
                this.mc.displayGuiScreen(guiperformancesettingsof);
            }
            if (p_actionPerformed_1_.id == 222) {
                this.mc.gameSettings.saveOptions();
                GuiOtherSettingsOF guiothersettingsof = new GuiOtherSettingsOF(this, this.guiGameSettings);
                this.mc.displayGuiScreen(guiothersettingsof);
            }
            if (p_actionPerformed_1_.id == 231) {
                if (Config.isAntialiasing() || Config.isAntialiasingConfigured()) {
                    Config.showGuiMessage(Lang.get("of.message.shaders.aa1"), Lang.get("of.message.shaders.aa2"));
                    return;
                }
                if (Config.isAnisotropicFiltering()) {
                    Config.showGuiMessage(Lang.get("of.message.shaders.af1"), Lang.get("of.message.shaders.af2"));
                    return;
                }
                if (Config.isFastRender()) {
                    Config.showGuiMessage(Lang.get("of.message.shaders.fr1"), Lang.get("of.message.shaders.fr2"));
                    return;
                }
                if (Config.getGameSettings().anaglyph) {
                    Config.showGuiMessage(Lang.get("of.message.shaders.an1"), Lang.get("of.message.shaders.an2"));
                    return;
                }
                this.mc.gameSettings.saveOptions();
                GuiShaders guishaders = new GuiShaders(this, this.guiGameSettings);
                this.mc.displayGuiScreen(guishaders);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        GuiVideoSettings.drawCenteredString(this.fontRendererObj, this.screenTitle, this.width / 2, 15, 0xFFFFFF);
        String s2 = Config.getVersion();
        String s1 = "HD_U";
        if (s1.equals("HD")) {
            s2 = "OptiFine HD M6_pre2";
        }
        if (s1.equals("HD_U")) {
            s2 = "OptiFine HD M6_pre2 Ultra";
        }
        if (s1.equals("L")) {
            s2 = "OptiFine M6_pre2 Light";
        }
        this.drawString(this.fontRendererObj, s2, 2, this.height - 10, 0x808080);
        String s22 = "Minecraft 1.8.9";
        int i = this.fontRendererObj.getStringWidth(s22);
        this.drawString(this.fontRendererObj, s22, this.width - i - 2, this.height - 10, 0x808080);
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.tooltipManager.drawTooltips(mouseX, mouseY, this.buttonList);
    }

    public static int getButtonWidth(GuiButton p_getButtonWidth_0_) {
        return p_getButtonWidth_0_.width;
    }

    public static int getButtonHeight(GuiButton p_getButtonHeight_0_) {
        return p_getButtonHeight_0_.height;
    }

    public static void drawGradientRect(GuiScreen p_drawGradientRect_0_, int p_drawGradientRect_1_, int p_drawGradientRect_2_, int p_drawGradientRect_3_, int p_drawGradientRect_4_, int p_drawGradientRect_5_, int p_drawGradientRect_6_) {
        p_drawGradientRect_0_.drawGradientRect(p_drawGradientRect_1_, p_drawGradientRect_2_, p_drawGradientRect_3_, p_drawGradientRect_4_, p_drawGradientRect_5_, p_drawGradientRect_6_);
    }

    public static String getGuiChatText(GuiChat p_getGuiChatText_0_) {
        return p_getGuiChatText_0_.inputField.getText();
    }
}

