package dev.emperor.module.modules.combat;

import dev.emperor.event.EventTarget;
import dev.emperor.event.attack.EventAttack;
import dev.emperor.event.world.EventMotion;
import dev.emperor.event.world.EventStep;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
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
    private final ModeValue<modeEnums> modeValue = new ModeValue("Mode", (Enum[])modeEnums.values(), (Enum)modeEnums.Hypixel);
    private final NumberValue hurtTimeValue = new NumberValue("HurtTime", 15.0, 0.0, 20.0, 1.0);
    private final NumberValue delayValue = new NumberValue("Delay", 3.0, 0.0, 10.0, 0.5);
    private int groundTicks;

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
        this.setSuffix(((modeEnums)((Object)this.modeValue.getValue())).toString());
        this.groundTicks = PlayerUtil.isOnGround(0.01) ? ++this.groundTicks : 0;
        if (this.groundTicks > 20) {
            this.groundTicks = 20;
        }
        if (this.modeValue.getValue() == modeEnums.NoGround) {
            event.setOnGround(false);
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
        if (event.isPre() && canCrit && event.getTarget().hurtResistantTime <= ((Double)this.hurtTimeValue.getValue()).intValue() && this.prevent.hasPassed(300L) && this.timer.hasPassed((long)((Double)this.delayValue.getValue()).intValue() * 100L)) {
            switch (((modeEnums)((Object)this.modeValue.getValue())).toString().toLowerCase()) {
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
            }
        }
    }

    private static enum modeEnums {
        Packet,
        Hypixel,
        HvH,
        Hop,
        Jump,
        Visual,
        NoGround;

    }
}

