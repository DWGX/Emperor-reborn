package dev.robin.utils.player;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.player.EntityPlayer;

import static dev.robin.utils.player.RotationUtil.mc;

public class StaticModelPlayer extends ModelPlayer{
   private final EntityPlayer player;
   private float limbSwing;
   private float limbSwingAmount;
   private float yaw;
   private float yawHead;
   private float pitch;

   public StaticModelPlayer(EntityPlayer playerIn, boolean smallArms, float modelSize) {
      super(modelSize, smallArms);
      this.player = playerIn;
      this.limbSwing = this.player.limbSwing;
      this.limbSwingAmount = this.player.limbSwingAmount;
      this.yaw = this.player.rotationYaw;
      this.yawHead = this.player.rotationYawHead;
      this.pitch = this.player.rotationPitch;
      this.isSneak = this.player.isSneaking();
      this.swingProgress = this.player.swingProgress;
      this.setLivingAnimations(this.player, this.limbSwing, this.limbSwingAmount, mc.timer.getRenderPartialTicks());
   }

   public void render(float scale) {
      this.render(this.player, this.limbSwing, this.limbSwingAmount, (float)this.player.ticksExisted, this.yawHead, this.pitch, scale);
   }

   public void disableArmorLayers() {
      this.bipedBodyWear.showModel = false;
      this.bipedLeftLegwear.showModel = false;
      this.bipedRightLegwear.showModel = false;
      this.bipedLeftArmwear.showModel = false;
      this.bipedRightArmwear.showModel = false;
      this.bipedHeadwear.showModel = true;
      this.bipedHead.showModel = false;
   }

   public void setLimbSwing(float limbSwing) {
      this.limbSwing = limbSwing;
   }

   public void setLimbSwingAmount(float limbSwingAmount) {
      this.limbSwingAmount = limbSwingAmount;
   }

   public void setYaw(float yaw) {
      this.yaw = yaw;
   }

   public void setYawHead(float yawHead) {
      this.yawHead = yawHead;
   }

   public void setPitch(float pitch) {
      this.pitch = pitch;
   }

   public EntityPlayer getPlayer() {
      return this.player;
   }

   public float getLimbSwing() {
      return this.limbSwing;
   }

   public float getLimbSwingAmount() {
      return this.limbSwingAmount;
   }

   public float getYaw() {
      return this.yaw;
   }

   public float getYawHead() {
      return this.yawHead;
   }

   public float getPitch() {
      return this.pitch;
   }
}
