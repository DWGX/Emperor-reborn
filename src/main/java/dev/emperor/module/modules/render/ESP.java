package dev.emperor.module.modules.render;

import dev.emperor.Client;
import dev.emperor.event.EventTarget;
import dev.emperor.event.rendering.EventRender2D;
import dev.emperor.event.rendering.EventRender3D;
import dev.emperor.event.rendering.EventRenderNameTag;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.modules.combat.AntiBot;
import dev.emperor.module.modules.misc.Teams;
import dev.emperor.module.modules.player.Blink;
import dev.emperor.module.values.BoolValue;
import dev.emperor.module.values.NumberValue;
import dev.emperor.utils.render.Colors;
import dev.emperor.utils.render.RenderUtil;
import dev.emperor.utils.render.fontRender.FontManager;
import dev.emperor.utils.render.fontRender.RapeMasterFontManager;
import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import org.lwjgl.BufferUtils;
import org.lwjgl.compatibility.display.Display;
import org.lwjgl.compatibility.util.glu.GLU;
import org.lwjgl.opengl.GL11;

public class ESP
extends Module {
    private final BoolValue armorValue = new BoolValue("Armor", true);
    private final BoolValue healthValue = new BoolValue("Health", true);
    private final BoolValue boxValue = new BoolValue("Box", true);
    private final BoolValue nameValue = new BoolValue("Name", true);
    private final NumberValue width2d = new NumberValue("BoxWidth", 0.5, 0.1, 1.0, 0.1);
    private final DecimalFormat decimalFormat = new DecimalFormat("0.0#", new DecimalFormatSymbols(Locale.ENGLISH));
    private static final FloatBuffer modelView = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
    private static final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private final List<Vec3> positions = new ArrayList<Vec3>();

    public ESP() {
        super("ESP", Category.Render);
    }

    @EventTarget
    public void onRender3D(EventRender3D event) {
        for (Entity entity : ESP.mc.theWorld.loadedEntityList) {
            if (!(entity instanceof EntityPlayer) && !this.isValid(entity)) continue;
            ESP.updateView();
        }
    }

    @EventTarget
    public void onRenderNameTag(EventRenderNameTag e) {
        if (((Boolean)this.nameValue.getValue()).booleanValue() && e.getTarget() instanceof EntityPlayer) {
            e.setCancelled(true);
        }
    }

    @EventTarget
    public void onRender2DEvent(EventRender2D e) {
        ScaledResolution sr = RenderUtil.getScaledResolution();
        GlStateManager.pushMatrix();
        double twoScale = (double)sr.getScaleFactor() / Math.pow(sr.getScaleFactor(), 2.0);
        GlStateManager.scale(twoScale, twoScale, twoScale);
        for (EntityPlayer entity : ESP.getLoadedPlayers()) {
            if (!this.isValid(entity) || !RenderUtil.isInViewFrustrum(entity)) continue;
            this.updatePositions(entity);
            int maxLeft = Integer.MAX_VALUE;
            int maxRight = Integer.MIN_VALUE;
            int maxBottom = Integer.MIN_VALUE;
            int maxTop = Integer.MAX_VALUE;
            Iterator<Vec3> iterator2 = this.positions.iterator();
            boolean canEntityBeSeen = false;
            while (iterator2.hasNext()) {
                Vec3 screenPosition = ESP.WorldToScreen(iterator2.next());
                if (screenPosition == null || !(screenPosition.zCoord >= 0.0) || !(screenPosition.zCoord < 1.0)) continue;
                maxLeft = (int)Math.min(screenPosition.xCoord, (double)maxLeft);
                maxRight = (int)Math.max(screenPosition.xCoord, (double)maxRight);
                maxBottom = (int)Math.max(screenPosition.yCoord, (double)maxBottom);
                maxTop = (int)Math.min(screenPosition.yCoord, (double)maxTop);
                canEntityBeSeen = true;
            }
            if (!canEntityBeSeen) continue;
            if (((Boolean)this.healthValue.getValue()).booleanValue()) {
                this.drawHealth(entity, maxLeft, maxTop, maxBottom);
            }
            if (((Boolean)this.armorValue.getValue()).booleanValue()) {
                this.drawArmor(entity, maxTop, maxRight, maxBottom);
            }
            if (((Boolean)this.boxValue.getValue()).booleanValue()) {
                this.drawBox(maxLeft, maxTop, maxRight, maxBottom);
            }
            if (Client.instance.moduleManager.getModule(NameTags.class).state || !((Boolean)this.nameValue.getValue()).booleanValue()) continue;
            this.drawName(entity, maxLeft, maxTop, maxRight);
        }
        GlStateManager.popMatrix();
    }

    private void drawName(Entity e, int left, int top, int right) {
        EntityPlayer ent = (EntityPlayer)e;
        String renderName = ent.getName();
        RapeMasterFontManager font = FontManager.arial18;
        float meme2 = (float)((double)(right - left) / 2.0 - (double)font.getStringWidth(renderName));
        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        FontManager.arial18.drawStringWithShadow(renderName, ((float)left + meme2) / 2.0f, ((float)top - (float)font.getStringWidth(renderName) / 1.5f * 2.0f) / 2.0f - 4.0f, this.getColor(ent));
        GlStateManager.popMatrix();
    }

    public static List<EntityPlayer> getLoadedPlayers() {
        return ESP.mc.theWorld.playerEntities;
    }

    private void drawBox(int left, int top, int right, int bottom) {
        RenderUtil.drawRectBordered((double)left + 0.5, (double)top + 0.5, (double)right - 0.5, (double)bottom - 0.5, 1.0, Colors.getColor(0, 0, 0, 0), Colors.WHITE);
        RenderUtil.drawRectBordered((double)left - 0.5, (double)top - 0.5, (double)right + 0.5, (double)bottom + 0.5, 1.0, Colors.getColor(0, 0), Colors.getColor(0));
        RenderUtil.drawRectBordered((double)left + 1.5, (double)top + 1.5, (double)right - 1.5, (double)bottom - 1.5, 1.0, Colors.getColor(0, 0), Colors.getColor(0));
    }

    private void drawArmor(EntityLivingBase entityLivingBase, float top, float right, float bottom) {
        float height = bottom + 1.0f - top;
        float currentArmor = entityLivingBase.getTotalArmorValue();
        float armorPercent = currentArmor / 20.0f;
        float MOVE = 2.0f;
        boolean line = true;
        RenderUtil.drawESPRect(right + 2.0f + 1.0f + 2.0f, top - 2.0f, right + 1.0f - 1.0f + 2.0f, bottom + 1.0f, new Color(25, 25, 25, 150).getRGB());
        RenderUtil.drawESPRect(right + 3.0f + 2.0f, top + height * (1.0f - armorPercent) - 1.0f, right + 1.0f + 2.0f, bottom, new Color(78, 206, 229).getRGB());
        RenderUtil.drawESPRect(right + 3.0f + 2.0f + 1.0f, bottom + 1.0f, right + 3.0f + 2.0f, top - 2.0f, new Color(0, 0, 0, 255).getRGB());
        RenderUtil.drawESPRect(right + 1.0f + 2.0f, bottom + 1.0f, right + 1.0f + 2.0f - 1.0f, top - 2.0f, new Color(0, 0, 0, 255).getRGB());
        RenderUtil.drawESPRect(right + 1.0f + 2.0f, top - 1.0f, right + 3.0f + 2.0f, top - 2.0f, new Color(0, 0, 0, 255).getRGB());
        RenderUtil.drawESPRect(right + 1.0f + 2.0f, bottom + 1.0f, right + 3.0f + 2.0f, bottom, new Color(0, 0, 0, 255).getRGB());
    }

    private void drawHealth(EntityLivingBase entityLivingBase, float left, float top, float bottom) {
        float height = bottom + 1.0f - top;
        float currentHealth = entityLivingBase.getHealth();
        float maxHealth = entityLivingBase.getMaxHealth();
        float healthPercent = currentHealth / maxHealth;
        float MOVE = 2.0f;
        boolean line = true;
        String healthStr = "\u00a7f" + this.decimalFormat.format(currentHealth) + "\u00a7c\u2764";
        float bottom2 = top + height * (1.0f - healthPercent) - 1.0f;
        float health = entityLivingBase.getHealth();
        float[] fractions = new float[]{0.0f, 0.5f, 1.0f};
        Color[] colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
        float progress = health / entityLivingBase.getMaxHealth();
        Color customColor = health >= 0.0f ? Colors.blendColors(fractions, colors, progress).brighter() : Color.RED;
        ESP.mc.fontRendererObj.drawStringWithShadow(healthStr, left - 3.0f - 2.0f - (float)ESP.mc.fontRendererObj.getStringWidth(healthStr), bottom2, -1);
        RenderUtil.drawESPRect(left - 3.0f - 2.0f, bottom, left - 1.0f - 2.0f, top - 1.0f, new Color(25, 25, 25, 150).getRGB());
        RenderUtil.drawESPRect(left - 3.0f - 2.0f, bottom, left - 1.0f - 2.0f, bottom2, customColor.getRGB());
        RenderUtil.drawESPRect(left - 3.0f - 2.0f, bottom + 1.0f, left - 3.0f - 2.0f - 1.0f, top - 2.0f, new Color(0, 0, 0, 255).getRGB());
        RenderUtil.drawESPRect(left - 1.0f - 2.0f + 1.0f, bottom + 1.0f, left - 1.0f - 2.0f, top - 2.0f, new Color(0, 0, 0, 255).getRGB());
        RenderUtil.drawESPRect(left - 3.0f - 2.0f, top - 1.0f, left - 1.0f - 2.0f, top - 2.0f, new Color(0, 0, 0, 255).getRGB());
        RenderUtil.drawESPRect(left - 3.0f - 2.0f, bottom + 1.0f, left - 1.0f - 2.0f, bottom, new Color(0, 0, 0, 255).getRGB());
    }

    private int getColor(EntityLivingBase ent) {
        if (AntiBot.isServerBot(ent)) {
            return new Color(255, 0, 0).getRGB();
        }
        if (Teams.isSameTeam(ent) || ent instanceof EntityPlayerSP) {
            return new Color(0, 255, 0).getRGB();
        }
        return new Color(255, 0, 0).getRGB();
    }

    private static Vec3 WorldToScreen(Vec3 position) {
        FloatBuffer screenPositions = BufferUtils.createFloatBuffer((int)3);
        boolean result = GLU.gluProject((float)((float)position.xCoord), (float)((float)position.yCoord), (float)((float)position.zCoord), (FloatBuffer)modelView, (FloatBuffer)projection, (IntBuffer)viewport, (FloatBuffer)screenPositions);
        if (result) {
            return new Vec3(screenPositions.get(0), (float) Display.getHeight() - screenPositions.get(1), screenPositions.get(2));
        }
        return null;
    }

    public void updatePositions(Entity entity) {
        this.positions.clear();
        Vec3 position = ESP.getEntityRenderPosition(entity);
        double x2 = position.xCoord - entity.posX;
        double y2 = position.yCoord - entity.posY;
        double z = position.zCoord - entity.posZ;
        double height = entity instanceof EntityItem ? 0.5 : (double)entity.height + 0.1;
        double width = entity instanceof EntityItem ? 0.25 : (Double)this.width2d.getValue();
        AxisAlignedBB aabb = new AxisAlignedBB(entity.posX - width + x2, entity.posY + y2, entity.posZ - width + z, entity.posX + width + x2, entity.posY + height + y2, entity.posZ + width + z);
        this.positions.add(new Vec3(aabb.minX, aabb.minY, aabb.minZ));
        this.positions.add(new Vec3(aabb.minX, aabb.minY, aabb.maxZ));
        this.positions.add(new Vec3(aabb.minX, aabb.maxY, aabb.minZ));
        this.positions.add(new Vec3(aabb.minX, aabb.maxY, aabb.maxZ));
        this.positions.add(new Vec3(aabb.maxX, aabb.minY, aabb.minZ));
        this.positions.add(new Vec3(aabb.maxX, aabb.minY, aabb.maxZ));
        this.positions.add(new Vec3(aabb.maxX, aabb.maxY, aabb.minZ));
        this.positions.add(new Vec3(aabb.maxX, aabb.maxY, aabb.maxZ));
    }

    private static Vec3 getEntityRenderPosition(Entity entity) {
        return new Vec3(ESP.getEntityRenderX(entity), ESP.getEntityRenderY(entity), ESP.getEntityRenderZ(entity));
    }

    private static double getEntityRenderX(Entity entity) {
        return entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)Minecraft.getMinecraft().timer.renderPartialTicks - Minecraft.getMinecraft().getRenderManager().renderPosX;
    }

    private static double getEntityRenderY(Entity entity) {
        return entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)Minecraft.getMinecraft().timer.renderPartialTicks - Minecraft.getMinecraft().getRenderManager().renderPosY;
    }

    private static double getEntityRenderZ(Entity entity) {
        return entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)Minecraft.getMinecraft().timer.renderPartialTicks - Minecraft.getMinecraft().getRenderManager().renderPosZ;
    }

    private boolean isValid(Entity entity) {
        Blink blink = Client.instance.moduleManager.getModule(Blink.class);
        if (entity == ESP.mc.thePlayer && ESP.mc.gameSettings.thirdPersonView == 0) {
            return false;
        }
        if (entity.isInvisible()) {
            return false;
        }
        if (entity instanceof EntityArmorStand) {
            return false;
        }
        if (blink.getState()) {
            return true;
        }
        return entity instanceof EntityPlayer;
    }

    private static void updateView() {
        GL11.glGetFloatv((int)2982, (FloatBuffer)modelView);
        GL11.glGetFloatv((int)2983, (FloatBuffer)projection);
        GL11.glGetIntegerv((int)2978, (IntBuffer)viewport);
    }
}

