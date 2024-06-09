package dev.emperor.gui.ui.modules;

import dev.emperor.Client;
import dev.emperor.event.EventTarget;
import dev.emperor.event.rendering.EventRender2D;
import dev.emperor.event.rendering.EventShader;
import dev.emperor.event.world.EventTick;
import dev.emperor.gui.ui.UiModule;
import dev.emperor.module.modules.combat.KillAura;
import dev.emperor.module.modules.render.HUD;
import dev.emperor.utils.client.MathUtil;
import dev.emperor.utils.client.TimeUtil;
import dev.emperor.utils.render.AnimationUtil;
import dev.emperor.utils.render.ColorUtil;
import dev.emperor.utils.render.GlowUtils;
import dev.emperor.utils.render.ParticleRender;
import dev.emperor.utils.render.RenderUtil;
import dev.emperor.utils.render.RoundedUtils;
import dev.emperor.utils.render.StencilUtil;
import dev.emperor.utils.render.animation.Animation;
import dev.emperor.utils.render.animation.Direction;
import dev.emperor.utils.render.animation.impl.ContinualAnimation;
import dev.emperor.utils.render.animation.impl.EaseBackIn;
import dev.emperor.utils.render.fontRender.FontManager;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TargetHud
extends UiModule {
    private boolean sentParticles;
    public final List<Particle> particles = new ArrayList<Particle>();
    private final TimeUtil timer = new TimeUtil();
    private final ContinualAnimation animation2 = new ContinualAnimation();
    final DecimalFormat DF_1 = new DecimalFormat("0.0");
    private EntityLivingBase target;
    private final Animation animation = new EaseBackIn(500, 1.0, 1.5f);

    public TargetHud() {
        super("TargetHud", 50.0, 50.0, 200.0, 120.0);
    }

    @EventTarget
    public void onTick(EventTick event) {
        block6: {
            block7: {
                KillAura aura = Client.instance.moduleManager.getModule(KillAura.class);
                if (!aura.getState()) {
                    this.animation.setDirection(Direction.BACKWARDS);
                }
                if (KillAura.target != null) {
                    this.target = KillAura.target;
                    this.animation.setDirection(Direction.FORWARDS);
                }
                if (!aura.getState()) break block6;
                if (KillAura.target == null) break block7;
                if (this.target == KillAura.target) break block6;
            }
            this.animation.setDirection(Direction.BACKWARDS);
        }
        if (TargetHud.mc.currentScreen instanceof GuiChat) {
            this.animation.setDirection(Direction.FORWARDS);
            this.target = TargetHud.mc.thePlayer;
        }
    }

    @EventTarget
    public void onRender2D(EventRender2D event) {
        float x2 = (float)this.getPosX();
        float y2 = (float)this.getPosY();
        KillAura ka = Client.instance.moduleManager.getModule(KillAura.class);
        if (((Boolean)HUD.multi_targetHUD.getValue()).booleanValue()) {
            if (TargetHud.mc.currentScreen instanceof GuiChat) {
                this.render(x2, y2, 1.0f, TargetHud.mc.thePlayer, false);
            } else if (!KillAura.targets.isEmpty()) {
                int count = 0;
                for (int i = 0; i < KillAura.targets.size(); ++i) {
                    Entity target = KillAura.targets.get(i);
                    if (count > 4) continue;
                    this.render(x2, y2, 1.0f, (EntityLivingBase)target, false);
                    y2 += 60.0f;
                    ++count;
                }
            }
        } else {
            RenderUtil.scaleStart((float)((double)x2 + this.width / 2.0), (float)((double)y2 + this.height / 2.0), (float)this.animation.getOutput());
            this.render(x2, y2, 1.0f, this.target, false);
            RenderUtil.scaleEnd();
        }
    }

    @EventTarget
    public void shader(EventShader event) {
        float x2 = (float)this.getPosX();
        float y2 = (float)this.getPosY();
        KillAura ka = Client.instance.moduleManager.getModule(KillAura.class);
        if (((HUD.THUDMode)((Object)HUD.thudmodeValue.getValue())).equals((Object)HUD.THUDMode.WTFNovo) && event.isBloom()) {
            return;
        }
        if (((Boolean)HUD.multi_targetHUD.getValue()).booleanValue()) {
            if (TargetHud.mc.currentScreen instanceof GuiChat) {
                this.render(x2, y2, 1.0f, TargetHud.mc.thePlayer, true);
            } else if (!KillAura.targets.isEmpty()) {
                int count = 0;
                for (int i = 0; i < KillAura.targets.size(); ++i) {
                    Entity target = KillAura.targets.get(i);
                    if (count > 4) continue;
                    this.render(x2, y2, 1.0f, (EntityLivingBase)target, true);
                    y2 += 60.0f;
                    ++count;
                }
            }
        } else {
            RenderUtil.scaleStart((float)((double)x2 + this.width / 2.0), (float)((double)y2 + this.height / 2.0), (float)this.animation.getOutput());
            this.render(x2, y2, 1.0f, this.target, true);
            RenderUtil.scaleEnd();
        }
    }

    public void render(float x2, float y2, float alpha, EntityLivingBase target, boolean blur) {
        Color firstColor = HUD.color(1);
        Color secondColor = HUD.color(6);
        switch (((HUD.THUDMode)((Object)HUD.thudmodeValue.getValue())).name()) {
            case "Neon": {
                float width = Math.max(128, FontManager.arial20.getStringWidth("Name: " + target.getName()) + 60);
                this.width = width;
                this.height = 50.0;
                if (blur) {
                    Gui.drawRect3(x2, y2, width, 50.0, new Color(0, 0, 0).getRGB());
                    Gui.drawRect3(x2, y2, width, 1.0, HUD.mainColor.getColor());
                    return;
                }
                Gui.drawRect3(x2, y2, width, 50.0, new Color(19, 19, 19, 180).getRGB());
                RenderUtil.drawHGradientRect(x2, y2, width, 1.0, firstColor.getRGB(), secondColor.getRGB());
                int textColor = -1;
                Gui.drawRect3(x2, y2, width, 50.0, new Color(0, 0, 0, (int)(110.0f * alpha)).getRGB());
                int scaleOffset = (int)((float)target.hurtTime * 0.35f);
                float healthPercent = (target.getHealth() + target.getAbsorptionAmount()) / (target.getMaxHealth() + target.getAbsorptionAmount());
                float var = (width - 28.0f) * healthPercent;
                target.animatedHealthBar = AnimationUtil.animate(target.animatedHealthBar, var, 0.1f);
                RenderUtil.drawHGradientRect(x2 + 5.0f, y2 + 40.0f, target.animatedHealthBar, 5.0, firstColor.getRGB(), secondColor.getRGB());
                for (Particle p : this.particles) {
                    p.x = x2 + 20.0f;
                    p.y = y2 + 20.0f;
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    if (!(p.opacity > 4.0f)) continue;
                    p.render2D();
                }
                if (target instanceof AbstractClientPlayer) {
                    GlStateManager.pushMatrix();
                    this.drawBigHead(x2 + 5.0f + (float)scaleOffset / 2.0f, y2 + 5.0f + (float)scaleOffset / 2.0f, 30 - scaleOffset, 30 - scaleOffset, (AbstractClientPlayer)target);
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    GlStateManager.popMatrix();
                } else {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(x2 + 5.0f + (float)scaleOffset / 2.0f + 15.0f, y2 + 5.0f + (float)scaleOffset / 2.0f + 25.0f, 40.0f);
                    GlStateManager.scale(0.31, 0.31, 0.31);
                    this.drawModel(target.rotationYaw, target.rotationPitch, target);
                    GlStateManager.popMatrix();
                }
                if (this.timer.hasTimeElapsed(16L, true)) {
                    for (Particle p : this.particles) {
                        p.updatePosition();
                        if (!(p.opacity < 1.0f)) continue;
                        this.particles.remove(p);
                    }
                }
                double healthNum = MathUtil.round((double)(target.getHealth() + target.getAbsorptionAmount()), 1);
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                FontManager.arial18.drawString(String.valueOf(healthNum), x2 + target.animatedHealthBar + 8.0f, y2 + 38.0f, textColor);
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                FontManager.arial20.drawString("Name: " + target.getName(), x2 + 40.0f, y2 + 8.0f, textColor);
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                FontManager.arial20.drawString("Distance: " + MathUtil.round((double)TargetHud.mc.thePlayer.getDistanceToEntity(target), 1), x2 + 40.0f, y2 + 20.0f, textColor);
                if (target.hurtTime == 9 && !this.sentParticles) {
                    for (int i = 0; i <= 15; ++i) {
                        Particle particle = new Particle();
                        particle.init(x2 + 20.0f, y2 + 20.0f, (float)((Math.random() - 0.5) * 2.0 * 1.4), (float)((Math.random() - 0.5) * 2.0 * 1.4), (float)(Math.random() * 4.0), RenderUtil.reAlpha(Color.RED, target.hurtTime * 10));
                        this.particles.add(particle);
                    }
                    this.sentParticles = true;
                }
                if (target.hurtTime != 8) break;
                this.sentParticles = false;
                break;
            }
            case "Novoline": {
                FontRenderer fr = TargetHud.mc.fontRendererObj;
                double healthPercentage = MathHelper.clamp_float((target.getHealth() + target.getAbsorptionAmount()) / (target.getMaxHealth() + target.getAbsorptionAmount()), 0.0f, 1.0f);
                this.width = Math.max(120, fr.getStringWidth(target.getName()) + 50);
                this.height = 36.0;
                int alphaInt = (int)(alpha * 255.0f);
                Gui.drawRect3(x2, y2, this.width, 36.0, new Color(29, 29, 29, alphaInt).getRGB());
                Gui.drawRect3(x2 + 1.0f, y2 + 1.0f, this.width - 2.0, 34.0, new Color(40, 40, 40, alphaInt).getRGB());
                Gui.drawRect3(x2 + 34.0f, y2 + 15.0f, 83.0, 10.0, -14213603);
                float f = (float)(83.0 * healthPercentage);
                target.animatedHealthBar = AnimationUtil.animate(target.animatedHealthBar, f, 0.1f);
                RenderUtil.drawHGradientRect(x2 + 34.0f, y2 + 15.0f, target.animatedHealthBar, 10.0, firstColor.darker().darker().getRGB(), secondColor.darker().darker().getRGB());
                RenderUtil.drawHGradientRect(x2 + 34.0f, y2 + 15.0f, f, 10.0, firstColor.getRGB(), secondColor.getRGB());
                int textColor = -1;
                int mcTextColor = -1;
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 771);
                if (target instanceof AbstractClientPlayer) {
                    RenderUtil.glColor(textColor);
                    this.drawBigHead(x2 + 3.5f, y2 + 3.0f, 28.0f, 28.0f, (AbstractClientPlayer)target);
                } else {
                    fr.drawStringWithShadow("?", x2 + 17.0f - (float)fr.getStringWidth("?") / 2.0f, y2 + 17.0f - (float)fr.FONT_HEIGHT / 2.0f, mcTextColor);
                }
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 771);
                fr.drawStringWithShadow(target.getName(), x2 + 34.0f, y2 + 5.0f, mcTextColor);
                String healthText = MathUtil.round(healthPercentage * 100.0, 0) + "%";
                fr.drawStringWithShadow(healthText, (float)((double)(x2 + 17.0f) + this.width / 2.0 - (double)((float)fr.getStringWidth(healthText) / 2.0f)), y2 + 16.0f, mcTextColor);
                break;
            }
            case "Exhibition": {
                GlStateManager.pushMatrix();
                this.width = (float)((float)FontManager.arial18.getStringWidth(target.getName()) > 70.0f ? (double)(125.0f + (float)FontManager.arial18.getStringWidth(target.getName()) - 70.0f) : 125.0);
                this.height = 45.0;
                GlStateManager.translate(x2, y2 + 6.0f, 0.0f);
                RenderUtil.skeetRect(0.0, -2.0, (float)FontManager.arial18.getStringWidth(target.getName()) > 70.0f ? (double)(124.0f + (float)FontManager.arial18.getStringWidth(target.getName()) - 70.0f) : 124.0, 38.0, 1.0);
                RenderUtil.skeetRectSmall(0.0, -2.0, 124.0, 38.0, 1.0);
                FontManager.arial18.drawStringWithShadow(target.getName(), 41.0f, 0.3f, -1);
                float health = target.getHealth();
                float healthWithAbsorption = target.getHealth() + target.getAbsorptionAmount();
                float progress = health / target.getMaxHealth();
                Color healthColor = health >= 0.0f ? ColorUtil.getBlendColor(target.getHealth(), target.getMaxHealth()).brighter() : Color.RED;
                double cockWidth = 0.0;
                cockWidth = MathUtil.round(cockWidth, 5);
                if (cockWidth < 50.0) {
                    cockWidth = 50.0;
                }
                double healthBarPos = cockWidth * (double)progress;
                Gui.drawRect(42.5, 10.3, 53.0 + healthBarPos + 0.5, 13.5, healthColor.getRGB());
                if (target.getAbsorptionAmount() > 0.0f) {
                    Gui.drawRect(97.5 - (double)target.getAbsorptionAmount(), 10.3, 103.5, 13.5, new Color(137, 112, 9).getRGB());
                }
                RenderUtil.drawBorderedRect2(42.0, 9.8f, 54.0 + cockWidth, 14.0, 0.5, 0, Color.BLACK.getRGB());
                for (int dist = 1; dist < 10; ++dist) {
                    double cock = cockWidth / 8.5 * (double)dist;
                    Gui.drawRect(43.5 + cock, 9.8, 43.5 + cock + 0.5, 14.0, Color.BLACK.getRGB());
                }
                GlStateManager.scale(0.5, 0.5, 0.5);
                int distance = (int)TargetHud.mc.thePlayer.getDistanceToEntity(target);
                String nice = "HP: " + (int)healthWithAbsorption + " | Dist: " + distance;
                TargetHud.mc.fontRendererObj.drawString(nice, 85.3f, 32.3f, -1, true);
                GlStateManager.scale(2.0, 2.0, 2.0);
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                if (target != null) {
                    TargetHud.drawEquippedShit(28, 20, target);
                }
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                GlStateManager.scale(0.31, 0.31, 0.31);
                GlStateManager.translate(73.0f, 102.0f, 40.0f);
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                this.drawModel(target.rotationYaw, target.rotationPitch, target);
                GlStateManager.popMatrix();
                break;
            }
            case "ThunderHack": {
                double healthPercentage = MathHelper.clamp_float((target.getHealth() + target.getAbsorptionAmount()) / (target.getMaxHealth() + target.getAbsorptionAmount()), 0.0f, 1.0f);
                if (blur) {
                    RoundedUtils.round(x2, y2, 70.0f, 50.0f, 6.0f, new Color(0, 0, 0, 255));
                    RoundedUtils.round(x2 + 50.0f, y2, 10.0f, 50.0f, 6.0f, new Color(0, 0, 0, 255));
                    return;
                }
                this.setWidth(170.0);
                this.setHeight(50.0);
                RoundedUtils.round(x2, y2, 70.0f, 50.0f, 6.0f, new Color(0, 0, 0, 139));
                RoundedUtils.round(x2 + 50.0f, y2, 100.0f, 50.0f, 6.0f, new Color(0, 0, 0, 255));
                RenderUtil.drawImageRound(new ResourceLocation("express/thud.png"), x2 + 30.0f, y2 - 1.0f, 150.0, 80.0, new Color(255, 255, 255, 150).getRGB(), () -> RoundedUtils.round(x2 + 50.0f, y2, 100.0f, 50.0f, 6.0f, new Color(0, 0, 0, 255)));
                GlStateManager.resetColor();
                if (target instanceof AbstractClientPlayer) {
                    ParticleRender.render(x2 + 2.0f, y2 + 2.0f, target);
                    this.drawBigHeadRound(x2 + 2.0f, y2 + 3.0f, 44.0f, 44.0f, (AbstractClientPlayer)target);
                }
                FontManager.arial18.drawString(target.getName(), x2 + 54.0f, y2 + 6.0f, -1);
                float f = (float)(92.0 * healthPercentage);
                target.animatedHealthBar = AnimationUtil.animate(target.animatedHealthBar, f, 0.1f);
                GlStateManager.pushMatrix();
                GlStateManager.scale(0.8, 0.8, 0.8);
                if (target != null) {
                    TargetHud.drawEquippedShit2((double)((int)(x2 + 42.0f)) / 0.8, (double)((int)(y2 + 18.0f)) / 0.8, target);
                }
                GlStateManager.popMatrix();
                GlowUtils.drawGlow(x2 + 54.0f, y2 + 36.0f, target.animatedHealthBar, 8.0f, 16, HUD.mainColor.getColorC(), () -> RoundedUtils.drawRound(x2 + 54.0f, y2 + 36.0f, target.animatedHealthBar, 8.0f, 2.0f, HUD.mainColor.getColorC()));
                RoundedUtils.drawRound(x2 + 54.0f, y2 + 36.0f, target.animatedHealthBar, 8.0f, 2.0f, HUD.mainColor.getColorC());
                FontManager.arial14.drawCenteredString(String.valueOf(MathUtil.round((double)target.getHealth(), 1)), x2 + 100.0f, y2 + 38.0f, -1);
                break;
            }
            case "Raven": {
                ScaledResolution sr = new ScaledResolution(mc);
                GlStateManager.pushMatrix();
                GlStateManager.translate(x2, y2, 0.0f);
                RoundedUtils.round(0.0f, 0.0f, 70.0f + (float)TargetHud.mc.fontRendererObj.getStringWidth(target.getName()), 40.0f, 12.0f, new Color(0, 0, 0, 92));
                RenderUtil.drawOutline(8.0f, 0.0f, 62.0f + (float)TargetHud.mc.fontRendererObj.getStringWidth(target.getName()), 24.0f, 8.0f, 2.0f, 6.0f, firstColor.brighter(), secondColor.brighter());
                TargetHud.mc.fontRendererObj.drawStringWithShadow(target.getName(), 7.0f, 10.0f, new Color(244, 67, 54).getRGB());
                TargetHud.mc.fontRendererObj.drawStringWithShadow(target.getHealth() > TargetHud.mc.thePlayer.getHealth() ? "L" : "W", (float)TargetHud.mc.fontRendererObj.getStringWidth(target.getName()) + 55.0f, 10.0f, target.getHealth() > TargetHud.mc.thePlayer.getHealth() ? new Color(244, 67, 54).getRGB() : new Color(0, 255, 0).getRGB());
                TargetHud.mc.fontRendererObj.drawStringWithShadow(this.DF_1.format(target.getHealth()), 7.0f + (float)TargetHud.mc.fontRendererObj.getStringWidth(target.getName()) + 4.0f, 10.0f, RenderUtil.getHealthColor(target.getHealth(), target.getMaxHealth()).getRGB());
                RoundedUtils.drawGradientRoundLR(6.0f, 25.0f, (int)((70.0f + (float)TargetHud.mc.fontRendererObj.getStringWidth(target.getName()) - 5.0f) * (target.getHealth() / target.getMaxHealth())) - 6, 5.0f, 2.0f, firstColor.brighter(), secondColor.brighter());
                GlStateManager.resetColor();
                GlStateManager.enableAlpha();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                break;
            }
            case "Sils": {
                DecimalFormat DF = new DecimalFormat("0.0");
                this.setWidth(Math.max(120.0f, (float)FontManager.arial18.getStringWidth(target.getName() + "") + 100.0f));
                this.setHeight(45.0);
                RoundedUtils.drawRound(x2, y2, (float)this.getWidth(), (float)this.getHeight(), 5.0f, new Color(0, 0, 0, 120));
                RoundedUtils.drawRoundOutline(x2, y2, (float)this.getWidth(), (float)this.getHeight(), 5.0f, 0.5f, new Color(0, 0, 0, 0), Color.WHITE);
                float hurt_time = Math.max(7, target.hurtTime);
                String str = TargetHud.mc.thePlayer.getHealth() >= target.animatedHealthBar ? "Winning" : "Losing";
                target.animatedHealthBar = AnimationUtil.animate(target.animatedHealthBar, target.getHealth(), 0.2f);
                TargetHud.mc.fontRendererObj.drawStringWithShadow("Name: " + target.getName(), x2 + 43.0f, y2 + 7.0f, Color.WHITE.getRGB());
                TargetHud.mc.fontRendererObj.drawStringWithShadow("HP: " + DF.format(target.animatedHealthBar) + " | " + str, x2 + 43.0f, y2 + 19.0f, Color.WHITE.getRGB());
                RoundedUtils.drawRound(x2 + 43.0f, y2 + 32.0f, (float)(this.getWidth() - 53.0), 5.0f, 2.0f, new Color(255, 255, 255, 40));
                RoundedUtils.drawGradientRound(x2 + 43.0f, y2 + 33.0f, (float)((double)(target.animatedHealthBar / target.getMaxHealth()) * (this.getWidth() - 53.0)), 4.0f, 2.0f, firstColor, firstColor, secondColor, secondColor);
                NetworkPlayerInfo playerInfo = mc.getNetHandler().getPlayerInfo(TargetHud.mc.thePlayer.getUniqueID());
                if (target instanceof EntityPlayer) {
                    playerInfo = mc.getNetHandler().getPlayerInfo(target.getUniqueID());
                }
                if (playerInfo != null && target instanceof AbstractClientPlayer) {
                    float renderHurtTime = (float)target.hurtTime - (target.hurtTime != 0 ? Minecraft.getMinecraft().timer.renderPartialTicks : 0.0f);
                    float hurtPercent = renderHurtTime / 10.0f;
                    GL11.glColor4f((float)1.0f, (float)(1.0f - hurtPercent), (float)(1.0f - hurtPercent), (float)1.0f);
                    GL11.glPushMatrix();
                    this.drawBigHead(x2 + hurt_time, y2 + hurt_time, 44.0f - hurt_time * 2.0f, 44.0f - hurt_time * 2.0f, (AbstractClientPlayer)target);
                    GL11.glPopMatrix();
                }
                if (hurt_time != 7.0f) break;
                hurt_time = 0.0f;
                break;
            }
            case "WTFNovo": {
                double armorValue;
                this.setWidth(Math.max(120, FontManager.arial18.getStringWidth(target.getName()) + 15));
                this.setHeight(38.0);
                double healthPercentage = MathHelper.clamp_float((target.animatedHealthBar + target.getAbsorptionAmount()) / (target.getMaxHealth() + target.getAbsorptionAmount()), 0.0f, 1.0f);
                int bg = new Color(0.0f, 0.0f, 0.0f, 0.4f * alpha).getRGB();
                float hurtPercent = (float)target.hurtTime / 10.0f;
                float scale = hurtPercent == 0.0f ? 1.0f : (hurtPercent < 0.5f ? 1.0f - 0.1f * hurtPercent * 2.0f : 0.9f + 0.1f * (hurtPercent - 0.5f) * 2.0f);
                if (blur) {
                    Gui.drawRect3(x2, y2, this.getWidth(), this.getHeight(), new Color(0, 0, 0).getRGB());
                    return;
                }
                Gui.drawRect3(x2, y2, this.getWidth(), this.getHeight(), bg);
                Gui.drawRect3((double)x2 + 2.5, y2 + 31.0f, this.getWidth() - 6.0, 1.5, bg);
                Gui.drawRect3((double)x2 + 2.5, y2 + 34.0f, this.getWidth() - 6.0, 1.5, bg);
                float endWidth = (float)Math.max(0.0, (this.getWidth() - 6.0) * healthPercentage);
                this.animation2.animate(endWidth, 18);
                if (this.animation2.getOutput() > 0.0f) {
                    RenderUtil.drawGradientRect((double)x2 + 2.5, (double)(y2 + 31.0f), (double)x2 + 1.5 + (double)this.animation2.getOutput(), (double)y2 + 32.5, ColorUtil.applyOpacity(-16737215, alpha), ColorUtil.applyOpacity(-7405631, alpha));
                }
                if ((armorValue = (double)target.getTotalArmorValue() / 20.0) > 0.0) {
                    RenderUtil.drawGradientRect((double)x2 + 2.5, (double)(y2 + 34.0f), (double)x2 + 1.5 + (this.getWidth() - 6.0) * armorValue, (double)y2 + 35.5, ColorUtil.applyOpacity(-16750672, alpha), ColorUtil.applyOpacity(-12986881, alpha));
                }
                GlStateManager.pushMatrix();
                RenderUtil.setAlphaLimit(0.0f);
                int textColor = ColorUtil.applyOpacity(-1, alpha);
                if (target instanceof AbstractClientPlayer) {
                    RenderUtil.color(textColor);
                    float f = 0.8125f;
                    GlStateManager.scale(f, f, f);
                    RenderUtil.scaleStart(x2 / f + 3.0f + 16.0f, y2 / f + 3.0f + 16.0f, scale);
                    GL11.glColor4f((float)1.0f, (float)(1.0f - hurtPercent), (float)(1.0f - hurtPercent), (float)1.0f);
                    this.drawBigHead(x2 / f + 3.0f, y2 / f + 3.0f, 32.0f, 32.0f, (AbstractClientPlayer)target);
                    RenderUtil.scaleEnd();
                } else {
                    Gui.drawRect3(x2 + 3.0f, y2 + 3.0f, 25.0, 25.0, bg);
                    GlStateManager.scale(2.0f, 2.0f, 2.0f);
                    FontManager.arial18.drawStringWithShadow("?", (x2 + 11.0f) / 2.0f, (y2 + 11.0f) / 2.0f, textColor);
                }
                GlStateManager.popMatrix();
                FontManager.arial18.drawStringWithShadow(target.getName(), x2 + 31.0f, y2 + 5.0f, textColor);
                FontManager.arial14.drawStringWithShadow("Health: " + this.DF_1.format(target.getHealth()), x2 + 31.0f, y2 + 15.0f, textColor);
                FontManager.arial14.drawStringWithShadow("Distance: " + this.DF_1.format(TargetHud.mc.thePlayer.getDistanceToEntity(target)) + "m", x2 + 31.0f, y2 + 22.0f, textColor);
                float delta = RenderUtil.deltaTime;
                target.animatedHealthBar = (float)((double)target.animatedHealthBar + (double)(target.getHealth() - target.animatedHealthBar) / Math.pow(2.0, 6.0) * (double)delta);
                break;
            }
            case "Exire": {
                float hurtPercent = (float)target.hurtTime / 10.0f;
                this.setWidth(Math.max(70.0f, (float)FontManager.arial18.getStringWidth(target.getName() + "") + 64.0f));
                this.setHeight(32.0);
                if (blur) {
                    RenderUtil.drawRectWH(x2, y2, (float)this.getWidth(), (float)this.getHeight(), new Color(0, 0, 0, 255).getRGB());
                    return;
                }
                RenderUtil.drawRectWH(x2, y2, (float)this.getWidth(), (float)this.getHeight(), new Color(19, 19, 19, 220).getRGB());
                target.animatedHealthBar = AnimationUtil.animate(target.animatedHealthBar, target.getHealth(), 0.15f);
                FontManager.arial24.drawStringWithShadow(target.getName(), x2 + 32.0f, y2 + 6.0f, Color.WHITE.getRGB());
                RenderUtil.drawRectWH(x2 + 33.0f, y2 + 22.0f, this.getWidth() - 38.0, 6.0, new Color(0, 0, 0, 230).getRGB());
                RoundedUtils.drawGradientCornerLR(x2 + 33.0f, y2 + 23.0f, (float)((double)(target.animatedHealthBar / target.getMaxHealth()) * (this.getWidth() - 38.0)), 4.0f, 0.0f, firstColor, secondColor);
                int textColor = ColorUtil.applyOpacity(-1, alpha);
                if (!(target instanceof AbstractClientPlayer)) break;
                GlStateManager.pushMatrix();
                RenderUtil.color(textColor);
                float f = 0.8125f;
                GlStateManager.scale(f, f, f);
                GL11.glColor4f((float)1.0f, (float)(1.0f - hurtPercent), (float)(1.0f - hurtPercent), (float)1.0f);
                this.drawBigHead(x2 / f + 3.0f, y2 / f + 3.5f, 32.0f, 32.0f, (AbstractClientPlayer)target);
                GlStateManager.popMatrix();
                GlStateManager.resetColor();
                break;
            }
            case "Moon": {
                float getMaxHel = target.getMaxHealth() < 20.0f ? target.getMaxHealth() : 20.0f;
                this.setWidth(Math.max(26.0f + getMaxHel * 3.0f + (float)FontManager.arial18.getStringWidth(target.getName()) - 34.0f, 28.0f + getMaxHel * 3.0f));
                this.setHeight(36.0);
                if (blur) {
                    RoundedUtils.drawRound(x2, y2, Math.max(39.0f + getMaxHel * 3.0f, (float)(39 + FontManager.arial18.getStringWidth(target.getName()))) + 2.0f, 36.0f, 5.0f, new Color(0, 0, 0));
                    return;
                }
                RoundedUtils.drawRound(x2, y2, Math.max(39.0f + getMaxHel * 3.0f, (float)(39 + FontManager.arial18.getStringWidth(target.getName()))) + 2.0f, 36.0f, 5.0f, new Color(0, 0, 0, 100));
                if (target instanceof AbstractClientPlayer) {
                    this.drawBigHeadRound(x2 + 3.0f, y2 + 3.0f, 30.0f, 30.0f, (AbstractClientPlayer)target);
                }
                FontManager.arial18.drawString(target.getName(), x2 + 36.0f, y2 + 6.0f, new Color(255, 255, 255).getRGB());
                double nmsl = (double)target.getHealth() - Math.floor(target.getHealth()) >= 0.5 ? 0.5 : 0.0;
                FontManager.arial14.drawString(Math.floor(target.getHealth()) + nmsl + " HP", x2 + 36.0f, y2 + 6.0f + (float)FontManager.arial18.getHeight(), new Color(255, 255, 255).getRGB());
                target.animatedHealthBar = AnimationUtil.animate(target.animatedHealthBar, target.getHealth(), 0.15f);
                RoundedUtils.drawRound(x2 + 36.0f, y2 + 16.0f + (float)FontManager.arial16.getHeight(), Math.max(getMaxHel * 3.0f, (float)FontManager.arial18.getStringWidth(target.getName())), 5.0f, 2.5f, new Color(0, 0, 0, 200));
                RoundedUtils.drawRound(x2 + 36.0f, y2 + 16.0f + (float)FontManager.arial16.getHeight(), target.animatedHealthBar / target.getMaxHealth() * Math.max(getMaxHel * 3.0f, (float)FontManager.arial18.getStringWidth(target.getName())), 5.0f, 2.5f, HUD.color(8));
                break;
            }
            case "RiseNew": {
                if (blur) {
                    RoundedUtils.drawRoundOutline(x2, y2, (float)this.getWidth(), (float)this.getHeight(), 5.0f, 0.1f, HUD.color(8), HUD.color(1));
                    return;
                }
                DecimalFormat DF1 = new DecimalFormat("0");
                this.setWidth(Math.max(90.0f, (float)FontManager.arial18.getStringWidth(target.getName()) + 70.0f));
                this.setHeight(35.0);
                RoundedUtils.drawRoundOutline(x2, y2, (float)this.getWidth(), (float)this.getHeight(), 5.0f, 0.1f, new Color(255, 255, 255, 10), RenderUtil.reAlpha(HUD.color(1), 200));
                float hurt_time1 = Math.max(7, target.hurtTime);
                target.animatedHealthBar = AnimationUtil.animate(target.animatedHealthBar, target.getHealth(), 0.2f);
                FontManager.arial18.drawString(target.getName(), x2 + 37.0f, y2 + 9.0f, HUD.color(2).getRGB());
                FontManager.arial18.drawString(DF1.format(target.animatedHealthBar), (float)((double)x2 + this.getWidth() - 19.0), y2 + 9.0f, HUD.color(8).getRGB());
                RoundedUtils.drawRound(x2 + 38.0f, y2 + 21.0f, (float)(this.getWidth() - 46.0), 4.0f, 2.0f, new Color(32, 32, 32, 100));
                RoundedUtils.drawGradientRoundLR(x2 + 38.0f, y2 + 21.0f, (float)((double)(target.animatedHealthBar / target.getMaxHealth()) * (this.getWidth() - 46.0)), 4.0f, 2.0f, firstColor, secondColor);
                NetworkPlayerInfo playerInfo1 = mc.getNetHandler().getPlayerInfo(TargetHud.mc.thePlayer.getUniqueID());
                if (target instanceof EntityPlayer) {
                    playerInfo1 = mc.getNetHandler().getPlayerInfo(target.getUniqueID());
                }
                if (playerInfo1 != null && target instanceof AbstractClientPlayer) {
                    float renderHurtTime = (float)target.hurtTime - (target.hurtTime != 0 ? Minecraft.getMinecraft().timer.renderPartialTicks : 0.0f);
                    float hurtPercent1 = renderHurtTime / 10.0f;
                    GL11.glColor4f((float)1.0f, (float)(1.0f - hurtPercent1), (float)(1.0f - hurtPercent1), (float)1.0f);
                    GL11.glPushMatrix();
                    this.drawBigHeadRound2(x2 - 2.0f + hurt_time1, y2 - 2.0f + hurt_time1, 42.0f - hurt_time1 * 2.0f, 42.0f - hurt_time1 * 2.0f, (AbstractClientPlayer)target);
                    GL11.glPopMatrix();
                }
                if (hurt_time1 != 7.0f) break;
                hurt_time1 = 0.0f;
            }
        }
    }

    public static void drawEquippedShit(int x2, int y2, EntityLivingBase target) {
        if (!(target instanceof EntityPlayer)) {
            return;
        }
        GL11.glPushMatrix();
        ArrayList<ItemStack> stuff = new ArrayList<ItemStack>();
        int cock = -2;
        for (int geraltOfNigeria = 3; geraltOfNigeria >= 0; --geraltOfNigeria) {
            ItemStack armor = target.getCurrentArmor(geraltOfNigeria);
            if (armor == null) continue;
            stuff.add(armor);
        }
        if (target.getHeldItem() != null) {
            stuff.add(target.getHeldItem());
        }
        for (ItemStack yes : stuff) {
            if (Minecraft.getMinecraft().theWorld != null) {
                RenderHelper.enableGUIStandardItemLighting();
                cock += 16;
            }
            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.clear(256);
            GlStateManager.enableBlend();
            Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(yes, cock + x2, y2, 255.0f);
            Minecraft.getMinecraft().getRenderItem().renderItemOverlays(Minecraft.getMinecraft().fontRendererObj, yes, cock + x2, y2);
            GlStateManager.disableBlend();
            GlStateManager.scale(0.5, 0.5, 0.5);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.scale(2.0f, 2.0f, 2.0f);
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
            yes.getEnchantmentTagList();
        }
        GL11.glPopMatrix();
    }

    public static void drawEquippedShit2(double x2, double y2, EntityLivingBase target) {
        if (!(target instanceof EntityPlayer)) {
            return;
        }
        GL11.glPushMatrix();
        ArrayList<ItemStack> stuff = new ArrayList<ItemStack>();
        int cock = -2;
        if (target.getHeldItem() != null) {
            stuff.add(target.getHeldItem());
        }
        for (int geraltOfNigeria = 3; geraltOfNigeria >= 0; --geraltOfNigeria) {
            ItemStack armor = target.getCurrentArmor(geraltOfNigeria);
            if (armor == null) continue;
            stuff.add(armor);
        }
        for (ItemStack yes : stuff) {
            if (Minecraft.getMinecraft().theWorld != null) {
                RenderHelper.enableGUIStandardItemLighting();
                cock += 16;
            }
            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.clear(256);
            GlStateManager.enableBlend();
            GL11.glHint((int)3155, (int)4352);
            Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(yes, (double)cock + x2, y2, 255.0f);
            Minecraft.getMinecraft().getRenderItem().renderItemOverlays(Minecraft.getMinecraft().fontRendererObj, yes, (double)cock + x2, y2);
            GlStateManager.disableBlend();
            GlStateManager.scale(0.5, 0.5, 0.5);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.scale(2.0f, 2.0f, 2.0f);
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
            yes.getEnchantmentTagList();
        }
        GL11.glPopMatrix();
    }

    public void drawModel(float yaw, float pitch, EntityLivingBase entityLivingBase) {
        GlStateManager.resetColor();
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, 0.0f, 50.0f);
        GlStateManager.scale(-50.0f, 50.0f, 50.0f);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        float renderYawOffset = entityLivingBase.renderYawOffset;
        float rotationYaw = entityLivingBase.rotationYaw;
        float rotationPitch = entityLivingBase.rotationPitch;
        float prevRotationYawHead = entityLivingBase.prevRotationYawHead;
        float rotationYawHead = entityLivingBase.rotationYawHead;
        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate((float)(-Math.atan(pitch / 40.0f) * 20.0), 1.0f, 0.0f, 0.0f);
        entityLivingBase.renderYawOffset = yaw - 0.4f;
        entityLivingBase.rotationYaw = yaw - 0.2f;
        entityLivingBase.rotationPitch = pitch;
        entityLivingBase.rotationYawHead = entityLivingBase.rotationYaw;
        entityLivingBase.prevRotationYawHead = entityLivingBase.rotationYaw;
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        RenderManager renderManager = mc.getRenderManager();
        renderManager.setPlayerViewY(180.0f);
        renderManager.setRenderShadow(false);
        renderManager.renderEntityWithPosYaw(entityLivingBase, 0.0, 0.0, 0.0, 0.0f, 1.0f);
        renderManager.setRenderShadow(true);
        entityLivingBase.renderYawOffset = renderYawOffset;
        entityLivingBase.rotationYaw = rotationYaw;
        entityLivingBase.rotationPitch = rotationPitch;
        entityLivingBase.prevRotationYawHead = prevRotationYawHead;
        entityLivingBase.rotationYawHead = rotationYawHead;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.resetColor();
    }

    protected void drawBigHead(float x2, float y2, float width, float height, AbstractClientPlayer player) {
        double offset = -(player.hurtTime * 23);
        RenderUtil.glColor(new Color(255, (int)(255.0 + offset), (int)(255.0 + offset)).getRGB());
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        mc.getTextureManager().bindTexture(player.getLocationSkin());
        Gui.drawScaledCustomSizeModalRect(x2, y2, 8.0f, 8.0f, 8, 8, width, height, 64.0f, 64.0f);
        GlStateManager.disableBlend();
        GlStateManager.resetColor();
    }

    protected void drawBigHeadRound(float x2, float y2, float width, float height, AbstractClientPlayer player) {
        StencilUtil.initStencilToWrite();
        RenderUtil.renderRoundedRect(x2, y2, width, height, 4.0f, -1);
        StencilUtil.readStencilBuffer(1);
        RenderUtil.color(-1);
        this.drawBigHead(x2, y2, width, height, player);
        StencilUtil.uninitStencilBuffer();
        GlStateManager.disableBlend();
    }

    protected void drawBigHeadRound2(float x2, float y2, float width, float height, AbstractClientPlayer player) {
        StencilUtil.initStencilToWrite();
        RenderUtil.renderRoundedRect(x2 - 1.0f, y2 - 1.0f, width, height, 6.0f, -1);
        StencilUtil.readStencilBuffer(1);
        RenderUtil.color(-1);
        this.drawBigHead(x2 - 1.0f, y2 - 1.0f, width, height, player);
        StencilUtil.uninitStencilBuffer();
        GlStateManager.disableBlend();
    }

    public static class Particle {
        public float x;
        public float y;
        public float adjustedX;
        public float adjustedY;
        public float deltaX;
        public float deltaY;
        public float size;
        public float opacity;
        public Color color;

        public void render2D() {
            RoundedUtils.round(this.x + this.adjustedX, this.y + this.adjustedY, this.size, this.size, 12.0f, this.color);
        }

        public void updatePosition() {
            for (int i = 1; i <= 2; ++i) {
                this.adjustedX += this.deltaX;
                this.adjustedY += this.deltaY;
                this.deltaY = (float)((double)this.deltaY * 0.97);
                this.deltaX = (float)((double)this.deltaX * 0.97);
                this.opacity -= 1.0f;
                if (!(this.opacity < 1.0f)) continue;
                this.opacity = 1.0f;
            }
        }

        public void init(float x2, float y2, float deltaX, float deltaY, float size, Color color) {
            this.x = x2;
            this.y = y2;
            this.deltaX = deltaX;
            this.deltaY = deltaY;
            this.size = size;
            this.opacity = 254.0f;
            this.color = color;
        }
    }
}

