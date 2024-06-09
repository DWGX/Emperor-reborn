package dev.robin.module.modules.misc;

import dev.robin.Client;
import dev.robin.event.EventTarget;
import dev.robin.event.world.EventMotion;
import dev.robin.event.world.EventPacketSend;
import dev.robin.event.world.EventUpdate;
import dev.robin.event.world.EventWorldLoad;
import dev.robin.module.Category;
import dev.robin.module.Module;
import dev.robin.module.modules.world.Scaffold;
import dev.robin.module.values.BoolValue;
import dev.robin.module.values.ModeValue;
import dev.robin.module.values.NumberValue;
import dev.robin.utils.BlinkUtils;
import dev.robin.utils.RotationComponent;
import dev.robin.utils.client.MathUtil;
import dev.robin.utils.client.PacketUtil;
import dev.robin.utils.client.TimeUtil;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class Disabler
extends Module {
    public static final ModeValue<mode> modeValue = new ModeValue("Mode", (Enum[])mode.values(), (Enum)mode.Grim);
    private final BoolValue oldGrimPostValue = new BoolValue("OldGrim-Post", false, () -> ((mode)((Object)((Object)modeValue.getValue()))).equals((Object)mode.Grim));
    public static final BoolValue postValue = new BoolValue("NewGrim-Post", false, () -> ((mode)((Object)((Object)modeValue.getValue()))).equals((Object)mode.Grim));
    private final BoolValue badPacketsA = new BoolValue("Grim-BadPacketsA", false, () -> ((mode)((Object)((Object)modeValue.getValue()))).equals((Object)mode.Grim));
    public static final BoolValue badPacketsF = new BoolValue("Grim-BadPacketsF", false, () -> ((mode)((Object)((Object)modeValue.getValue()))).equals((Object)mode.Grim));
    public static final BoolValue badPacketsT = new BoolValue("Grim-BadPacketsT",false,()-> ((mode)((Object)((Object)modeValue.getValue()))).equals((Object)mode.Grim));
    private final BoolValue duplicateRotPlace = new BoolValue("DuplicateRotPlace",true,() -> ((mode)((Object)((Object)modeValue.getValue()))).equals((Object)mode.Grim));
    private final NumberValue randLength = new NumberValue("RandomLength",10,1,500,1, duplicateRotPlace::getValue);
    private final BoolValue fakePingValue = new BoolValue("Grim-FakePing", false, () -> ((mode)((Object)((Object)modeValue.getValue()))).equals((Object)mode.Grim));
    private final BoolValue sprint = new BoolValue("Vulcan-Omni-Sprint", true, () -> ((mode)((Object)((Object)modeValue.getValue()))).equals((Object)mode.Vulcan));
    private final BoolValue reach = new BoolValue("Vulcan-Reach (4.5 Blocks)", true, () -> ((mode)((Object)((Object)modeValue.getValue()))).equals((Object)mode.Vulcan));
    private final BoolValue movement = new BoolValue("Vulcan-Strafe and Jump", true, () -> ((mode)((Object)((Object)modeValue.getValue()))).equals((Object)mode.Vulcan));
    private final BoolValue miscellaneous = new BoolValue("Vulcan-Miscellaneous (Auto-Block, BadPackets)", true, () -> ((mode)((Object)((Object)modeValue.getValue()))).equals((Object)mode.Vulcan));
    private final HashMap<Packet<?>, Long> packetsMap = new HashMap();
    int lastSlot = -1;
    boolean lastSprinting;
    public static ConcurrentLinkedDeque<Integer> pingPackets = new ConcurrentLinkedDeque();
    public static Disabler INSTANCE;

    public Disabler() {
        super("Disabler", Category.Misc);
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        this.setSuffix(((mode)((Object)modeValue.getValue())).name());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @EventTarget(value=1)
    public void onUpdate(EventUpdate event) {
        if (((mode)((Object)modeValue.getValue())).equals((Object)mode.Grim) && ((Boolean)this.fakePingValue.getValue()).booleanValue()) {
            try {
                HashMap<Packet<?>, Long> hashMap = this.packetsMap;
                synchronized (hashMap) {
                    Iterator<Map.Entry<Packet<?>, Long>> iterator = this.packetsMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<Packet<?>, Long> entry = iterator.next();
                        if (entry.getValue() >= System.currentTimeMillis()) continue;
                        mc.getNetHandler().addToSendQueue(entry.getKey());
                        iterator.remove();
                    }
                }
            }
            catch (Throwable t2) {
                t2.printStackTrace();
            }
        }
        this.setSuffix(((mode)((Object)modeValue.getValue())).name());
    }

    @EventTarget
    public void onMotion(EventMotion event) {
        if (((mode)((Object)modeValue.getValue())).equals((Object)mode.Vulcan)) {
            if (((Boolean)this.sprint.getValue()).booleanValue()) {
                PacketUtil.send(new C0BPacketEntityAction(Disabler.mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                PacketUtil.send(new C0BPacketEntityAction(Disabler.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
            }
            if (((Boolean)this.movement.getValue()).booleanValue() && Disabler.mc.thePlayer.ticksExisted % 5 == 0) {
                PacketUtil.send(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, new BlockPos(Disabler.mc.thePlayer), EnumFacing.UP));
            }
            if (((Boolean)this.reach.getValue()).booleanValue()) {
                BlinkUtils.setBlinkState(false, false, true, false, false, false, false, false, false, false, false);
                if (Disabler.mc.thePlayer.ticksExisted % 2 == 0) {
                    BlinkUtils.setBlinkState(true, true, false, false, false, false, false, false, false, false, false);
                    BlinkUtils.clearPacket(null, false, -1);
                }
            }
        }
    }

    @EventTarget
    public void onWorld(EventWorldLoad event) {
        this.lastSlot = -1;
        this.lastSprinting = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @EventTarget
    public void onPacket(EventPacketSend event) {
        switch ((mode)((Object)modeValue.getValue())) {
            case Grim: {
                Packet packet = event.getPacket();
                if (Disabler.mc.thePlayer == null) {
                    return;
                }
                if (Disabler.mc.thePlayer.isDead) {
                    return;
                }
                if (((Boolean)badPacketsF.getValue()).booleanValue() && packet instanceof C0BPacketEntityAction) {
                    if (((C0BPacketEntityAction)packet).getAction() == C0BPacketEntityAction.Action.START_SPRINTING) {
                        if (this.lastSprinting) {
                            event.setCancelled(true);
                        }
                        this.lastSprinting = true;
                    } else if (((C0BPacketEntityAction)packet).getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                        if (!this.lastSprinting) {
                            event.setCancelled(true);
                        }
                        this.lastSprinting = false;
                    }
                }
                if (((Boolean)this.badPacketsA.getValue()).booleanValue() && packet instanceof C09PacketHeldItemChange) {
                    int slot = ((C09PacketHeldItemChange)packet).getSlotId();
                    if (slot == this.lastSlot && slot != -1) {
                        event.setCancelled(true);
                    }
                    this.lastSlot = ((C09PacketHeldItemChange)packet).getSlotId();
                }
                if (((Boolean)this.fakePingValue.getValue()).booleanValue() && (packet instanceof C00PacketKeepAlive || packet instanceof C16PacketClientStatus) && !(Disabler.mc.thePlayer.getHealth() <= 0.0f) && !this.packetsMap.containsKey(packet)) {
                    event.setCancelled(true);
                    HashMap<Packet<?>, Long> hashMap = this.packetsMap;
                    synchronized (hashMap) {
                        this.packetsMap.put(packet, System.currentTimeMillis() + TimeUtil.randomDelay(199999, 9999999));
                    }
                }
                if(packet instanceof C02PacketUseEntity && badPacketsT.getValue()){
                    if(((C02PacketUseEntity) packet).getAction() == C02PacketUseEntity.Action.INTERACT){
                        if(((C02PacketUseEntity) packet).getEntityFromWorld(mc.theWorld) instanceof EntityPlayer){
                            event.setCancelled(true);
                        }
                    }
                    if(((C02PacketUseEntity) packet).getAction() == C02PacketUseEntity.Action.INTERACT_AT){
                        if(((C02PacketUseEntity) packet).getEntityFromWorld(mc.theWorld) instanceof EntityPlayer){
                            event.setCancelled(true);
                        }
                    }
                }
                Scaffold scaffold = Client.instance.moduleManager.getModule(Scaffold.class);
                if(duplicateRotPlace.getValue() && scaffold.getState() && packet instanceof C03PacketPlayer){
                    if (RotationComponent.modify && RotationComponent.rotation != null) {
                        ((C03PacketPlayer) packet).setYaw(RotationComponent.rotation.x);
                        ((C03PacketPlayer) packet).setPitch(RotationComponent.rotation.y);
                    }
                    ((C03PacketPlayer) packet).setYaw(getRandomYaw(((C03PacketPlayer) packet).getYaw()));
                }
                if (!((Boolean)this.oldGrimPostValue.getValue()).booleanValue() || ((Boolean)postValue.getValue()).booleanValue() || mc.getCurrentServerData() == null || !(packet instanceof C0APacketAnimation) && !(packet instanceof C02PacketUseEntity) && !(packet instanceof C0EPacketClickWindow) && !(packet instanceof C08PacketPlayerBlockPlacement) && !(packet instanceof C07PacketPlayerDigging)) break;
                PacketUtil.sendPacketC0F();
                break;
            }
        }
    }
    private float getRandomYaw(float requestedYaw){
        int rand = MathUtil.getRandomInRange(1,randLength.getValue().intValue());
        if(!duplicateRotPlace.getValue()){
            return requestedYaw;
        } else {
            return requestedYaw + (360 * rand);
        }
    }
    public static boolean getGrimPost() {
        boolean result = INSTANCE != null && INSTANCE.getState() && ((mode)((Object)modeValue.getValue())).name().equals("Grim") && (Boolean)postValue.getValue() != false && (Boolean)Disabler.INSTANCE.oldGrimPostValue.getValue() == false && Disabler.mc.thePlayer != null && Disabler.mc.thePlayer.isEntityAlive() && Disabler.mc.thePlayer.ticksExisted >= 20 && !(Disabler.mc.currentScreen instanceof GuiDownloadTerrain);
        return result;
    }

    public static void fixC0F(C0FPacketConfirmTransaction packet) {
        short id = packet.getUid();
        if (id >= 0 || pingPackets.isEmpty()) {
            mc.getNetHandler().addToSendQueueUnregistered(packet);
        } else {
            int current;
            do {
                current = pingPackets.getFirst();
                mc.getNetHandler().addToSendQueueUnregistered(new C0FPacketConfirmTransaction(packet.getWindowId(), (short)current, true));
                pingPackets.pollFirst();
            } while (current != id && !pingPackets.isEmpty());
        }
    }

    public static enum mode {
        Grim,
        Vulcan;

    }
}

