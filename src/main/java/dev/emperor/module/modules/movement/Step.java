package dev.emperor.module.modules.movement;

import dev.emperor.event.EventTarget;
import dev.emperor.event.world.EventMotion;
import dev.emperor.event.world.EventStep;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.values.BoolValue;
import dev.emperor.module.values.ModeValue;
import dev.emperor.module.values.NumberValue;
import dev.emperor.utils.client.PacketUtil;
import dev.emperor.utils.player.MoveUtil;
import dev.emperor.utils.player.PlayerUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;

public class Step
extends Module {
    private final ModeValue<stepMode> mode = new ModeValue("Mode", stepMode.values(), stepMode.Vanilla);
    private final NumberValue height = new NumberValue("Height", 1.0, 1.0, 2.5, 0.1);
    private final NumberValue timer = new NumberValue("Timer", 0.5, 0.1, 1.0, 0.1);
    private final BoolValue reverse = new BoolValue("Reverse", false);
    private final BoolValue hypixelSmooth = new BoolValue("HypixelSmooth", false);
    private final BoolValue twoBlockValue = new BoolValue("Matrix-2Block", true);
    private final BoolValue instantValue = new BoolValue("Matrix-Instant", true);
    private int ticks;
    private boolean doJump;
    private boolean step;
    private int onGroundTicks;
    private int offGroundTicks;

    public Step() {
        super("Step", Category.Movement);
    }

    @Override
    public void onDisable() {
        if (this.mode.getValue() != stepMode.Jump) {
            Step.mc.thePlayer.stepHeight = 0.6f;
        }
    }

    @EventTarget
    public void onMotion(EventMotion event) {
        if (event.isPre()) {
            switch (this.mode.getValue()) {
                case Jump: {
                    if (!Step.mc.thePlayer.onGround || !Step.mc.thePlayer.isCollidedHorizontally) break;
                    Step.mc.thePlayer.jump();
                    break;
                }
                case Matrix: {
                    float f = Step.mc.thePlayer.stepHeight = this.twoBlockValue.getValue() ? 2.0f : 1.0f;
                    if (!this.doJump) break;
                    if (this.ticks > 0 && Step.mc.thePlayer.onGround || this.ticks > 5) {
                        this.ticks = 0;
                        this.doJump = false;
                        return;
                    }
                    if (this.ticks % 3 == 0) {
                        event.setOnGround(true);
                        Step.mc.thePlayer.jump();
                    }
                    ++this.ticks;
                    break;
                }
                case NCP_PacketLess: {
                    if (Step.mc.thePlayer.onGround && Step.mc.thePlayer.isCollidedHorizontally && !Step.mc.thePlayer.isPotionActive(Potion.jump)) {
                        Step.mc.thePlayer.jump();
                        MoveUtil.stop();
                        this.step = true;
                    }
                    if (Step.mc.thePlayer.offGroundTicks != 3 || !this.step) break;
                    Step.mc.thePlayer.motionY = MoveUtil.predictedMotion(Step.mc.thePlayer.motionY, 2);
                    MoveUtil.strafe(0.221);
                    this.step = false;
                    break;
                }
                case NCP: {
                    Step.mc.thePlayer.stepHeight = Step.mc.thePlayer.onGround && !PlayerUtil.isInLiquid() ? this.height.getValue().floatValue() : 0.6f;
                    if (!this.reverse.getValue().booleanValue() || PlayerUtil.blockRelativeToPlayer(0.0, -(this.height.getValue().intValue() + 1), 0.0) instanceof BlockAir || PlayerUtil.isInLiquid()) {
                        return;
                    }
                    for (int i = 1; i < this.height.getValue().intValue() + 1; ++i) {
                        Step.mc.thePlayer.motionY -= i;
                    }
                    break;
                }
                case Hypixel: {
                    double mx = Step.mc.thePlayer.motionX;
                    double my = Step.mc.thePlayer.motionY;
                    double mz = Step.mc.thePlayer.motionZ;
                    double x2 = Step.mc.thePlayer.posX;
                    double y2 = Step.mc.thePlayer.posY;
                    double z = Step.mc.thePlayer.posZ;
                    if (Step.mc.thePlayer.onGround) {
                        ++this.onGroundTicks;
                        this.offGroundTicks = 0;
                    } else {
                        ++this.offGroundTicks;
                        this.onGroundTicks = 0;
                    }
                    if (y2 % 1.0 < 0.01 && this.offGroundTicks == 3) {
                        this.setMotion(mx, -0.784, mz);
                        MoveUtil.setSpeed(this.hypixelSmooth.getValue() ? 0.36 : 0.38);
                        Step.mc.thePlayer.setPosition(x2, Math.floor(y2), z);
                    }
                    if (!Step.mc.thePlayer.isCollidedHorizontally || !Step.mc.thePlayer.onGround) break;
                    Step.mc.thePlayer.setPosition(x2, Math.floor(y2), z);
                    this.setMotion(mx, my, mz);
                    Step.mc.thePlayer.jump();
                    MoveUtil.setSpeed(this.hypixelSmooth.getValue() ? 0.41 : 0.43);
                    break;
                }
                case Vanilla: {
                    Step.mc.thePlayer.stepHeight = this.height.getValue().floatValue();
                    if (!this.reverse.getValue().booleanValue() || !PlayerUtil.isBlockUnder(this.height.getValue().floatValue() + Step.mc.thePlayer.getEyeHeight()) || PlayerUtil.isInLiquid()) {
                        return;
                    }
                    if (Step.mc.thePlayer.posY < Step.mc.thePlayer.lastGroundY && !Step.mc.thePlayer.onGround && Step.mc.thePlayer.offGroundTicks <= 1) {
                        Step.mc.thePlayer.motionY = -this.height.getValue().doubleValue();
                    }
                    if (Step.mc.thePlayer.offGroundTicks != 1 || !(Step.mc.thePlayer.posY < Step.mc.thePlayer.lastLastGroundY)) break;
                    Step.mc.timer.timerSpeed = this.timer.getValue().floatValue();
                    break;
                }
                case Vulcan: {
                    Step.mc.thePlayer.stepHeight = Step.mc.thePlayer.ticksSinceJump > 11 ? 1.0f : 0.6f;
                }
            }
        }
    }

    public void setMotion(double motionX, double motionY, double motionZ) {
        Step.mc.thePlayer.motionX = motionX;
        Step.mc.thePlayer.motionY = motionY;
        Step.mc.thePlayer.motionZ = motionZ;
    }

    @EventTarget
    public void onStep(EventStep event) {
        if (!event.isPre()) {
            switch (this.mode.getValue()) {
                case Matrix: {
                    if (event.getStepHeight() > 1.0) {
                        if (this.instantValue.getValue().booleanValue()) {
                            PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(Step.mc.thePlayer.posX, Step.mc.thePlayer.posY + 0.41999998688698, Step.mc.thePlayer.posZ, false));
                            PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(Step.mc.thePlayer.posX, Step.mc.thePlayer.posY + 0.7531999805212, Step.mc.thePlayer.posZ, false));
                            PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(Step.mc.thePlayer.posX, Step.mc.thePlayer.posY + 1.00133597911215, Step.mc.thePlayer.posZ, true));
                            PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(Step.mc.thePlayer.posX, Step.mc.thePlayer.posY + 1.42133596599913, Step.mc.thePlayer.posZ, false));
                            PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(Step.mc.thePlayer.posX, Step.mc.thePlayer.posY + 1.75453595963335, Step.mc.thePlayer.posZ, false));
                            PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(Step.mc.thePlayer.posX, Step.mc.thePlayer.posY + 2.0026719582243, Step.mc.thePlayer.posZ, false));
                            Step.mc.timer.timerSpeed = 0.14285715f;
                        } else {
                            this.doJump = true;
                            this.ticks = 0;
                            Step.mc.thePlayer.setPosition(Step.mc.thePlayer.posX, Step.mc.thePlayer.posY, Step.mc.thePlayer.posZ);
                        }
                        return;
                    }
                    if (!(event.getStepHeight() > (double)0.6f)) break;
                    Step.mc.timer.timerSpeed = 0.33333f;
                    PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(Step.mc.thePlayer.posX, Step.mc.thePlayer.posY + (double)0.42f, Step.mc.thePlayer.posZ, false));
                    PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(Step.mc.thePlayer.posX, Step.mc.thePlayer.posY + (double)0.42f, Step.mc.thePlayer.posZ, true));
                    break;
                }
                case NCP: {
                    if (!Step.mc.thePlayer.onGround || PlayerUtil.isInLiquid()) {
                        return;
                    }
                    double height = event.getStepHeight();
                    if (height <= 0.6) {
                        return;
                    }
                    double[] values = height > 2.019 ? new double[]{0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.919} : (height > 1.869 ? new double[]{0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869} : (height > 1.5 ? new double[]{0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652} : (height > 1.015 ? new double[]{0.42, 0.7532, 1.01, 1.093, 1.015} : (height > 0.875 ? new double[]{0.42, 0.7532} : new double[]{0.39, 0.6938}))));
                    Step.mc.timer.timerSpeed = this.timer.getValue().floatValue();
                    for (double d2 : values) {
                        PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(Step.mc.thePlayer.posX, Step.mc.thePlayer.posY + (d2 + Math.random() / 2000.0), Step.mc.thePlayer.posZ, false));
                    }
                    break;
                }
                case Vanilla: {
                    if (!(event.getStepHeight() > 0.6)) break;
                    Step.mc.timer.timerSpeed = this.timer.getValue().floatValue();
                    break;
                }
                case Vulcan: {
                    if (!(event.getStepHeight() > 0.6)) break;
                    Step.mc.timer.timerSpeed = 0.5f;
                    PacketUtil.send(new C03PacketPlayer.C04PacketPlayerPosition(Step.mc.thePlayer.posX, Step.mc.thePlayer.posY + 0.5, Step.mc.thePlayer.posZ, true));
                }
            }
        }
    }

    public enum stepMode {
        Vanilla,
        NCP,
        Hypixel,
        NCP_PacketLess,
        Vulcan,
        Matrix,
        Jump

    }
}

