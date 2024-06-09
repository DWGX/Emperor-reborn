package dev.robin.module.modules.combat;

import dev.robin.event.EventTarget;
import dev.robin.event.world.EventMotion;
import dev.robin.event.world.EventTick;
import dev.robin.module.Category;
import dev.robin.module.Module;
import dev.robin.module.values.BoolValue;
import dev.robin.module.values.NumberValue;
import dev.robin.utils.client.ReflectionUtil;
import dev.robin.utils.client.TimeUtil;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Mouse;

public class AutoClicker
extends Module {
    private final BoolValue left = new BoolValue("Left Clicker", true);
    private final BoolValue right = new BoolValue("Right Clicker", false);
    private final NumberValue maxCps = new NumberValue("Left MaxCPS", 10.0, 1.0, 20.0, 1.0);
    private final NumberValue minCps = new NumberValue("Left MinCPS", 10.0, 1.0, 20.0, 1.0);
    private final NumberValue RmaxCps = new NumberValue("Right MaxCPS", 14.0, 1.0, 20.0, 1.0);
    private final NumberValue RminCps = new NumberValue("Right MinCPS", 10.0, 1.0, 20.0, 1.0);
    public static final NumberValue jitter = new NumberValue("Jitter", 0.0, 0.0, 3.0, 0.1);
    private final BoolValue blockHit = new BoolValue("BlockHit", false);
    private final BoolValue autoUnBlock = new BoolValue("AutoUnblock", false);
    private final BoolValue weaponOnly = new BoolValue("Weapons Only", false);
    private final Random random = new Random();
    private final TimeUtil timeUtils = new TimeUtil();
    public static boolean b = false;
    public static double c = 0.0;

    public AutoClicker() {
        super("AutoClicker", Category.Combat);
    }

    @EventTarget
    public void Tick(EventTick event) {
        if ((Double)this.minCps.getValue() >= (Double)this.maxCps.getValue()) {
            this.maxCps.setValue(this.minCps.getValue());
        }
        if ((Double)this.maxCps.getValue() <= (Double)this.minCps.getValue()) {
            this.minCps.setValue(this.maxCps.getValue());
        }
        if ((Double)this.RminCps.getValue() >= (Double)this.RmaxCps.getValue()) {
            this.RmaxCps.setValue(this.RminCps.getValue());
        }
        if ((Double)this.RmaxCps.getValue() <= (Double)this.RminCps.getValue()) {
            this.RminCps.setValue(this.RmaxCps.getValue());
        }
    }

    public static void clickMouse() {
        int leftClickCounter = (Integer)ReflectionUtil.getFieldValue(Minecraft.getMinecraft(), "leftClickCounter", "field_71429_W");
        if (leftClickCounter <= 0) {
            Minecraft.getMinecraft().thePlayer.swingItem();
            if (Minecraft.getMinecraft().objectMouseOver == null) {
                if (Minecraft.getMinecraft().playerController.isNotCreative()) {
                    ReflectionUtil.setFieldValue(Minecraft.getMinecraft(), (Object)10, "leftClickCounter", "field_71429_W");
                }
            } else {
                switch (Minecraft.getMinecraft().objectMouseOver.typeOfHit) {
                    case ENTITY: {
                        try {
                            Minecraft.getMinecraft().playerController.attackEntity(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().objectMouseOver.entityHit);
                        }
                        catch (NullPointerException exception) {
                            exception.printStackTrace();
                        }
                        break;
                    }
                    case BLOCK: {
                        BlockPos blockpos = Minecraft.getMinecraft().objectMouseOver.getBlockPos();
                        try {
                            if (Minecraft.getMinecraft().theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
                                Minecraft.getMinecraft().playerController.clickBlock(blockpos, Minecraft.getMinecraft().objectMouseOver.sideHit);
                                break;
                            }
                        }
                        catch (NullPointerException ex) {
                            ex.printStackTrace();
                        }
                    }
                    default: {
                        if (!Minecraft.getMinecraft().playerController.isNotCreative()) break;
                        ReflectionUtil.setFieldValue(Minecraft.getMinecraft(), (Object)10, "leftClickCounter", "field_71429_W");
                    }
                }
            }
        }
    }

    @EventTarget
    public void onTick(EventTick e) {
        this.setSuffix(String.format("%s - %s", this.minCps.getValue(), this.maxCps.getValue()));
        if (!this.state) {
            return;
        }
        if (AutoClicker.mc.currentScreen == null && Mouse.isButtonDown((int)0)) {
            if (!((Boolean)this.left.getValue()).booleanValue()) {
                return;
            }
            if (((Boolean)this.weaponOnly.getValue()).booleanValue()) {
                if (AutoClicker.mc.thePlayer.getCurrentEquippedItem() == null) {
                    return;
                }
                if (!(AutoClicker.mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword) && !(AutoClicker.mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemAxe)) {
                    return;
                }
            }
            if (!((Boolean)this.blockHit.getValue()).booleanValue() && AutoClicker.mc.thePlayer.isUsingItem()) {
                return;
            }
            if (this.shouldAttack(Objects.equals(((Double)this.minCps.getValue()).intValue(), ((Double)this.maxCps.getValue()).intValue()) ? ((Double)this.maxCps.getValue()).intValue() : ThreadLocalRandom.current().nextInt(((Double)this.minCps.getValue()).intValue(), ((Double)this.maxCps.getValue()).intValue()))) {
                this.timeUtils.reset();
                ReflectionUtil.setFieldValue(Minecraft.getMinecraft(), (Object)0, "leftClickCounter", "field_71429_W");
                AutoClicker.clickMouse();
                if (((Boolean)this.autoUnBlock.getValue()).booleanValue() && Mouse.isButtonDown((int)1) && AutoClicker.mc.thePlayer.getHeldItem() != null && AutoClicker.mc.thePlayer.getHeldItem().getItem() != null && AutoClicker.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                    if (AutoClicker.mc.thePlayer.isBlocking()) {
                        KeyBinding.setKeyBindState(AutoClicker.mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                        AutoClicker.mc.playerController.onStoppedUsingItem(AutoClicker.mc.thePlayer);
                        AutoClicker.mc.thePlayer.setItemInUse(AutoClicker.mc.thePlayer.getItemInUse(), 0);
                    } else {
                        KeyBinding.setKeyBindState(AutoClicker.mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                        AutoClicker.mc.playerController.sendUseItem(AutoClicker.mc.thePlayer, AutoClicker.mc.theWorld, AutoClicker.mc.thePlayer.inventory.getCurrentItem(), false);
                    }
                }
            }
        }
        if (AutoClicker.mc.currentScreen == null && Mouse.isButtonDown((int)1)) {
            if (!((Boolean)this.right.getValue()).booleanValue()) {
                return;
            }
            if (this.shouldAttack(((Double)this.RminCps.getValue()).intValue() == ((Double)this.RmaxCps.getValue()).intValue() ? ((Double)this.RmaxCps.getValue()).intValue() : ThreadLocalRandom.current().nextInt(((Double)this.RminCps.getValue()).intValue(), ((Double)this.RmaxCps.getValue()).intValue() + 1))) {
                this.timeUtils.reset();
                try {
                    Field rightClickDelay = Minecraft.class.getDeclaredField("field_71467_ac");
                    rightClickDelay.setAccessible(true);
                    rightClickDelay.set(mc, 0);
                }
                catch (Exception d2) {
                    try {
                        Field ex = Minecraft.class.getDeclaredField("rightClickDelayTimer");
                        ex.setAccessible(true);
                        ex.set(mc, 0);
                    }
                    catch (Exception f) {
                        this.onDisable();
                    }
                }
            }
        }
    }

    @EventTarget
    public void onUpdate(EventMotion e) {
        if (!this.state) {
            return;
        }
        if (AutoClicker.mc.currentScreen == null && Mouse.isButtonDown((int)0) && (Double)jitter.getValue() > 0.0) {
            double a = (Double)jitter.getValue() * 0.45;
            boolean b2 = this.random.nextBoolean();
            Minecraft.getMinecraft().thePlayer.rotationYawHead += AutoClicker.mc.thePlayer.rotationYaw;
            Minecraft.getMinecraft().thePlayer.renderYawOffset += AutoClicker.mc.thePlayer.rotationYaw;
            e.setYaw(AutoClicker.mc.thePlayer.rotationYaw);
            Minecraft.getMinecraft().thePlayer.rotationPitchHead = AutoClicker.mc.thePlayer.rotationPitch;
            e.setPitch(AutoClicker.mc.thePlayer.rotationPitch);
            if (b2) {
                EntityPlayerSP thePlayer = AutoClicker.mc.thePlayer;
                c = (double)this.random.nextFloat() * a;
                Minecraft.getMinecraft().thePlayer.rotationYawHead += (float)c;
                Minecraft.getMinecraft().thePlayer.renderYawOffset += (float)c;
                e.setYaw(e.getYaw() + (float)c);
            } else {
                EntityPlayerSP thePlayer2 = AutoClicker.mc.thePlayer;
                c = (double)this.random.nextFloat() * a;
                Minecraft.getMinecraft().thePlayer.rotationYawHead -= (float)c;
                Minecraft.getMinecraft().thePlayer.renderYawOffset -= (float)c;
                e.setYaw(e.getYaw() - (float)c);
            }
            b2 = this.random.nextBoolean();
            if (b2) {
                EntityPlayerSP thePlayer3 = AutoClicker.mc.thePlayer;
                Minecraft.getMinecraft().thePlayer.rotationPitchHead += (float)((double)this.random.nextFloat() * (a * 0.45));
                e.setPitch(e.getPitch() + (float)((double)this.random.nextFloat() * (a * 0.45)));
            } else {
                EntityPlayerSP thePlayer4 = AutoClicker.mc.thePlayer;
                Minecraft.getMinecraft().thePlayer.rotationPitchHead -= (float)((double)this.random.nextFloat() * (a * 0.45));
                e.setPitch(e.getPitch() - (float)((double)this.random.nextFloat() * (a * 0.45)));
            }
        }
    }

    public boolean shouldAttack(int cps) {
        return this.timeUtils.hasReached(1000.0 / (double)cps);
    }

    public void reset() {
        this.timeUtils.reset();
    }
}

