package dev.emperor.module.modules.movement;

import dev.emperor.event.EventTarget;
import dev.emperor.event.misc.EventCollideWithBlock;
import dev.emperor.event.world.EventMotion;
import dev.emperor.event.world.EventPacketReceive;
import dev.emperor.event.world.EventWorldLoad;
import dev.emperor.gui.notification.NotificationManager;
import dev.emperor.gui.notification.NotificationType;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.values.BoolValue;
import dev.emperor.module.values.ModeValue;
import dev.emperor.module.values.NumberValue;
import dev.emperor.utils.BlinkUtils;
import dev.emperor.utils.DebugUtil;
import dev.emperor.utils.player.MoveUtil;
import java.awt.AWTException;
import java.awt.Robot;
import java.util.Timer;
import java.util.TimerTask;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;

public class AutoClip
extends Module {
    private final ModeValue<mode> modeValue = new ModeValue("Mode", mode.values(), mode.Tick);
    private final ModeValue<upmpdes> upMode = new ModeValue("UP Mode", upmpdes.values(), upmpdes.SetPosition);
    private final NumberValue high = new NumberValue("High", 2.0, 1.0, 20.0, 1.0);
    private final NumberValue flySpeed = new NumberValue("Fly Speed", 2.0, 1.0, 20.0, 1.0);
    private final NumberValue delay = new NumberValue("Delay", 50.0, 1.0, 2000.0, 1.0);
    private final BoolValue flyValue = new BoolValue("Fly", false);
    private final BoolValue reSize = new BoolValue("ReSize Window", false);
    private boolean teleporting = false;
    private boolean shouldFly = false;
    private int width;
    private int height;

    public AutoClip() {
        super("AutoClip", Category.Movement);
    }

    @Override
    public void onEnable() {
        this.teleporting = false;
        this.shouldFly = false;
    }

    @EventTarget
    public void onWorld(EventWorldLoad event) {
        this.shouldFly = false;
        this.width = AutoClip.mc.displayWidth;
        this.height = AutoClip.mc.displayHeight;
        try {
            Robot robot = new Robot();
            robot.keyPress(Keyboard.KEY_LMETA);
            robot.keyPress(Keyboard.KEY_UP);
            DebugUtil.log("release up");
            robot.keyRelease(Keyboard.KEY_UP);
            robot.keyPress(Keyboard.KEY_DOWN);
            DebugUtil.log("release down");
            robot.keyRelease(Keyboard.KEY_DOWN);
            DebugUtil.log("release m");
            robot.keyRelease(Keyboard.KEY_LMETA);
        }
        catch (AWTException e) {
            throw new RuntimeException(e);
        }
        this.teleporting = false;
    }

    @EventTarget(value=0)
    public void onMotion(EventMotion e) {
        if (e.isPre()) {
            if (this.modeValue.is("Tick") && AutoClip.mc.thePlayer.ticksExisted <= 2) {
                if (this.reSize.getValue().booleanValue()) {
                    AutoClip.mc.displayWidth = 800;
                    AutoClip.mc.displayHeight = 600;
                }
                BlockPos pos = new BlockPos(AutoClip.mc.thePlayer.posX, AutoClip.mc.thePlayer.posY + 2.0, AutoClip.mc.thePlayer.posZ);
                BlockPos higherPos = new BlockPos(AutoClip.mc.thePlayer.posX, AutoClip.mc.thePlayer.posY + 3.0, AutoClip.mc.thePlayer.posZ);
                if (AutoClip.mc.theWorld.getBlockState(pos).getBlock() == Blocks.glass || AutoClip.mc.theWorld.getBlockState(higherPos).getBlock() == Blocks.glass) {
                    this.teleporting = true;
                    this.shouldFly = true;
                    this.up();
                    NotificationManager.post(NotificationType.SUCCESS, "AutoClip", "Clip UP!", 5.0f);
                    this.teleporting = false;
                }
            }
            if (this.flyValue.getValue().booleanValue()) {
                if (AutoClip.mc.thePlayer.capabilities.allowFlying) {
                    if (this.shouldFly) {
                        double vanillaSpeed = this.flySpeed.getValue();
                        AutoClip.mc.thePlayer.motionY = 0.0;
                        AutoClip.mc.thePlayer.motionX = 0.0;
                        AutoClip.mc.thePlayer.motionZ = 0.0;
                        MoveUtil.setSpeed(vanillaSpeed);
                    }
                } else {
                    this.shouldFly = false;
                }
            }
        }
    }

    private void up() {
        BlinkUtils.setBlinkState(false, false, true, false, false, false, false, false, false, false, false);
        switch (this.upMode.get()) {
            case SetPosition: {
                AutoClip.mc.thePlayer.setPosition(AutoClip.mc.thePlayer.posX, AutoClip.mc.thePlayer.posY + this.high.getValue(), AutoClip.mc.thePlayer.posZ);
                break;
            }
            case SetPositionRotation: {
                AutoClip.mc.thePlayer.setPositionAndRotation(AutoClip.mc.thePlayer.posX, AutoClip.mc.thePlayer.posY + this.high.getValue(), AutoClip.mc.thePlayer.posZ, AutoClip.mc.thePlayer.rotationYaw, AutoClip.mc.thePlayer.rotationPitch);
                break;
            }
            case SetPositionRotation2: {
                AutoClip.mc.thePlayer.setPositionAndRotation2(AutoClip.mc.thePlayer.posX, AutoClip.mc.thePlayer.posY + this.high.getValue(), AutoClip.mc.thePlayer.posZ, AutoClip.mc.thePlayer.rotationYaw, AutoClip.mc.thePlayer.rotationPitch, 3, true);
            }
        }
        BlinkUtils.setBlinkState(true, true, false, false, false, false, false, false, false, false, false);
        BlinkUtils.clearPacket(null, false, -1);
    }

    @EventTarget
    public void onAABB(EventCollideWithBlock e) {
        if (this.teleporting) {
            e.setBoundingBox(null);
        }
    }

    @EventTarget(value=0)
    public void onPacketReceiveEvent(EventPacketReceive event) {
        String text;
        Packet<?> packet = event.getPacket();
        if (event.getPacket() instanceof S02PacketChat && (text = ((S02PacketChat)event.getPacket()).getChatComponent().getUnformattedText()).contains("\u5f00\u59cb\u5012\u8ba1\u65f6: 1 \u79d2")) {
            AutoClip.mc.displayWidth = this.width;
            AutoClip.mc.displayHeight = this.height;
        }
        if (event.getPacket() instanceof S45PacketTitle && this.modeValue.is("Delay")) {
            S45PacketTitle s45 = (S45PacketTitle)packet;
            if (s45.getMessage() == null) {
                return;
            }
            if (s45.getMessage().getUnformattedText().equals("\u00a7a\u6218\u6597\u5f00\u59cb...")) {
                if (this.reSize.getValue().booleanValue()) {
                    AutoClip.mc.displayWidth = 800;
                    AutoClip.mc.displayHeight = 600;
                }
                Timer timer = new Timer();
                TimerTask task = new TimerTask(){

                    @Override
                    public void run() {
                        AutoClip.this.teleporting = true;
                        AutoClip.this.up();
                        NotificationManager.post(NotificationType.SUCCESS, "AutoClip", "Clip UP!", 5.0f);
                        AutoClip.this.teleporting = false;
                    }
                };
                timer.schedule(task, this.delay.getValue().intValue());
            }
        }
    }

    public enum upmpdes {
        SetPosition,
        SetPositionRotation,
        SetPositionRotation2

    }

    public enum mode {
        Delay,
        Tick

    }
}

