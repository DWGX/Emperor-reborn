package dev.emperor.module.modules.render;

import dev.emperor.event.EventTarget;
import dev.emperor.event.rendering.EventRLE;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.values.BoolValue;
import dev.emperor.module.values.ColorValue;
import dev.emperor.module.values.ModeValue;
import dev.emperor.module.values.NumberValue;
import dev.emperor.utils.render.RenderUtil;
import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import org.lwjgl.opengl.GL11;

public class Chams
extends Module {
    public static ColorValue chamsColours = new ColorValue("ChamsColor", Color.RED.getRGB());
    public static ModeValue<chamsMode> mode = new ModeValue("ChamsMode", (Enum[])chamsMode.values(), (Enum)chamsMode.Normal);
    public static BoolValue flat = new BoolValue("Flat", false);
    public static BoolValue teamCol = new BoolValue("TeamColor", false);
    public static NumberValue alpha = new NumberValue("Alpha", 170.0, 0.0, 255.0, 1.0);

    public Chams() {
        super("Chams", Category.Render);
    }

    @EventTarget
    public void onRenderLivingEntity(EventRLE evt) {
        if (evt.getEntity() != Chams.mc.thePlayer) {
            if (evt.isPre()) {
                if (mode.getValue() == chamsMode.Colored) {
                    evt.setCancelled(true);
                    try {
                        Render renderObject = mc.getRenderManager().getEntityRenderObject(evt.getEntity());
                        if (renderObject != null && Chams.mc.getRenderManager().renderEngine != null && renderObject instanceof RendererLivingEntity) {
                            GL11.glPushMatrix();
                            GL11.glDisable((int)2929);
                            GL11.glBlendFunc((int)770, (int)771);
                            GL11.glDisable((int)3553);
                            GL11.glEnable((int)3042);
                            Color teamColor = null;
                            if (((Boolean)flat.getValue()).booleanValue()) {
                                GlStateManager.disableLighting();
                            }
                            if (((Boolean)teamCol.getValue()).booleanValue()) {
                                String text = evt.getEntity().getDisplayName().getFormattedText();
                                for (int i = 0; i < text.length(); ++i) {
                                    char oneMore;
                                    int colorCode;
                                    if (text.charAt(i) != '\u00a7' || i + 1 >= text.length() || (colorCode = "0123456789abcdefklmnorg".indexOf(oneMore = Character.toLowerCase(text.charAt(i + 1)))) >= 16) continue;
                                    try {
                                        Color newCol = teamColor = new Color(Chams.mc.fontRendererObj.colorCode[colorCode]);
                                        GL11.glColor4f((float)((float)newCol.getRed() / 255.0f), (float)((float)newCol.getGreen() / 255.0f), (float)((float)newCol.getBlue() / 255.0f), (float)(((Double)alpha.getValue()).floatValue() / 255.0f));
                                        continue;
                                    }
                                    catch (ArrayIndexOutOfBoundsException exception) {
                                        GL11.glColor4f((float)1.0f, (float)0.0f, (float)0.0f, (float)(((Double)alpha.getValue()).floatValue() / 255.0f));
                                    }
                                }
                            } else {
                                int c = RenderUtil.reAlpha(new Color((Integer)chamsColours.getValue()), ((Double)alpha.getValue()).intValue()).getRGB();
                                RenderUtil.glColor(c);
                            }
                            ((RendererLivingEntity)renderObject).renderModel(evt.getEntity(), evt.getLimbSwing(), evt.getLimbSwingAmount(), evt.getAgeInTicks(), evt.getRotationYawHead(), evt.getRotationPitch(), evt.getOffset());
                            GL11.glEnable((int)2929);
                            if (((Boolean)teamCol.getValue()).booleanValue() && teamColor != null) {
                                GL11.glColor4f((float)((float)teamColor.getRed() / 255.0f), (float)((float)teamColor.getGreen() / 255.0f), (float)((float)teamColor.getBlue() / 255.0f), (float)(((Double)alpha.getValue()).floatValue() / 255.0f));
                            } else {
                                int c = (Integer)chamsColours.getValue();
                                GL11.glColor4f((float)new Color(c).getRGB(), (float)new Color(c).getRGB(), (float)new Color(c).getRGB(), (float)(((Double)alpha.getValue()).floatValue() / 255.0f));
                            }
                            ((RendererLivingEntity)renderObject).renderModel(evt.getEntity(), evt.getLimbSwing(), evt.getLimbSwingAmount(), evt.getAgeInTicks(), evt.getRotationYawHead(), evt.getRotationPitch(), evt.getOffset());
                            GL11.glEnable((int)3553);
                            GL11.glDisable((int)3042);
                            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)(((Double)alpha.getValue()).floatValue() / 255.0f));
                            if (((Boolean)flat.getValue()).booleanValue()) {
                                GlStateManager.enableLighting();
                            }
                            GL11.glPopMatrix();
                            ((RendererLivingEntity)renderObject).renderLayers(evt.getEntity(), evt.getLimbSwing(), evt.getLimbSwingAmount(), Chams.mc.timer.renderPartialTicks, evt.getAgeInTicks(), evt.getRotationYawHead(), evt.getRotationPitch(), evt.getOffset());
                            GL11.glPopMatrix();
                        }
                    }
                    catch (Exception exception) {}
                } else {
                    GL11.glEnable((int)32823);
                    GL11.glPolygonOffset((float)1.0f, (float)-1100000.0f);
                }
            } else if (mode.getValue() != chamsMode.Colored && evt.isPost()) {
                GL11.glDisable((int)32823);
                GL11.glPolygonOffset((float)1.0f, (float)1100000.0f);
            }
        }
    }

    public static enum chamsMode {
        Normal,
        Colored;

    }
}

