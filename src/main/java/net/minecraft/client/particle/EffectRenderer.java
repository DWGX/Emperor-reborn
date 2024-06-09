package net.minecraft.client.particle;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Barrier;
import net.minecraft.client.particle.EntityAuraFX;
import net.minecraft.client.particle.EntityBlockDustFX;
import net.minecraft.client.particle.EntityBreakingFX;
import net.minecraft.client.particle.EntityBubbleFX;
import net.minecraft.client.particle.EntityCloudFX;
import net.minecraft.client.particle.EntityCrit2FX;
import net.minecraft.client.particle.EntityCritFX;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityDropParticleFX;
import net.minecraft.client.particle.EntityEnchantmentTableParticleFX;
import net.minecraft.client.particle.EntityExplodeFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFirework;
import net.minecraft.client.particle.EntityFishWakeFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.particle.EntityFootStepFX;
import net.minecraft.client.particle.EntityHeartFX;
import net.minecraft.client.particle.EntityHugeExplodeFX;
import net.minecraft.client.particle.EntityLargeExplodeFX;
import net.minecraft.client.particle.EntityLavaFX;
import net.minecraft.client.particle.EntityNoteFX;
import net.minecraft.client.particle.EntityParticleEmitter;
import net.minecraft.client.particle.EntityPortalFX;
import net.minecraft.client.particle.EntityRainFX;
import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.particle.EntitySnowShovelFX;
import net.minecraft.client.particle.EntitySpellParticleFX;
import net.minecraft.client.particle.EntitySplashFX;
import net.minecraft.client.particle.EntitySuspendFX;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.MobAppearance;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EffectRenderer {
    private static final ResourceLocation particleTextures = new ResourceLocation("textures/particle/particles.png");
    protected World worldObj;
    private List<EntityFX>[][] fxLayers = new List[4][];
    private List<EntityParticleEmitter> particleEmitters = Lists.newArrayList();
    private TextureManager renderer;
    private Random rand = new Random();
    private Map<Integer, IParticleFactory> particleTypes = Maps.newHashMap();

    public EffectRenderer(World worldIn, TextureManager rendererIn) {
        this.worldObj = worldIn;
        this.renderer = rendererIn;
        for (int i = 0; i < 4; ++i) {
            this.fxLayers[i] = new List[2];
            for (int j2 = 0; j2 < 2; ++j2) {
                this.fxLayers[i][j2] = Lists.newArrayList();
            }
        }
        this.registerVanillaParticles();
    }

    private void registerVanillaParticles() {
        this.registerParticle(EnumParticleTypes.EXPLOSION_NORMAL.getParticleID(), new EntityExplodeFX.Factory());
        this.registerParticle(EnumParticleTypes.WATER_BUBBLE.getParticleID(), new EntityBubbleFX.Factory());
        this.registerParticle(EnumParticleTypes.WATER_SPLASH.getParticleID(), new EntitySplashFX.Factory());
        this.registerParticle(EnumParticleTypes.WATER_WAKE.getParticleID(), new EntityFishWakeFX.Factory());
        this.registerParticle(EnumParticleTypes.WATER_DROP.getParticleID(), new EntityRainFX.Factory());
        this.registerParticle(EnumParticleTypes.SUSPENDED.getParticleID(), new EntitySuspendFX.Factory());
        this.registerParticle(EnumParticleTypes.SUSPENDED_DEPTH.getParticleID(), new EntityAuraFX.Factory());
        this.registerParticle(EnumParticleTypes.CRIT.getParticleID(), new EntityCrit2FX.Factory());
        this.registerParticle(EnumParticleTypes.CRIT_MAGIC.getParticleID(), new EntityCrit2FX.MagicFactory());
        this.registerParticle(EnumParticleTypes.SMOKE_NORMAL.getParticleID(), new EntitySmokeFX.Factory());
        this.registerParticle(EnumParticleTypes.SMOKE_LARGE.getParticleID(), new EntityCritFX.Factory());
        this.registerParticle(EnumParticleTypes.SPELL.getParticleID(), new EntitySpellParticleFX.Factory());
        this.registerParticle(EnumParticleTypes.SPELL_INSTANT.getParticleID(), new EntitySpellParticleFX.InstantFactory());
        this.registerParticle(EnumParticleTypes.SPELL_MOB.getParticleID(), new EntitySpellParticleFX.MobFactory());
        this.registerParticle(EnumParticleTypes.SPELL_MOB_AMBIENT.getParticleID(), new EntitySpellParticleFX.AmbientMobFactory());
        this.registerParticle(EnumParticleTypes.SPELL_WITCH.getParticleID(), new EntitySpellParticleFX.WitchFactory());
        this.registerParticle(EnumParticleTypes.DRIP_WATER.getParticleID(), new EntityDropParticleFX.WaterFactory());
        this.registerParticle(EnumParticleTypes.DRIP_LAVA.getParticleID(), new EntityDropParticleFX.LavaFactory());
        this.registerParticle(EnumParticleTypes.VILLAGER_ANGRY.getParticleID(), new EntityHeartFX.AngryVillagerFactory());
        this.registerParticle(EnumParticleTypes.VILLAGER_HAPPY.getParticleID(), new EntityAuraFX.HappyVillagerFactory());
        this.registerParticle(EnumParticleTypes.TOWN_AURA.getParticleID(), new EntityAuraFX.Factory());
        this.registerParticle(EnumParticleTypes.NOTE.getParticleID(), new EntityNoteFX.Factory());
        this.registerParticle(EnumParticleTypes.PORTAL.getParticleID(), new EntityPortalFX.Factory());
        this.registerParticle(EnumParticleTypes.ENCHANTMENT_TABLE.getParticleID(), new EntityEnchantmentTableParticleFX.EnchantmentTable());
        this.registerParticle(EnumParticleTypes.FLAME.getParticleID(), new EntityFlameFX.Factory());
        this.registerParticle(EnumParticleTypes.LAVA.getParticleID(), new EntityLavaFX.Factory());
        this.registerParticle(EnumParticleTypes.FOOTSTEP.getParticleID(), new EntityFootStepFX.Factory());
        this.registerParticle(EnumParticleTypes.CLOUD.getParticleID(), new EntityCloudFX.Factory());
        this.registerParticle(EnumParticleTypes.REDSTONE.getParticleID(), new EntityReddustFX.Factory());
        this.registerParticle(EnumParticleTypes.SNOWBALL.getParticleID(), new EntityBreakingFX.SnowballFactory());
        this.registerParticle(EnumParticleTypes.SNOW_SHOVEL.getParticleID(), new EntitySnowShovelFX.Factory());
        this.registerParticle(EnumParticleTypes.SLIME.getParticleID(), new EntityBreakingFX.SlimeFactory());
        this.registerParticle(EnumParticleTypes.HEART.getParticleID(), new EntityHeartFX.Factory());
        this.registerParticle(EnumParticleTypes.BARRIER.getParticleID(), new Barrier.Factory());
        this.registerParticle(EnumParticleTypes.ITEM_CRACK.getParticleID(), new EntityBreakingFX.Factory());
        this.registerParticle(EnumParticleTypes.BLOCK_CRACK.getParticleID(), new EntityDiggingFX.Factory());
        this.registerParticle(EnumParticleTypes.BLOCK_DUST.getParticleID(), new EntityBlockDustFX.Factory());
        this.registerParticle(EnumParticleTypes.EXPLOSION_HUGE.getParticleID(), new EntityHugeExplodeFX.Factory());
        this.registerParticle(EnumParticleTypes.EXPLOSION_LARGE.getParticleID(), new EntityLargeExplodeFX.Factory());
        this.registerParticle(EnumParticleTypes.FIREWORKS_SPARK.getParticleID(), new EntityFirework.Factory());
        this.registerParticle(EnumParticleTypes.MOB_APPEARANCE.getParticleID(), new MobAppearance.Factory());
    }

    public void registerParticle(int id, IParticleFactory particleFactory) {
        this.particleTypes.put(id, particleFactory);
    }

    public void emitParticleAtEntity(Entity entityIn, EnumParticleTypes particleTypes) {
        this.particleEmitters.add(new EntityParticleEmitter(this.worldObj, entityIn, particleTypes));
    }

    public EntityFX spawnEffectParticle(int particleId, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int ... parameters) {
        EntityFX entityfx;
        IParticleFactory iparticlefactory = this.particleTypes.get(particleId);
        if (iparticlefactory != null && (entityfx = iparticlefactory.getEntityFX(particleId, this.worldObj, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters)) != null) {
            this.addEffect(entityfx);
            return entityfx;
        }
        return null;
    }

    public void addEffect(EntityFX effect) {
        if (effect != null && (!(effect instanceof EntityFirework.SparkFX) || Config.isFireworkParticles())) {
            int j2;
            int i = effect.getFXLayer();
            int n = j2 = effect.getAlpha() != 1.0f ? 0 : 1;
            if (this.fxLayers[i][j2].size() >= 4000) {
                this.fxLayers[i][j2].remove(0);
            }
            this.fxLayers[i][j2].add(effect);
        }
    }

    public void updateEffects() {
        for (int i = 0; i < 4; ++i) {
            this.updateEffectLayer(i);
        }
        ArrayList list = Lists.newArrayList();
        for (EntityParticleEmitter entityparticleemitter : this.particleEmitters) {
            entityparticleemitter.onUpdate();
            if (!entityparticleemitter.isDead) continue;
            list.add(entityparticleemitter);
        }
        this.particleEmitters.removeAll(list);
    }

    private void updateEffectLayer(int layer) {
        this.updateEffectAlphaLayer(this.fxLayers[layer][0]);
        this.updateEffectAlphaLayer(this.fxLayers[layer][1]);
    }

    private void updateEffectAlphaLayer(List<EntityFX> entitiesFX) {
        ArrayList list = Lists.newArrayList();
        long i = System.currentTimeMillis();
        int j2 = entitiesFX.size();
        for (int k2 = 0; k2 < entitiesFX.size(); ++k2) {
            EntityFX entityfx = entitiesFX.get(k2);
            this.tickParticle(entityfx);
            if (entityfx.isDead) {
                list.add(entityfx);
            }
            --j2;
            if (System.currentTimeMillis() > i + 20L) break;
        }
        if (j2 > 0) {
            Iterator<EntityFX> iterator = entitiesFX.iterator();
            for (int l2 = j2; iterator.hasNext() && l2 > 0; --l2) {
                EntityFX entityfx1 = iterator.next();
                entityfx1.setDead();
                iterator.remove();
            }
        }
        entitiesFX.removeAll(list);
    }

    private void tickParticle(final EntityFX particle) {
        try {
            particle.onUpdate();
        }
        catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Ticking Particle");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being ticked");
            final int i = particle.getFXLayer();
            crashreportcategory.addCrashSectionCallable("Particle", new Callable<String>(){

                @Override
                public String call() throws Exception {
                    return particle.toString();
                }
            });
            crashreportcategory.addCrashSectionCallable("Particle Type", new Callable<String>(){

                @Override
                public String call() throws Exception {
                    return i == 0 ? "MISC_TEXTURE" : (i == 1 ? "TERRAIN_TEXTURE" : (i == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + i));
                }
            });
            throw new ReportedException(crashreport);
        }
    }

    public void renderParticles(Entity entityIn, float partialTicks) {
        float f = ActiveRenderInfo.getRotationX();
        float f1 = ActiveRenderInfo.getRotationZ();
        float f2 = ActiveRenderInfo.getRotationYZ();
        float f3 = ActiveRenderInfo.getRotationXY();
        float f4 = ActiveRenderInfo.getRotationXZ();
        EntityFX.interpPosX = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double)partialTicks;
        EntityFX.interpPosY = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double)partialTicks;
        EntityFX.interpPosZ = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double)partialTicks;
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.alphaFunc(516, 0.003921569f);
        Block block = ActiveRenderInfo.getBlockAtEntityViewpoint(this.worldObj, entityIn, partialTicks);
        boolean flag = block.getMaterial() == Material.water;
        for (int i = 0; i < 3; ++i) {
            for (int j2 = 0; j2 < 2; ++j2) {
                final int i_f = i;
                if (this.fxLayers[i][j2].isEmpty()) continue;
                switch (j2) {
                    case 0: {
                        GlStateManager.depthMask(false);
                        break;
                    }
                    case 1: {
                        GlStateManager.depthMask(true);
                    }
                }
                switch (i) {
                    default: {
                        this.renderer.bindTexture(particleTextures);
                        break;
                    }
                    case 1: {
                        this.renderer.bindTexture(TextureMap.locationBlocksTexture);
                    }
                }
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                Tessellator tessellator = Tessellator.getInstance();
                WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                worldrenderer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                for (int k2 = 0; k2 < this.fxLayers[i][j2].size(); ++k2) {
                    final EntityFX entityfx = this.fxLayers[i][j2].get(k2);
                    try {
                        if (!flag && entityfx instanceof EntitySuspendFX) continue;
                        entityfx.renderParticle(worldrenderer, entityIn, partialTicks, f, f4, f1, f2, f3);
                        continue;
                    }
                    catch (Throwable throwable) {
                        CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering Particle");
                        CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being rendered");
                        crashreportcategory.addCrashSectionCallable("Particle", new Callable<String>(){

                            @Override
                            public String call() throws Exception {
                                return entityfx.toString();
                            }
                        });
                        crashreportcategory.addCrashSectionCallable("Particle Type", new Callable<String>(){

                            @Override
                            public String call() throws Exception {
                                return i_f == 0 ? "MISC_TEXTURE" : (i_f == 1 ? "TERRAIN_TEXTURE" : (i_f == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + i_f));
                            }
                        });
                        throw new ReportedException(crashreport);
                    }
                }
                tessellator.draw();
            }
        }
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1f);
    }

    public void renderLitParticles(Entity entityIn, float partialTick) {
        float f = (float)Math.PI / 180;
        float f1 = MathHelper.cos(entityIn.rotationYaw * ((float)Math.PI / 180));
        float f2 = MathHelper.sin(entityIn.rotationYaw * ((float)Math.PI / 180));
        float f3 = -f2 * MathHelper.sin(entityIn.rotationPitch * ((float)Math.PI / 180));
        float f4 = f1 * MathHelper.sin(entityIn.rotationPitch * ((float)Math.PI / 180));
        float f5 = MathHelper.cos(entityIn.rotationPitch * ((float)Math.PI / 180));
        for (int i = 0; i < 2; ++i) {
            List<EntityFX> list = this.fxLayers[3][i];
            if (list.isEmpty()) continue;
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            for (int j2 = 0; j2 < list.size(); ++j2) {
                EntityFX entityfx = list.get(j2);
                entityfx.renderParticle(worldrenderer, entityIn, partialTick, f1, f5, f2, f3, f4);
            }
        }
    }

    public void clearEffects(World worldIn) {
        this.worldObj = worldIn;
        for (int i = 0; i < 4; ++i) {
            for (int j2 = 0; j2 < 2; ++j2) {
                this.fxLayers[i][j2].clear();
            }
        }
        this.particleEmitters.clear();
    }

    public void addBlockDestroyEffects(BlockPos pos, IBlockState state) {
        boolean flag;
        boolean bl = flag = state.getBlock().getMaterial() != Material.air;
        if (flag) {
            state = state.getBlock().getActualState(state, this.worldObj, pos);
            int l2 = 4;
            for (int i = 0; i < l2; ++i) {
                for (int j2 = 0; j2 < l2; ++j2) {
                    for (int k2 = 0; k2 < l2; ++k2) {
                        double d0 = (double)pos.getX() + ((double)i + 0.5) / (double)l2;
                        double d1 = (double)pos.getY() + ((double)j2 + 0.5) / (double)l2;
                        double d2 = (double)pos.getZ() + ((double)k2 + 0.5) / (double)l2;
                        this.addEffect(new EntityDiggingFX(this.worldObj, d0, d1, d2, d0 - (double)pos.getX() - 0.5, d1 - (double)pos.getY() - 0.5, d2 - (double)pos.getZ() - 0.5, state).setBlockPos(pos));
                    }
                }
            }
        }
    }

    public void addBlockHitEffects(BlockPos pos, EnumFacing side) {
        IBlockState iblockstate = this.worldObj.getBlockState(pos);
        Block block = iblockstate.getBlock();
        if (block.getRenderType() != -1) {
            int i = pos.getX();
            int j2 = pos.getY();
            int k2 = pos.getZ();
            float f = 0.1f;
            double d0 = (double)i + this.rand.nextDouble() * (block.getBlockBoundsMaxX() - block.getBlockBoundsMinX() - (double)(f * 2.0f)) + (double)f + block.getBlockBoundsMinX();
            double d1 = (double)j2 + this.rand.nextDouble() * (block.getBlockBoundsMaxY() - block.getBlockBoundsMinY() - (double)(f * 2.0f)) + (double)f + block.getBlockBoundsMinY();
            double d2 = (double)k2 + this.rand.nextDouble() * (block.getBlockBoundsMaxZ() - block.getBlockBoundsMinZ() - (double)(f * 2.0f)) + (double)f + block.getBlockBoundsMinZ();
            switch (side) {
                case DOWN: {
                    d1 = (double)j2 + block.getBlockBoundsMinY() - (double)f;
                    break;
                }
                case UP: {
                    d1 = (double)j2 + block.getBlockBoundsMaxY() + (double)f;
                    break;
                }
                case NORTH: {
                    d2 = (double)k2 + block.getBlockBoundsMinZ() - (double)f;
                    break;
                }
                case SOUTH: {
                    d2 = (double)k2 + block.getBlockBoundsMaxZ() + (double)f;
                    break;
                }
                case WEST: {
                    d0 = (double)i + block.getBlockBoundsMinX() - (double)f;
                    break;
                }
                case EAST: {
                    d0 = (double)i + block.getBlockBoundsMaxX() + (double)f;
                }
            }
            this.addEffect(new EntityDiggingFX(this.worldObj, d0, d1, d2, 0.0, 0.0, 0.0, iblockstate).setBlockPos(pos).multiplyVelocity(0.2f).multipleParticleScaleBy(0.6f));
        }
    }

    public void moveToAlphaLayer(EntityFX effect) {
        this.moveToLayer(effect, 1, 0);
    }

    public void moveToNoAlphaLayer(EntityFX effect) {
        this.moveToLayer(effect, 0, 1);
    }

    private void moveToLayer(EntityFX effect, int layerFrom, int layerTo) {
        for (int i = 0; i < 4; ++i) {
            if (!this.fxLayers[i][layerFrom].contains(effect)) continue;
            this.fxLayers[i][layerFrom].remove(effect);
            this.fxLayers[i][layerTo].add(effect);
        }
    }

    public String getStatistics() {
        int i = 0;
        for (int j2 = 0; j2 < 4; ++j2) {
            for (int k2 = 0; k2 < 2; ++k2) {
                i += this.fxLayers[j2][k2].size();
            }
        }
        return new StringBuilder(i).toString();
    }

    public void addBlockHitEffects(BlockPos pos, MovingObjectPosition mop) {
        if (this.worldObj.getBlockState(pos) != null) {
            this.addBlockHitEffects(pos, mop.sideHit);
        }
    }
}

