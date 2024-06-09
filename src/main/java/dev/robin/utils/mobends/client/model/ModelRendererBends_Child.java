package dev.robin.utils.mobends.client.model;

import dev.robin.utils.mobends.client.model.ModelRendererBends;
import net.minecraft.client.model.ModelBase;
import org.lwjgl.opengl.GL11;

public class ModelRendererBends_Child
extends ModelRendererBends {
    ModelRendererBends momModel;

    public ModelRendererBends_Child(ModelBase argModel) {
        super(argModel);
    }

    public ModelRendererBends_Child(ModelBase argModel, String argName) {
        super(argModel, argName);
    }

    public ModelRendererBends_Child(ModelBase argModel, int argTexOffsetX, int argTexOffsetY) {
        super(argModel, argTexOffsetX, argTexOffsetY);
    }

    public ModelRendererBends_Child setMother(ModelRendererBends argMom) {
        this.momModel = argMom;
        return this;
    }

    @Override
    public void postRender(float p_78794_1_) {
        this.updateBends(p_78794_1_);
        this.momModel.postRender(p_78794_1_);
        if (this.rotateAngleX == 0.0f && this.rotateAngleY == 0.0f && this.rotateAngleZ == 0.0f) {
            if (this.rotationPointX != 0.0f || this.rotationPointY != 0.0f || this.rotationPointZ != 0.0f) {
                GL11.glTranslatef((float)(this.rotationPointX * p_78794_1_), (float)(this.rotationPointY * p_78794_1_), (float)(this.rotationPointZ * p_78794_1_));
                GL11.glRotatef((float)(-this.pre_rotation.getY()), (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glRotatef((float)this.pre_rotation.getX(), (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)this.pre_rotation.getZ(), (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glScalef((float)this.scaleX, (float)this.scaleY, (float)this.scaleZ);
            }
        } else {
            GL11.glTranslatef((float)(this.rotationPointX * p_78794_1_), (float)(this.rotationPointY * p_78794_1_), (float)(this.rotationPointZ * p_78794_1_));
            GL11.glRotatef((float)(-this.pre_rotation.getY()), (float)0.0f, (float)1.0f, (float)0.0f);
            GL11.glRotatef((float)this.pre_rotation.getX(), (float)1.0f, (float)0.0f, (float)0.0f);
            GL11.glRotatef((float)this.pre_rotation.getZ(), (float)0.0f, (float)0.0f, (float)1.0f);
            if (this.rotateAngleZ != 0.0f) {
                GL11.glRotatef((float)(this.rotateAngleZ * 57.295776f), (float)0.0f, (float)0.0f, (float)1.0f);
            }
            if (this.rotateAngleY != 0.0f) {
                GL11.glRotatef((float)(this.rotateAngleY * 57.295776f), (float)0.0f, (float)1.0f, (float)0.0f);
            }
            if (this.rotateAngleX != 0.0f) {
                GL11.glRotatef((float)(this.rotateAngleX * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
            }
            GL11.glScalef((float)this.scaleX, (float)this.scaleY, (float)this.scaleZ);
        }
    }
}

