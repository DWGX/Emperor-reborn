package dev.emperor.module.modules.combat;

import dev.emperor.event.EventTarget;
import dev.emperor.event.attack.EventAttack;
import dev.emperor.event.world.EventMotion;
import dev.emperor.event.world.EventStep;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.modules.player.Blink;
import dev.emperor.module.values.ModeValue;
import dev.emperor.module.values.NumberValue;
import dev.emperor.utils.client.MathUtil;
import dev.emperor.utils.client.TimeUtil;
import dev.emperor.utils.player.PlayerUtil;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

public class Criticals
extends Module {
    private final TimeUtil timer = new TimeUtil();
    private final TimeUtil prevent = new TimeUtil();
    private final ModeValue<modeEnums> modeValue = new ModeValue("Mode", modeEnums.values(), modeEnums.Hypixel);
    private final NumberValue hurtTimeValue = new NumberValue("HurtTime", 15.0, 0.0, 20.0, 1.0);
    private final NumberValue delayValue = new NumberValue("Delay", 3.0, 0.0, 10.0, 0.5);
    private final NumberValue timervalue = new NumberValue("TimerValue", 1F, 0.1F, 1F, 0.1);
    private final NumberValue jumpHeight = new NumberValue("JumpHeight", 0.42F, 0.1F, 1.0F, 0.01F);
    private final NumberValue airTime = new NumberValue("AirTime", 10, 5, 20, 1);
    private int groundTicks;

    private int blinktick = 0;
    private int jump = 0;
    private int airtick = 0;
    private boolean dotimercri = false;


    public Criticals() {
        super("Criticals", Category.Combat);
    }

    @Override
    public void onEnable() {
        this.timer.reset();
        this.prevent.reset();
        this.groundTicks = 0;
    }

    @EventTarget
    void onUpdate(EventMotion event) {
        this.setSuffix(this.modeValue.getValue().toString());
        this.groundTicks = PlayerUtil.isOnGround(0.01) ? ++this.groundTicks : 0;
        if (this.groundTicks > 20) {
            this.groundTicks = 20;
        }
        if (this.modeValue.getValue() == modeEnums.NoGround) {
            event.setOnGround(false);
        }
        
        if (this.modeValue.getValue() == modeEnums.Grim) {
            Module blink = getModule(Blink.class);
            if (!mc.thePlayer.onGround) {
                airtick++;
            } else {
                airtick = 0;
            }

            if (blink.getState()) {
                blinktick = 100;
            } else {
                blinktick--;
            }

            if (dotimercri && getModule(KillAura.class).getState()) {
                if (airtick == 1) {
                    mc.gameSettings.keyBindJump.pressed = false;
                }

                if (airtick >= 6 && airtick <= airTime.getValue().intValue() && !mc.thePlayer.onGround) {
                    mc.timer.timerSpeed = timervalue.getValue().floatValue();
                }

                if (airtick > airTime.getValue().intValue() || airtick == 0 || mc.thePlayer.onGround) {
                    mc.timer.timerSpeed = 1F;
                    dotimercri = false;
                }
            }

            jump++;
        }
    }

    @EventTarget
    void onStep(EventStep event) {
        if (!event.isPre()) {
            this.prevent.reset();
        }
    }

    @EventTarget
    void onAttack(EventAttack event) {
        boolean canCrit;
        boolean bl = canCrit = this.groundTicks > 3 && Criticals.mc.theWorld.getBlockState(new BlockPos(Criticals.mc.thePlayer.posX, Criticals.mc.thePlayer.posY - 1.0, Criticals.mc.thePlayer.posZ)).getBlock().isFullBlock() && !PlayerUtil.isInLiquid() && !PlayerUtil.isOnLiquid() && !Criticals.mc.thePlayer.isOnLadder() && Criticals.mc.thePlayer.ridingEntity == null && Criticals.mc.thePlayer.onGround;
        if (event.isPre() && canCrit && event.getTarget().hurtResistantTime <= this.hurtTimeValue.getValue().intValue() && this.prevent.hasPassed(300L) && this.timer.hasPassed((long) this.delayValue.getValue().intValue() * 100L)) {
            switch (this.modeValue.getValue().toString().toLowerCase()) {
                case "hypixel": {
                    double[] values;
                    for (double value : values = new double[]{0.0625 + Math.random() / 100.0, 0.03125 + Math.random() / 100.0}) {
                        mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(Criticals.mc.thePlayer.posX, Criticals.mc.thePlayer.posY + value, Criticals.mc.thePlayer.posZ, false));
                    }
                    break;
                }
                case "hvh": {
                    for (double offset : new double[]{0.06253453, 0.02253453, 0.001253453, 1.135346E-4}) {
                        Criticals.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Criticals.mc.thePlayer.posX, Criticals.mc.thePlayer.posY + offset, Criticals.mc.thePlayer.posZ, false));
                    }
                    break;
                }
                case "packet": {
                    double[] values = new double[]{0.0425, 0.0015, MathUtil.getRandom().nextBoolean() ? 0.012 : 0.014};
                    if (Criticals.mc.thePlayer.ticksExisted % 2 != 0) break;
                    for (double value : values) {
                        double random = MathUtil.getRandom().nextBoolean() ? MathUtil.getRandom(-1.0E-8, -1.0E-7) : MathUtil.getRandom(1.0E-7, 1.0E-8);
                        mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(Criticals.mc.thePlayer.posX, Criticals.mc.thePlayer.posY + value + random, Criticals.mc.thePlayer.posZ, false));
                    }
                    break;
                }
                case "visual": {
                    Criticals.mc.thePlayer.onCriticalHit(event.getTarget());
                    break;
                }
                case "jump": {
                    Criticals.mc.thePlayer.jump();
                    break;
                }
                case "hop": {
                    Criticals.mc.thePlayer.motionY = 0.1;
                    Criticals.mc.thePlayer.fallDistance = 0.1f;
                    Criticals.mc.thePlayer.onGround = false;
                }
                case "grin": {
                    Module blink = getModule(Blink.class);
                    if (!blink.getState() && blinktick <= 0 && getModule(KillAura.class).getState()) {
                        if (mc.thePlayer.onGround && jump > 10 && airtick == 0 && !dotimercri && mc.thePlayer.hurtTime == 0) {
                            mc.gameSettings.keyBindJump.pressed = true;
                            jump = 0;
                            airtick = 0;
                            dotimercri = true;
                            mc.timer.timerSpeed = 2f - timervalue.getValue().floatValue();
                        }
                    } else {
                        if (mc.thePlayer.onGround && jump > 10) {
                            mc.thePlayer.motionY = jumpHeight.getValue().floatValue();
                            jump = 0;
                        }
                    }
                }
            }
        }
    }

    private enum modeEnums {
        Packet,
        Hypixel,
        HvH,
        Hop,
        Jump,
        Visual,
        Grim,
        NoGround

    }
}

