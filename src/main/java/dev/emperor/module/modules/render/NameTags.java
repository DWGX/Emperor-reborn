package dev.emperor.module.modules.render;

import dev.emperor.Client;
import dev.emperor.event.EventTarget;
import dev.emperor.event.rendering.EventRender2D;
import dev.emperor.event.rendering.EventRender3D;
import dev.emperor.event.rendering.EventRenderNameTag;
import dev.emperor.event.rendering.EventShader;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.modules.combat.AntiBot;
import dev.emperor.module.modules.combat.KillAura;
import dev.emperor.module.modules.misc.Teams;
import dev.emperor.utils.ESPUtil;
import dev.emperor.utils.HYTUtils;
import dev.emperor.utils.render.RenderUtil;
import dev.emperor.utils.render.RoundedUtils;
import dev.emperor.utils.render.fontRender.FontManager;
import dev.emperor.utils.render.fontRender.RapeMasterFontManager;
import java.awt.Color;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.compatibility.util.vector.Vector4f;

public class NameTags
extends Module {
    private final Map<Entity, Vector4f> entityPosition = new HashMap<Entity, Vector4f>();
    private final DecimalFormat DF_1 = new DecimalFormat("0.0");

    public NameTags() {
        super("NameTags", Category.Render);
    }

    @EventTarget
    public void onRender3DEvent(EventRender3D event) {
        this.entityPosition.clear();
        for (Entity entity : NameTags.mc.theWorld.loadedEntityList) {
            if (!this.shouldRender(entity) || !ESPUtil.isInView(entity)) continue;
            this.entityPosition.put(entity, ESPUtil.getEntityPositionsOn2D(entity));
        }
    }

    @EventTarget
    public void onRenderNameTag(EventRenderNameTag event) {
        if (event.getTarget() instanceof EntityPlayer) {
            event.setCancelled(true);
        }
    }

    private void drawShit(EntityLivingBase entity, float x2, float y2, float right, boolean blur) {
        RapeMasterFontManager font = FontManager.arial20;
        EntityLivingBase renderingEntity = entity;
        String rank = "";
        if (renderingEntity == NameTags.mc.thePlayer) {
            rank = "\u00a7a[You] ";
        }
        if (renderingEntity == KillAura.target) {
            rank = "\u00a74[Target] ";
        } else if (Client.instance.moduleManager.getModule(Teams.class).getState() && Teams.isSameTeam(renderingEntity)) {
            rank = "\u00a7a[Team]";
        } else if (HYTUtils.isStrength((EntityPlayer)renderingEntity) > 0 && renderingEntity != NameTags.mc.thePlayer && !Teams.isSameTeam(renderingEntity)) {
            rank = "\u00a74[Strength] ";
        } else if (HYTUtils.isRegen((EntityPlayer)renderingEntity) > 0 && renderingEntity != NameTags.mc.thePlayer && !Teams.isSameTeam(renderingEntity)) {
            rank = "\u00a74[Regen]";
        } else if (HYTUtils.isHoldingGodAxe((EntityPlayer)renderingEntity) && renderingEntity != NameTags.mc.thePlayer && !Teams.isSameTeam(renderingEntity)) {
            rank = "\u00a74[GodAxe] ";
        } else if (HYTUtils.isKBBall(renderingEntity.getHeldItem()) && renderingEntity != NameTags.mc.thePlayer && !Teams.isSameTeam(renderingEntity)) {
            rank = "\u00a74[KBBall] ";
        } else if (HYTUtils.hasEatenGoldenApple((EntityPlayer)renderingEntity) > 0 && renderingEntity != NameTags.mc.thePlayer && !Teams.isSameTeam(renderingEntity)) {
            rank = "\u00a74[GApple] ";
        }
        String name = renderingEntity.getDisplayName().getFormattedText();
        StringBuilder text = new StringBuilder(rank + "\u00a7f" + name + " " + this.DF_1.format(renderingEntity.getHealth()));
        double fontScale = 0.8;
        float middle = x2 + (right - x2) / 2.0f;
        double fontHeight = (double)font.getHeight() * fontScale;
        float textWidth = font.getStringWidth(text.toString());
        middle = (float)((double)middle - (double)textWidth * fontScale / 2.0);
        GlStateManager.pushMatrix();
        GlStateManager.translate((double)middle, (double)y2 - (fontHeight + 2.0), 0.0);
        GlStateManager.scale(fontScale, fontScale, 1.0);
        GlStateManager.translate((double)(-middle), -((double)y2 - (fontHeight + 2.0)), 0.0);
        Color backgroundTagColor = new Color(0, 0, 0, 100);
        RoundedUtils.drawRound(middle - 3.0f - 2.0f, (float)((double)y2 - (fontHeight + 7.0)) - 2.0f, textWidth + 6.0f + 6.0f, 1.0f, 1.0f, HUD.color(8));
        RoundedUtils.drawRound(middle - 3.0f - 2.0f, (float)((double)y2 - (fontHeight + 7.0)), textWidth + 6.0f + 6.0f, (float)(fontHeight / fontScale), 1.0f, new Color(19, 19, 19, 200));
        RenderUtil.resetColor();
        GL11.glPopMatrix();
        if (!blur) {
            FontManager.arial16.drawStringWithShadow(text.toString(), middle, (float)((double)y2 - (fontHeight + 4.5)) + 1.0f, -1);
        }
    }

    private void drawNeon(EntityLivingBase entity, float x2, float y2, float right, boolean blur) {
        RapeMasterFontManager font = FontManager.arial20;
        EntityLivingBase renderingEntity = entity;
        String rank = "";
        if (renderingEntity == NameTags.mc.thePlayer) {
            rank = "\u00a7a[You] ";
        }
        if (renderingEntity == KillAura.target) {
            rank = "\u00a74[Target] ";
        } else if (Client.instance.moduleManager.getModule(Teams.class).getState() && Teams.isSameTeam(renderingEntity)) {
            rank = "\u00a7a[Team]";
        } else if (HYTUtils.isStrength((EntityPlayer)renderingEntity) > 0 && renderingEntity != NameTags.mc.thePlayer && !Teams.isSameTeam(renderingEntity)) {
            rank = "\u00a74[Strength] ";
        } else if (HYTUtils.isRegen((EntityPlayer)renderingEntity) > 0 && renderingEntity != NameTags.mc.thePlayer && !Teams.isSameTeam(renderingEntity)) {
            rank = "\u00a74[Regen]";
        } else if (HYTUtils.isHoldingGodAxe((EntityPlayer)renderingEntity) && renderingEntity != NameTags.mc.thePlayer && !Teams.isSameTeam(renderingEntity)) {
            rank = "\u00a74[GodAxe] ";
        } else if (HYTUtils.isKBBall(renderingEntity.getHeldItem()) && renderingEntity != NameTags.mc.thePlayer && !Teams.isSameTeam(renderingEntity)) {
            rank = "\u00a74[KBBall] ";
        } else if (HYTUtils.hasEatenGoldenApple((EntityPlayer)renderingEntity) > 0 && renderingEntity != NameTags.mc.thePlayer && !Teams.isSameTeam(renderingEntity)) {
            rank = "\u00a74[GApple] ";
        }
        String name = renderingEntity.getDisplayName().getFormattedText();
        StringBuilder text = new StringBuilder(rank + "\u00a7f" + name + " " + this.DF_1.format(renderingEntity.getHealth()));
        double fontScale = 0.8;
        float middle = x2 + (right - x2) / 2.0f;
        double fontHeight = (double)font.getHeight() * fontScale;
        float textWidth = font.getStringWidth(text.toString());
        middle = (float)((double)middle - (double)textWidth * fontScale / 2.0);
        GlStateManager.pushMatrix();
        GlStateManager.translate((double)middle, (double)y2 - (fontHeight + 2.0), 0.0);
        GlStateManager.scale(fontScale, fontScale, 1.0);
        GlStateManager.translate((double)(-middle), -((double)y2 - (fontHeight + 2.0)), 0.0);
        Color backgroundTagColor = new Color(0, 0, 0, 100);
        RoundedUtils.drawRound(middle - 3.0f - 15.0f, (float)((double)y2 - (fontHeight + 7.0)), textWidth + 6.0f + 30.0f, (float)(fontHeight / fontScale), 4.0f, backgroundTagColor);
        RenderUtil.resetColor();
        GL11.glPopMatrix();
        if (!blur) {
            FontManager.arial16.drawStringWithShadow(text.toString(), middle, (float)((double)y2 - (fontHeight + 5.0)) + 1.0f, -1);
        }
    }

    @EventTarget
    public void onShaderEvent(EventShader e) {
        for (Entity entity : this.entityPosition.keySet()) {
            Vector4f pos = this.entityPosition.get(entity);
            float x2 = pos.getX();
            float y2 = pos.getY();
            float right = pos.getZ();
            if (!(entity instanceof EntityLivingBase)) continue;
            HUD hud = this.getModule(HUD.class);
            switch ((HUD.HUDmode)((Object)hud.hudModeValue.getValue())) {
                case Shit: {
                    this.drawShit((EntityLivingBase)entity, x2, y2, right, true);
                    break;
                }
                case Neon: {
                    this.drawNeon((EntityLivingBase)entity, x2, y2, right, true);
                }
            }
        }
    }

    @EventTarget
    public void onRender2DEvent(EventRender2D e) {
        for (Entity entity : this.entityPosition.keySet()) {
            Vector4f pos = this.entityPosition.get(entity);
            float x2 = pos.getX();
            float y2 = pos.getY();
            float right = pos.getZ();
            if (!(entity instanceof EntityLivingBase)) continue;
            HUD hud = this.getModule(HUD.class);
            switch ((HUD.HUDmode)((Object)hud.hudModeValue.getValue())) {
                case Shit: {
                    this.drawShit((EntityLivingBase)entity, x2, y2, right, false);
                    break;
                }
                case Neon: {
                    this.drawNeon((EntityLivingBase)entity, x2, y2, right, false);
                }
            }
        }
    }

    private boolean shouldRender(Entity entity) {
        if (entity.isDead || entity.isInvisible()) {
            return false;
        }
        if (AntiBot.isServerBot(entity)) {
            return false;
        }
        if (entity instanceof EntityPlayer) {
            if (entity == NameTags.mc.thePlayer) {
                return NameTags.mc.gameSettings.thirdPersonView != 0;
            }
            return !entity.getDisplayName().getUnformattedText().contains("[NPC");
        }
        return false;
    }
}

