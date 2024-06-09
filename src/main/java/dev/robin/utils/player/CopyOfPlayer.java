package dev.robin.utils.player;

import net.minecraft.entity.player.EntityPlayer;

public class CopyOfPlayer {
   private EntityPlayer player;
   private StaticModelPlayer model;
   private long time;
   private double x;
   private double y;
   private double z;

   public CopyOfPlayer(EntityPlayer player, long time, double x, double y, double z, boolean slim) {
      this.player = player;
      this.time = time;
      this.x = x;
      this.y = y - (player.isSneaking() ? 0.125D : 0.0D);
      this.z = z;
      this.model = new StaticModelPlayer(player, slim, 0.0F);
      this.model.disableArmorLayers();
   }

   public EntityPlayer getPlayer() {
      return this.player;
   }

   public StaticModelPlayer getModel() {
      return this.model;
   }

   public long getTime() {
      return this.time;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public void setPlayer(EntityPlayer player) {
      this.player = player;
   }

   public void setModel(StaticModelPlayer model) {
      this.model = model;
   }

   public void setTime(long time) {
      this.time = time;
   }

   public void setX(double x) {
      this.x = x;
   }

   public void setY(double y) {
      this.y = y;
   }

   public void setZ(double z) {
      this.z = z;
   }

   public CopyOfPlayer(EntityPlayer player, StaticModelPlayer model, long time, double x, double y, double z) {
      this.player = player;
      this.model = model;
      this.time = time;
      this.x = x;
      this.y = y;
      this.z = z;
   }
}
