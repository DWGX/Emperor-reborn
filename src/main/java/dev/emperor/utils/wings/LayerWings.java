package dev.emperor.utils.wings;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class LayerWings
implements LayerRenderer<AbstractClientPlayer> {
    private static ModelWings modelWings;
    private final RenderPlayer playerRenderer;
    private final ResourceLocation wing = new ResourceLocation("express/wings.png");

    public LayerWings(RenderPlayer playerRenderer) {
        this.playerRenderer = playerRenderer;
        modelWings = new ModelWings(2);
    }

    @Override
    public void doRenderLayer(AbstractClientPlayer player, float v, float v1, float v2, float v3, float v4, float v5, float v6) {
        if (player.isInvisible() || player != Minecraft.getMinecraft().thePlayer) {
            return;
        }
        GlStateManager.pushMatrix();
        this.playerRenderer.getMainModel().bipedBody.postRender(0.0625f);
        if (player.isSneaking()) {
            GlStateManager.translate(0.0, (double)0.2f, 0.0);
        }
        modelWings.renderLegacy(0.13f, 0.0625f, this.wing);
        GlStateManager.popMatrix();
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}

