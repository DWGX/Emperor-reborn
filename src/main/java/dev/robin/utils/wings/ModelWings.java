package dev.robin.utils.wings;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ModelWings
extends ModelBase {
    private final List<ModelBase> compiledFrames = new ArrayList<ModelBase>();
    private int frameCount;

    public ModelWings(int n) {
        this.textureWidth = 256;
        this.textureHeight = 256;
        this.frameCount = n;
        for (int i = 0; i < n; ++i) {
            ModelBase modelBase = new ModelBase(){};
            modelBase.textureHeight = 256;
            modelBase.textureWidth = 256;
            modelBase.setTextureOffset("wing.skin", -56, 88);
            modelBase.setTextureOffset("wingtip.skin", -56, 144);
            modelBase.setTextureOffset("wing.bone", 112, 88);
            modelBase.setTextureOffset("wingtip.bone", 112, 136);
            ModelRenderer modelRenderer = new ModelRenderer(modelBase, "wing");
            modelRenderer.setRotationPoint(-12.0f, 5.0f, 2.0f);
            modelRenderer.addBox("bone", -56.0f, -4.0f, -4.0f, 56, 8, 8);
            modelRenderer.addBox("skin", -56.0f, 0.0f, 2.0f, 56, 0, 56);
            ModelRenderer modelRenderer2 = new ModelRenderer(modelBase, "wingtip");
            modelRenderer2.setRotationPoint(-56.0f, 0.0f, 0.0f);
            modelRenderer2.addBox("bone", -56.0f, -2.0f, -2.0f, 56, 4, 4);
            modelRenderer2.addBox("skin", -56.0f, 0.0f, 2.0f, 56, 0, 56);
            modelRenderer.addChild(modelRenderer2);
            if (n > 1) {
                for (ModelRenderer modelRenderer3 : modelBase.boxList) {
                    for (ModelBox modelBox : modelRenderer3.cubeList) {
                        for (int j2 = 0; j2 < modelBox.quadList.length; ++j2) {
                            TexturedQuad texturedQuad = modelBox.quadList[j2];
                            for (int k2 = 0; k2 < texturedQuad.vertexPositions.length; ++k2) {
                                PositionTextureVertex positionTextureVertex = texturedQuad.vertexPositions[k2];
                                texturedQuad.vertexPositions[k2] = positionTextureVertex.setTexturePosition(positionTextureVertex.texturePositionX, this.mapV(positionTextureVertex.texturePositionY, i * n, n));
                            }
                        }
                    }
                }
            }
            this.compiledFrames.add(modelBase);
        }
    }

    private float mapV(float f, int n, int n2) {
        return (float)((double)(f / (float)n2) + Math.floor((float)n / (float)n2) / (double)n2);
    }

    public void renderLegacy(float f, float f2, ResourceLocation resourceLocation) {
        int n = 1;
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        GlStateManager.pushMatrix();
        GlStateManager.scale(f, f, f);
        GL11.glRotatef((float)15.0f, (float)1.0f, (float)0.0f, (float)0.0f);
        GL11.glTranslatef((float)0.0f, (float)0.5f, (float)0.25f);
        float f3 = (float)(System.currentTimeMillis() % 2000L) / 2000.0f * (float)Math.PI * 2.0f;
        ModelBase object = this.compiledFrames.get(n % this.compiledFrames.size());
        ModelRenderer modelRenderer = object.boxList.get(0);
        ModelRenderer modelRenderer2 = object.boxList.get(1);
        for (int i = 0; i < 2; ++i) {
            GL11.glEnable((int)2884);
            modelRenderer.rotateAngleX = -0.125f - MathHelper.cos(f3) * 0.2f;
            modelRenderer.rotateAngleY = 0.75f;
            modelRenderer.rotateAngleZ = (float)((double)MathHelper.sin(f3) + 0.125) * 0.8f;
            modelRenderer2.rotateAngleZ = (float)((double)MathHelper.sin(f3 + 2.0f) + 0.5) * 0.75f;
            modelRenderer.render(f2);
            GlStateManager.scale(-1.0f, 1.0f, 1.0f);
            if (i == 0) {
                GL11.glCullFace((int)1028);
            }
            GlStateManager.shadeModel(7424);
            GlStateManager.disableBlend();
        }
        GlStateManager.popMatrix();
        GL11.glCullFace((int)1029);
        GL11.glDisable((int)2884);
    }
}

