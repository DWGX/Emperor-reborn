package net.minecraft.client.entity;

import dev.robin.Client;
import dev.robin.command.Command;
import dev.robin.event.EventManager;
import dev.robin.event.misc.EventChat;
import dev.robin.event.misc.EventDisplayChest;
import dev.robin.event.misc.EventSilentMove;
import dev.robin.event.world.EventMotion;
import dev.robin.event.world.EventMove;
import dev.robin.event.world.EventSlowDown;
import dev.robin.event.world.EventUpdate;
import dev.robin.module.modules.combat.Velocity;
import dev.robin.module.modules.combat.velocity.GrimVelocity;
import dev.robin.module.modules.misc.Disabler;
import dev.robin.module.modules.player.FastEat;
import dev.robin.utils.player.MoveUtil;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecartRiding;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.Vec3;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import net.vialoadingbase.ViaLoadingBase;
import org.lwjgl.compatibility.util.vector.Vector2f;

public class EntityPlayerSP
extends AbstractClientPlayer {
    public final NetHandlerPlayClient sendQueue;
    private final StatFileWriter statWriter;
    public int offGroundTicks;
    public int onGroundTicks;
    private double lastReportedPosX;
    private double lastReportedPosY;
    private double lastReportedPosZ;
    public float lastReportedYaw;
    public float lastReportedPitch;
    private boolean serverSneakState;
    public boolean serverSprintState;
    public int positionUpdateTicks;
    private boolean hasValidHealth;
    private String clientBrand;
    public MovementInput movementInput;
    protected Minecraft mc;
    protected int sprintToggleTimer;
    public int sprintingTicksLeft;
    public float renderArmYaw;
    public float renderArmPitch;
    public float prevRenderArmYaw;
    public float prevRenderArmPitch;
    private int horseJumpPowerCounter;
    private float horseJumpPower;
    public float timeInPortal;
    public float prevTimeInPortal;
    private Vec3 lastServerPosition;
    private Vec3 severPosition;
    public int rotIncrement;

    public EntityPlayerSP(Minecraft mcIn, World worldIn, NetHandlerPlayClient netHandler, StatFileWriter statFile) {
        super(worldIn, netHandler.getGameProfile());
        this.sendQueue = netHandler;
        this.statWriter = statFile;
        this.mc = mcIn;
        this.dimension = 0;
    }

    public MovingObjectPosition customRayTrace(double blockReachDistance, float partialTicks, float yaw, float pitch) {
        Vec3 vec3 = this.getPositionEyes(partialTicks);
        Vec3 vec4 = this.customGetLook(partialTicks, yaw, pitch);
        Vec3 vec5 = vec3.addVector(vec4.xCoord * blockReachDistance, vec4.yCoord * blockReachDistance, vec4.zCoord * blockReachDistance);
        return this.worldObj.rayTraceBlocks(vec3, vec5, false, false, true);
    }

    private Vec3 customGetLook(float partialTicks, float yaw, float pitch) {
        if (partialTicks == 1.0f || partialTicks == 2.0f) {
            return this.getVectorForRotation(pitch, yaw);
        }
        float f = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * partialTicks;
        float f2 = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * partialTicks;
        return this.getVectorForRotation(f, f2);
    }

    public int getItemInUseMaxCount() {
        return this.isUsingItem() ? this.getItemInUse().getMaxItemUseDuration() - this.getItemInUseCount() : 0;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    @Override
    public void heal(float healAmount) {
    }

    @Override
    public void mountEntity(Entity entityIn) {
        super.mountEntity(entityIn);
        if (entityIn instanceof EntityMinecart) {
            this.mc.getSoundHandler().playSound(new MovingSoundMinecartRiding(this, (EntityMinecart)entityIn));
        }
    }

    @Override
    public void onUpdate() {
        if (this.worldObj.isBlockLoaded(new BlockPos(this.posX, 0.0, this.posZ))) {
            EventManager.call(new EventUpdate());
            this.prevRenderPitchHead = this.renderPitchHead;
            this.renderPitchHead = this.rotationPitch;
            super.onUpdate();
            this.onUpdateWalkingPlayer();
        }
    }

    public Vector2f getPreviousRotation() {
        return new Vector2f(this.lastReportedYaw, this.lastReportedPitch);
    }

    public void onUpdateWalkingPlayer() {
        boolean flag1;
        if (this.onGround) {
            this.offGroundTicks = 0;
            ++this.onGroundTicks;
        } else {
            this.onGroundTicks = 0;
            ++this.offGroundTicks;
        }
        EventMotion PRE = new EventMotion(this.rotationYaw, this.rotationPitch, this.lastReportedYaw, this.lastReportedPitch, this.posX, this.posY, this.posZ, this.onGround);
        EventManager.call(PRE);
        boolean flag = this.isSprinting();
        if (flag != this.serverSprintState) {
            if (flag) {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.START_SPRINTING));
            } else {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.STOP_SPRINTING));
            }
            this.serverSprintState = flag;
        }
        if ((flag1 = this.isSneaking()) != this.serverSneakState) {
            if (flag1) {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.START_SNEAKING));
            } else {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.STOP_SNEAKING));
            }
            this.serverSneakState = flag1;
        }
        if (this.isCurrentViewEntity()) {
            boolean flag3;
            double x2 = PRE.getX();
            double y2 = PRE.getY();
            double z = PRE.getZ();
            float yaw = PRE.getYaw();
            float pitch = PRE.getPitch();
            boolean ground = PRE.isOnGround();
            double d0 = x2 - this.lastReportedPosX;
            double d1 = y2 - this.lastReportedPosY;
            double d2 = z - this.lastReportedPosZ;
            double d3 = yaw - this.lastReportedYaw;
            double d4 = pitch - this.lastReportedPitch;
            Velocity velocity = Client.instance.moduleManager.getModule(Velocity.class);
            FastEat fastEat = Client.instance.moduleManager.getModule(FastEat.class);
            if (ViaLoadingBase.getInstance().getTargetVersion().getVersion() > 47 || velocity.getState() && Velocity.grimModes.getValue() == GrimVelocity.velMode.GrimV_2_4_43 || fastEat.getState() && fastEat.modeValue.getValue() == FastEat.eatModes.Grim) {
                ++this.positionUpdateTicks;
            }
            boolean flag2 = d0 * d0 + d1 * d1 + d2 * d2 > 9.0E-4 || this.positionUpdateTicks >= 20;
            boolean bl = flag3 = d3 != 0.0 || d4 != 0.0;
            if (this.ridingEntity == null) {
                if (flag2 && flag3) {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(x2, y2, z, yaw, pitch, ground));
                } else if (flag2) {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x2, y2, z, ground));
                } else if (flag3) {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(yaw, pitch, ground));
                } else {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer(ground));
                }
                this.processPackets();
                if (velocity.getState() && Velocity.grimModes.getValue() != GrimVelocity.velMode.GrimV_2_4_43 || fastEat.getState() && fastEat.modeValue.getValue() != FastEat.eatModes.Grim) {
                    if (!(ViaLoadingBase.getInstance().getTargetVersion().getVersion() > 47 || GrimVelocity.shouldCancel && fastEat.grimEat)) {
                        ++this.positionUpdateTicks;
                    }
                } else if (ViaLoadingBase.getInstance().getTargetVersion().getVersion() <= 47) {
                    ++this.positionUpdateTicks;
                }
                if (flag2) {
                    this.lastReportedPosX = x2;
                    this.lastReportedPosY = y2;
                    this.lastReportedPosZ = z;
                    this.positionUpdateTicks = 0;
                }
                if (flag3) {
                    this.lastReportedYaw = yaw;
                    this.lastReportedPitch = pitch;
                }
                EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;
                --thePlayer.rotIncrement;
                EventMotion POST = new EventMotion(yaw, pitch);
                EventManager.call(POST);
            } else {
                this.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(PRE.getYaw(), PRE.getPitch(), PRE.isOnGround()));
                this.sendQueue.addToSendQueue(new C0CPacketInput(this.moveStrafing, this.moveForward, this.movementInput.jump, this.movementInput.sneak));
            }
        }
    }

    @Override
    public void moveEntity(double x2, double y2, double z) {
        EventMove e = new EventMove(x2, y2, z);
        EventManager.call(e);
        if (e.isCancelled()) {
            return;
        }
        super.moveEntity(e.getX(), e.getY(), e.getZ());
    }

    public void processPackets() {
        if (Disabler.getGrimPost()) {
            this.mc.lastTickSentC03 = true;
            while (!this.mc.scheduledTasks.isEmpty()) {
                try {
                    Util.runTask(this.mc.scheduledTasks.poll(), Minecraft.logger);
                }
                catch (ThreadQuickExitException threadQuickExitException) {}
            }
        }
    }

    public Vec3 getLastServerPosition() {
        return this.lastServerPosition;
    }

    public void setLastServerPosition(Vec3 lastServerPosition) {
        this.lastServerPosition = lastServerPosition;
    }

    public Vec3 getSeverPosition() {
        return this.severPosition;
    }

    public void setSeverPosition(Vec3 severPosition) {
        this.severPosition = severPosition;
    }

    public final boolean isMoving() {
        return this.mc.thePlayer.moveForward != 0.0f || this.mc.thePlayer.moveStrafing != 0.0f;
    }

    @Override
    public EntityItem dropOneItem(boolean dropAll) {
        C07PacketPlayerDigging.Action c07packetplayerdigging$action = dropAll ? C07PacketPlayerDigging.Action.DROP_ALL_ITEMS : C07PacketPlayerDigging.Action.DROP_ITEM;
        this.sendQueue.addToSendQueue(new C07PacketPlayerDigging(c07packetplayerdigging$action, BlockPos.ORIGIN, EnumFacing.DOWN));
        return null;
    }

    @Override
    protected void joinEntityItemWithWorld(EntityItem itemIn) {
    }

    public void sendChatMessage(String message) {
        EventChat event = new EventChat(message);
        EventManager.call(event);
        if (message.startsWith(Client.instance.commandPrefix)) {
            String[] args = message.trim().substring(1).split(" ");
            Command c = Client.instance.commandManager.getCommand(args[0]);
            if (c != null) {
                c.run(Arrays.copyOfRange(args, 1, args.length));
            } else {
                this.addChatMessage(new ChatComponentText((Object)((Object)EnumChatFormatting.RED) + "Unknown Command! Use .help to view usages."));
            }
        } else {
            this.sendQueue.addToSendQueue(new C01PacketChatMessage(message));
        }
    }

    @Override
    public void swingItem() {
        super.swingItem();
        this.sendQueue.addToSendQueue(new C0APacketAnimation());
    }

    @Override
    public void respawnPlayer() {
        this.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
    }

    @Override
    protected void damageEntity(DamageSource damageSrc, float damageAmount) {
        if (!this.isEntityInvulnerable(damageSrc)) {
            this.setHealth(this.getHealth() - damageAmount);
        }
    }

    @Override
    public void closeScreen() {
        this.sendQueue.addToSendQueue(new C0DPacketCloseWindow(this.openContainer.windowId));
        this.closeScreenAndDropStack();
    }

    public void closeScreen(GuiScreen current, int windowsID) {
        this.sendQueue.getNetworkManager().sendUnregisteredPacket(new C0DPacketCloseWindow(windowsID));
        this.closeScreenAndDropStack(current);
    }

    public void closeScreenAndDropStack() {
        this.inventory.setItemStack(null);
        super.closeScreen();
        this.mc.displayGuiScreen(null);
    }

    public void closeScreenAndDropStack(GuiScreen screen) {
        this.inventory.setItemStack(null);
        super.closeScreen();
        this.mc.displayGuiScreen(screen);
    }

    public void setPlayerSPHealth(float health) {
        if (this.hasValidHealth) {
            float f = this.getHealth() - health;
            if (f <= 0.0f) {
                this.setHealth(health);
                if (f < 0.0f) {
                    this.hurtResistantTime = this.maxHurtResistantTime / 2;
                }
            } else {
                this.lastDamage = f;
                this.setHealth(this.getHealth());
                this.hurtResistantTime = this.maxHurtResistantTime;
                this.damageEntity(DamageSource.generic, f);
                this.maxHurtTime = 10;
                this.hurtTime = 10;
            }
        } else {
            this.setHealth(health);
            this.hasValidHealth = true;
        }
    }

    @Override
    public void addStat(StatBase stat, int amount) {
        if (stat != null && stat.isIndependent) {
            super.addStat(stat, amount);
        }
    }

    @Override
    public void sendPlayerAbilities() {
        this.sendQueue.addToSendQueue(new C13PacketPlayerAbilities(this.capabilities));
    }

    @Override
    public boolean isUser() {
        return true;
    }

    protected void sendHorseJump() {
        this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.RIDING_JUMP, (int)(this.getHorseJumpPower() * 100.0f)));
    }

    public void sendHorseInventory() {
        this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.OPEN_INVENTORY));
    }

    public void setClientBrand(String brand) {
        this.clientBrand = brand;
    }

    public String getClientBrand() {
        return this.clientBrand;
    }

    public StatFileWriter getStatFileWriter() {
        return this.statWriter;
    }

    @Override
    public void addChatComponentMessage(IChatComponent chatComponent) {
        this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponent);
    }

    @Override
    protected boolean pushOutOfBlocks(double x2, double y2, double z) {
        if (this.noClip) {
            return false;
        }
        BlockPos blockpos = new BlockPos(x2, y2, z);
        double d0 = x2 - (double)blockpos.getX();
        double d1 = z - (double)blockpos.getZ();
        if (!this.isOpenBlockSpace(blockpos)) {
            int i = -1;
            double d2 = 9999.0;
            if (this.isOpenBlockSpace(blockpos.west()) && d0 < d2) {
                d2 = d0;
                i = 0;
            }
            if (this.isOpenBlockSpace(blockpos.east()) && 1.0 - d0 < d2) {
                d2 = 1.0 - d0;
                i = 1;
            }
            if (this.isOpenBlockSpace(blockpos.north()) && d1 < d2) {
                d2 = d1;
                i = 4;
            }
            if (this.isOpenBlockSpace(blockpos.south()) && 1.0 - d1 < d2) {
                d2 = 1.0 - d1;
                i = 5;
            }
            float f = 0.1f;
            if (i == 0) {
                this.motionX = -f;
            }
            if (i == 1) {
                this.motionX = f;
            }
            if (i == 4) {
                this.motionZ = -f;
            }
            if (i == 5) {
                this.motionZ = f;
            }
        }
        return false;
    }

    private boolean isOpenBlockSpace(BlockPos pos) {
        return !this.worldObj.getBlockState(pos).getBlock().isNormalCube() && !this.worldObj.getBlockState(pos.up()).getBlock().isNormalCube();
    }

    @Override
    public void setSprinting(boolean sprinting) {
        super.setSprinting(sprinting);
        this.sprintingTicksLeft = sprinting ? 600 : 0;
    }

    public void setXPStats(float currentXP, int maxXP, int level) {
        this.experience = currentXP;
        this.experienceTotal = maxXP;
        this.experienceLevel = level;
    }

    @Override
    public void addChatMessage(IChatComponent component) {
        this.mc.ingameGUI.getChatGUI().printChatMessage(component);
    }

    @Override
    public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
        return permLevel <= 0;
    }

    @Override
    public BlockPos getPosition() {
        return new BlockPos(this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5);
    }

    @Override
    public void playSound(String name, float volume, float pitch) {
        this.worldObj.playSound(this.posX, this.posY, this.posZ, name, volume, pitch, false);
    }

    @Override
    public boolean isServerWorld() {
        return true;
    }

    public boolean isRidingHorse() {
        return this.ridingEntity != null && this.ridingEntity instanceof EntityHorse && ((EntityHorse)this.ridingEntity).isHorseSaddled();
    }

    public float getHorseJumpPower() {
        return this.horseJumpPower;
    }

    @Override
    public void openEditSign(TileEntitySign signTile) {
        this.mc.displayGuiScreen(new GuiEditSign(signTile));
    }

    @Override
    public void openEditCommandBlock(CommandBlockLogic cmdBlockLogic) {
        this.mc.displayGuiScreen(new GuiCommandBlock(cmdBlockLogic));
    }

    @Override
    public void displayGUIBook(ItemStack bookStack) {
        Item item = bookStack.getItem();
        if (item == Items.writable_book) {
            this.mc.displayGuiScreen(new GuiScreenBook(this, bookStack, true));
        }
    }

    @Override
    public void displayGUIChest(IInventory chestInventory) {
        String s2;
        String string = s2 = chestInventory instanceof IInteractionObject ? ((IInteractionObject)((Object)chestInventory)).getGuiID() : "minecraft:container";
        if ("minecraft:chest".equals(s2)) {
            this.mc.displayGuiScreen(new GuiChest(this.inventory, chestInventory));
        } else if ("minecraft:hopper".equals(s2)) {
            this.mc.displayGuiScreen(new GuiHopper(this.inventory, chestInventory));
        } else if ("minecraft:furnace".equals(s2)) {
            this.mc.displayGuiScreen(new GuiFurnace(this.inventory, chestInventory));
        } else if ("minecraft:brewing_stand".equals(s2)) {
            this.mc.displayGuiScreen(new GuiBrewingStand(this.inventory, chestInventory));
        } else if ("minecraft:beacon".equals(s2)) {
            this.mc.displayGuiScreen(new GuiBeacon(this.inventory, chestInventory));
        } else if (!"minecraft:dispenser".equals(s2) && !"minecraft:dropper".equals(s2)) {
            this.mc.displayGuiScreen(new GuiChest(this.inventory, chestInventory));
        } else {
            this.mc.displayGuiScreen(new GuiDispenser(this.inventory, chestInventory));
        }
        EventManager.call(new EventDisplayChest(s2));
    }

    @Override
    public void displayGUIHorse(EntityHorse horse, IInventory horseInventory) {
        this.mc.displayGuiScreen(new GuiScreenHorseInventory(this.inventory, horseInventory, horse));
    }

    @Override
    public void displayGui(IInteractionObject guiOwner) {
        String s2 = guiOwner.getGuiID();
        if ("minecraft:crafting_table".equals(s2)) {
            this.mc.displayGuiScreen(new GuiCrafting(this.inventory, this.worldObj));
        } else if ("minecraft:enchanting_table".equals(s2)) {
            this.mc.displayGuiScreen(new GuiEnchantment(this.inventory, this.worldObj, guiOwner));
        } else if ("minecraft:anvil".equals(s2)) {
            this.mc.displayGuiScreen(new GuiRepair(this.inventory, this.worldObj));
        }
    }

    @Override
    public void displayVillagerTradeGui(IMerchant villager) {
        this.mc.displayGuiScreen(new GuiMerchant(this.inventory, villager, this.worldObj));
    }

    @Override
    public void onCriticalHit(Entity entityHit) {
        this.mc.effectRenderer.emitParticleAtEntity(entityHit, EnumParticleTypes.CRIT);
    }

    @Override
    public void onEnchantmentCritical(Entity entityHit) {
        this.mc.effectRenderer.emitParticleAtEntity(entityHit, EnumParticleTypes.CRIT_MAGIC);
    }

    @Override
    public boolean isSneaking() {
        boolean flag = this.movementInput != null ? this.movementInput.sneak : false;
        return flag && !this.sleeping;
    }

    @Override
    public void updateEntityActionState() {
        super.updateEntityActionState();
        if (this.isCurrentViewEntity()) {
            this.moveStrafing = this.movementInput.moveStrafe;
            this.moveForward = this.movementInput.moveForward;
            this.isJumping = this.movementInput.jump;
            this.prevRenderArmYaw = this.renderArmYaw;
            this.prevRenderArmPitch = this.renderArmPitch;
            this.renderArmPitch = (float)((double)this.renderArmPitch + (double)(this.rotationPitch - this.renderArmPitch) * 0.5);
            this.renderArmYaw = (float)((double)this.renderArmYaw + (double)(this.rotationYaw - this.renderArmYaw) * 0.5);
            this.rotationPitchHead = this.rotationPitch;
        }
    }

    protected boolean isCurrentViewEntity() {
        return this.mc.getRenderViewEntity() == this;
    }

    @Override
    public void onLivingUpdate() {
        boolean flag3;
        if (this.sprintingTicksLeft > 0) {
            --this.sprintingTicksLeft;
            if (this.sprintingTicksLeft == 0) {
                this.setSprinting(false);
            }
        }
        if (this.sprintToggleTimer > 0) {
            --this.sprintToggleTimer;
        }
        this.prevTimeInPortal = this.timeInPortal;
        if (this.inPortal) {
            if (this.mc.currentScreen != null && !this.mc.currentScreen.doesGuiPauseGame()) {
                this.mc.displayGuiScreen(null);
            }
            if (this.timeInPortal == 0.0f) {
                this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("portal.trigger"), this.rand.nextFloat() * 0.4f + 0.8f));
            }
            this.timeInPortal += 0.0125f;
            if (this.timeInPortal >= 1.0f) {
                this.timeInPortal = 1.0f;
            }
            this.inPortal = false;
        } else if (this.isPotionActive(Potion.confusion) && this.getActivePotionEffect(Potion.confusion).getDuration() > 60) {
            this.timeInPortal += 0.006666667f;
            if (this.timeInPortal > 1.0f) {
                this.timeInPortal = 1.0f;
            }
        } else {
            if (this.timeInPortal > 0.0f) {
                this.timeInPortal -= 0.05f;
            }
            if (this.timeInPortal < 0.0f) {
                this.timeInPortal = 0.0f;
            }
        }
        if (this.timeUntilPortal > 0) {
            --this.timeUntilPortal;
        }
        boolean flag = this.movementInput.jump;
        boolean flag1 = this.movementInput.sneak;
        float f = 0.8f;
        boolean flag2 = this.movementInput.moveForward >= f;
        this.movementInput.updatePlayerMoveState();
        EventSilentMove eventSilentMove = new EventSilentMove(Client.instance.yawPitchHelper.realYaw);
        EventManager.call(eventSilentMove);
        float forward = this.movementInput.moveForward;
        float strafe = this.movementInput.moveStrafe;
        if (eventSilentMove.isSilent()) {
            float[] floats = this.mySilentStrafe(this.movementInput.moveStrafe, this.movementInput.moveForward, eventSilentMove.getYaw(), eventSilentMove.isAdvanced());
            float diffForward = forward - floats[1];
            float diffStrafe = strafe - floats[0];
            if (this.movementInput.sneak) {
                this.movementInput.moveStrafe = MathHelper.clamp_float(floats[0], -0.3f, 0.3f);
                this.movementInput.moveForward = MathHelper.clamp_float(floats[1], -0.3f, 0.3f);
            } else {
                if (diffForward >= 2.0f) {
                    floats[1] = 0.0f;
                }
                if (diffForward <= -2.0f) {
                    floats[1] = 0.0f;
                }
                if (diffStrafe >= 2.0f) {
                    floats[0] = 0.0f;
                }
                if (diffStrafe <= -2.0f) {
                    floats[0] = 0.0f;
                }
                this.movementInput.moveStrafe = MathHelper.clamp_float(floats[0], -1.0f, 1.0f);
                this.movementInput.moveForward = MathHelper.clamp_float(floats[1], -1.0f, 1.0f);
            }
        }
        EventSlowDown slowdown = new EventSlowDown(EventSlowDown.Type.Item, 0.2f, 0.2f);
        EventManager.call(slowdown);
        if (this.isUsingItem() && !this.isRiding() && !slowdown.isCancelled()) {
            this.movementInput.moveStrafe *= slowdown.getStrafeMultiplier();
            this.movementInput.moveForward *= slowdown.getForwardMultiplier();
            this.sprintToggleTimer = 0;
        }
        this.pushOutOfBlocks(this.posX - (double)this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ + (double)this.width * 0.35);
        this.pushOutOfBlocks(this.posX - (double)this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ - (double)this.width * 0.35);
        this.pushOutOfBlocks(this.posX + (double)this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ - (double)this.width * 0.35);
        this.pushOutOfBlocks(this.posX + (double)this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ + (double)this.width * 0.35);
        boolean bl = flag3 = (float)this.getFoodStats().getFoodLevel() > 6.0f || this.capabilities.allowFlying;
        if (this.onGround && !flag1 && !flag2 && this.movementInput.moveForward >= f && !this.isSprinting() && flag3 && !this.isUsingItem() && !this.isPotionActive(Potion.blindness)) {
            if (this.sprintToggleTimer <= 0 && !this.mc.gameSettings.keyBindSprint.isKeyDown()) {
                this.sprintToggleTimer = 7;
            } else {
                this.setSprinting(true);
            }
        }
        if (!this.isSprinting() && this.movementInput.moveForward >= f && flag3 && !this.isUsingItem() && !this.isPotionActive(Potion.blindness) && this.mc.gameSettings.keyBindSprint.isKeyDown()) {
            this.setSprinting(true);
        }
        if (!GrimVelocity.velocityOverrideSprint && this.isSprinting() && (this.movementInput.moveForward < f || this.isCollidedHorizontally || !flag3)) {
            this.setSprinting(false);
        }
        GrimVelocity.velocityOverrideSprint = false;
        if (this.capabilities.allowFlying) {
            if (this.mc.playerController.isSpectatorMode()) {
                if (!this.capabilities.isFlying) {
                    this.capabilities.isFlying = true;
                    this.sendPlayerAbilities();
                }
            } else if (!flag && this.movementInput.jump) {
                if (this.flyToggleTimer == 0) {
                    this.flyToggleTimer = 7;
                } else {
                    this.capabilities.isFlying = !this.capabilities.isFlying;
                    this.sendPlayerAbilities();
                    this.flyToggleTimer = 0;
                }
            }
        }
        if (this.capabilities.isFlying && this.isCurrentViewEntity()) {
            if (this.movementInput.sneak) {
                this.motionY -= (double)(this.capabilities.getFlySpeed() * 3.0f);
            }
            if (this.movementInput.jump) {
                this.motionY += (double)(this.capabilities.getFlySpeed() * 3.0f);
            }
        }
        if (this.isRidingHorse()) {
            if (this.horseJumpPowerCounter < 0) {
                ++this.horseJumpPowerCounter;
                if (this.horseJumpPowerCounter == 0) {
                    this.horseJumpPower = 0.0f;
                }
            }
            if (flag && !this.movementInput.jump) {
                this.horseJumpPowerCounter = -10;
                this.sendHorseJump();
            } else if (!flag && this.movementInput.jump) {
                this.horseJumpPowerCounter = 0;
                this.horseJumpPower = 0.0f;
            } else if (flag) {
                ++this.horseJumpPowerCounter;
                this.horseJumpPower = this.horseJumpPowerCounter < 10 ? (float)this.horseJumpPowerCounter * 0.1f : 0.8f + 2.0f / (float)(this.horseJumpPowerCounter - 9) * 0.1f;
            }
        } else {
            this.horseJumpPower = 0.0f;
        }
        super.onLivingUpdate();
        if (this.onGround && this.capabilities.isFlying && !this.mc.playerController.isSpectatorMode()) {
            this.capabilities.isFlying = false;
            this.sendPlayerAbilities();
        }
    }

    public float[] mySilentStrafe(float strafe, float forward, float yaw, boolean advanced) {
        Minecraft mc = Minecraft.getMinecraft();
        float diff = MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw);
        float newForward = 0.0f;
        float newStrafe = 0.0f;
        if (!advanced) {
            if ((double)diff >= 22.5 && (double)diff < 67.5) {
                newStrafe += strafe;
                newForward += forward;
                newStrafe -= forward;
                newForward += strafe;
            } else if ((double)diff >= 67.5 && (double)diff < 112.5) {
                newStrafe -= forward;
                newForward += strafe;
            } else if ((double)diff >= 112.5 && (double)diff < 157.5) {
                newStrafe -= strafe;
                newForward -= forward;
                newStrafe -= forward;
                newForward += strafe;
            } else if ((double)diff >= 157.5 || (double)diff <= -157.5) {
                newStrafe -= strafe;
                newForward -= forward;
            } else if ((double)diff > -157.5 && (double)diff <= -112.5) {
                newStrafe -= strafe;
                newForward -= forward;
                newStrafe += forward;
                newForward -= strafe;
            } else if ((double)diff > -112.5 && (double)diff <= -67.5) {
                newStrafe += forward;
                newForward -= strafe;
            } else if ((double)diff > -67.5 && (double)diff <= -22.5) {
                newStrafe += strafe;
                newForward += forward;
                newStrafe += forward;
                newForward -= strafe;
            } else {
                newStrafe += strafe;
                newForward += forward;
            }
            return new float[]{newStrafe, newForward};
        }
        double[] realMotion = MoveUtil.getMotion(0.22, strafe, forward, Client.instance.getYawPitchHelper().realYaw);
        double[] array = new double[]{mc.thePlayer.posX, mc.thePlayer.posZ};
        double[] realPos = array;
        boolean n = false;
        array[0] = array[0] + realMotion[0];
        double[] array2 = realPos;
        boolean n2 = true;
        array2[1] = array2[1] + realMotion[1];
        ArrayList<float[]> possibleForwardStrafe = new ArrayList<float[]>();
        int i = 0;
        boolean b2 = false;
        while (!b2) {
            newForward = 0.0f;
            newStrafe = 0.0f;
            if (i == 0) {
                newStrafe += strafe;
                newForward += forward;
                possibleForwardStrafe.add(new float[]{newForward += strafe, newStrafe -= forward});
            } else if (i == 1) {
                possibleForwardStrafe.add(new float[]{newForward += strafe, newStrafe -= forward});
            } else if (i == 2) {
                newStrafe -= strafe;
                newForward -= forward;
                possibleForwardStrafe.add(new float[]{newForward += strafe, newStrafe -= forward});
            } else if (i == 3) {
                possibleForwardStrafe.add(new float[]{newForward -= forward, newStrafe -= strafe});
            } else if (i == 4) {
                newStrafe -= strafe;
                newForward -= forward;
                possibleForwardStrafe.add(new float[]{newForward -= strafe, newStrafe += forward});
            } else if (i == 5) {
                possibleForwardStrafe.add(new float[]{newForward -= strafe, newStrafe += forward});
            } else if (i == 6) {
                newStrafe += strafe;
                newForward += forward;
                possibleForwardStrafe.add(new float[]{newForward -= strafe, newStrafe += forward});
            } else {
                possibleForwardStrafe.add(new float[]{newForward += forward, newStrafe += strafe});
                b2 = true;
            }
            ++i;
        }
        double distance = 5000.0;
        float[] floats = new float[2];
        for (float[] flo : possibleForwardStrafe) {
            double diffZ;
            double[] motion2;
            if (flo[0] > 1.0f) {
                flo[0] = 1.0f;
            } else if (flo[0] < -1.0f) {
                flo[0] = -1.0f;
            }
            if (flo[1] > 1.0f) {
                flo[1] = 1.0f;
            } else if (flo[1] < -1.0f) {
                flo[1] = -1.0f;
            }
            double[] motion = motion2 = MoveUtil.getMotion(0.22, flo[1], flo[0], this.rotationYaw);
            boolean n3 = false;
            motion2[0] = motion2[0] + mc.thePlayer.posX;
            double[] array3 = motion;
            boolean n4 = true;
            array3[1] = array3[1] + mc.thePlayer.posZ;
            double diffX = Math.abs(realPos[0] - motion[0]);
            double d0 = diffX * diffX + (diffZ = Math.abs(realPos[1] - motion[1])) * diffZ;
            if (!(d0 < distance)) continue;
            distance = d0;
            floats = flo;
        }
        return new float[]{floats[1], floats[0]};
    }
}

