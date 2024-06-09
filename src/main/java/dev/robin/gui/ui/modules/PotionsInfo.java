package dev.robin.gui.ui.modules;

import dev.robin.event.EventTarget;
import dev.robin.event.rendering.EventRender2D;
import dev.robin.event.rendering.EventShader;
import dev.robin.gui.ui.UiModule;
import dev.robin.module.modules.render.HUD;
import dev.robin.utils.render.RenderUtil;
import dev.robin.utils.render.RoundedUtils;
import dev.robin.utils.render.animation.Direction;
import dev.robin.utils.render.animation.impl.ContinualAnimation;
import dev.robin.utils.render.animation.impl.EaseBackIn;
import dev.robin.utils.render.fontRender.FontManager;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class PotionsInfo
extends UiModule {
    private int maxString = 0;
    private final Map<Integer, Integer> potionMaxDurations = new HashMap<Integer, Integer>();
    private final ContinualAnimation widthanimation = new ContinualAnimation();
    private final ContinualAnimation heightanimation = new ContinualAnimation();
    private final EaseBackIn animation = new EaseBackIn(200, 1.0, 1.3f);
    List<PotionEffect> effects = new ArrayList<PotionEffect>();

    public PotionsInfo() {
        super("PotionsInfo", 20.0, 40.0, 150.0, 60.0);
    }

    private String get(PotionEffect potioneffect) {
        Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
        String s1 = I18n.format(potion.getName(), new Object[0]);
        s1 = s1 + " " + this.intToRomanByGreedy(potioneffect.getAmplifier() + 1);
        return s1;
    }

    private String intToRomanByGreedy(int num) {
        int[] values = new int[]{1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] symbols = new String[]{"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < values.length && num >= 0; ++i) {
            while (values[i] <= num) {
                num -= values[i];
                stringBuilder.append(symbols[i]);
            }
        }
        return stringBuilder.toString();
    }

    @EventTarget
    public void onShader(EventShader event) {
        int i2 = 16;
        int offsetX = 21;
        int offsetY = 14;
        int x2 = (int)this.getPosX();
        int y2 = (int)this.getPosY();
        int l2 = 24;
        if (this.effects.isEmpty()) {
            return;
        }
        for (PotionEffect potioneffect : this.effects) {
            RoundedUtils.drawRound(x2, y2 + i2 - offsetY, (int)this.widthanimation.getOutput(), 24.0f, 4.0f, new Color(19, 19, 19, 150));
            RoundedUtils.drawRound(x2, (float)(y2 + i2 - offsetY) + 2.0f, 1.0f, 20.0f, 2.0f, HUD.mainColor.getColorC());
            i2 = (int)((double)i2 + (double)l2 * 1.2);
        }
    }

    @EventTarget
    public void onRender2D(EventRender2D event) {
        this.effects = PotionsInfo.mc.thePlayer.getActivePotionEffects().stream().sorted(Comparator.comparingInt(it -> FontManager.arial18.getStringWidth(this.get((PotionEffect)it)))).collect(Collectors.toList());
        int x2 = (int)this.getPosX();
        int y2 = (int)this.getPosY();
        int offsetX = 21;
        int offsetY = 14;
        int i2 = 16;
        ArrayList<Integer> needRemove = new ArrayList<Integer>();
        for (Map.Entry<Integer, Integer> entry : this.potionMaxDurations.entrySet()) {
            if (PotionsInfo.mc.thePlayer.getActivePotionEffect(Potion.potionTypes[entry.getKey()]) != null) continue;
            needRemove.add(entry.getKey());
        }
        Iterator<Integer> iterator = needRemove.iterator();
        while (iterator.hasNext()) {
            int id = (Integer)((Object)iterator.next());
            this.potionMaxDurations.remove(id);
        }
        for (PotionEffect effect : this.effects) {
            if (this.potionMaxDurations.containsKey(effect.getPotionID()) && this.potionMaxDurations.get(effect.getPotionID()) >= effect.getDuration()) continue;
            this.potionMaxDurations.put(effect.getPotionID(), effect.getDuration());
        }
        float width = !this.effects.isEmpty() ? (float)Math.max(50 + FontManager.arial18.getStringWidth(this.get(this.effects.get(this.effects.size() - 1))), 60 + FontManager.arial18.getStringWidth(this.get(this.effects.get(this.effects.size() - 1)))) : 0.0f;
        float height = this.effects.size() * 25;
        this.widthanimation.animate(width, 20);
        this.heightanimation.animate(height, 20);
        if (PotionsInfo.mc.currentScreen instanceof GuiChat && this.effects.isEmpty()) {
            this.animation.setDirection(Direction.FORWARDS);
        } else if (!(PotionsInfo.mc.currentScreen instanceof GuiChat)) {
            this.animation.setDirection(Direction.BACKWARDS);
        }
        RenderUtil.scaleStart(x2 + 50, y2 + 15, (float)this.animation.getOutput());
        HUD hud = this.getModule(HUD.class);
        FontManager.arial18.drawStringWithShadow("Potion Example", (float)x2 + 52.0f - (float)(FontManager.arial18.getStringWidth("Potion Example") / 2), y2 + 18 - FontManager.arial18.getHeight() / 2, new Color(255, 255, 255, 60).getRGB());
        RenderUtil.scaleEnd();
        if (this.effects.isEmpty()) {
            this.maxString = 0;
        }
        if (!this.effects.isEmpty()) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.disableLighting();
            int l2 = 24;
            for (PotionEffect potioneffect : this.effects) {
                RoundedUtils.drawRound(x2, y2 + i2 - offsetY, (int)this.widthanimation.getOutput(), 24.0f, 4.0f, new Color(19, 19, 19, 150));
                RoundedUtils.drawRound(x2, (float)(y2 + i2 - offsetY) + 2.0f, 1.0f, 20.0f, 2.0f, HUD.color(1));
                Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                if (potion.hasStatusIcon()) {
                    mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
                    int i1 = potion.getStatusIconIndex();
                    GlStateManager.enableBlend();
                    PotionsInfo.mc.ingameGUI.drawTexturedModalRect(x2 + offsetX - 17, y2 + i2 - offsetY + 2, 0 + i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
                }
                String s2 = Potion.getDurationString(potioneffect);
                String s1 = this.get(potioneffect);
                FontManager.arial18.drawStringWithShadow(s1, x2 + offsetX + 3, y2 + i2 - offsetY + 2, -1);
                FontManager.arial18.drawStringWithShadow(s2, x2 + offsetX + 3, y2 + i2 + 11 - offsetY + 2, -1);
                i2 = (int)((double)i2 + (double)l2 * 1.2);
                if (this.maxString >= PotionsInfo.mc.fontRendererObj.getStringWidth(s1)) continue;
                this.maxString = PotionsInfo.mc.fontRendererObj.getStringWidth(s1);
            }
        }
    }
}

