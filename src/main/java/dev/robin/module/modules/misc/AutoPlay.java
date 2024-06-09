package dev.robin.module.modules.misc;

import dev.robin.Client;
import dev.robin.event.EventTarget;
import dev.robin.event.world.EventMotion;
import dev.robin.event.world.EventPacketReceive;
import dev.robin.event.world.EventWorldLoad;
import dev.robin.gui.notification.NotificationManager;
import dev.robin.gui.notification.NotificationType;
import dev.robin.module.Category;
import dev.robin.module.Module;
import dev.robin.module.modules.combat.KillAura;
import dev.robin.module.modules.player.ChestStealer;
import dev.robin.module.modules.player.InvCleaner;
import dev.robin.module.modules.world.PlayerWarn;
import dev.robin.module.values.BoolValue;
import dev.robin.module.values.ModeValue;
import dev.robin.module.values.NumberValue;
import dev.robin.utils.PathUtils;
import dev.robin.utils.client.MathUtil;
import dev.robin.utils.client.TimeUtil;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.world.WorldSettings;

public class AutoPlay
extends Module {
    private final ModeValue<playMode> modeValue = new ModeValue("Mode", (Enum[])playMode.values(), (Enum)playMode.HYT);
    private final BoolValue swValue = new BoolValue("SkyWars", true);
    private final BoolValue bwValue = new BoolValue("BedWars", true);
    private final BoolValue toggleModule = new BoolValue("Toggle Module", true);
    private final NumberValue delayValue = new NumberValue("Delay", 3.0, 1.0, 10.0, 0.1);
    public boolean display = false;
    private final TimeUtil timer = new TimeUtil();
    private boolean waiting = false;

    public AutoPlay() {
        super("AutoPlay", Category.Misc);
    }

    @EventTarget
    public void onWorld(EventWorldLoad event) {
        this.waiting = false;
    }

    @EventTarget
    public void onMotion(EventMotion event) {
        if (event.isPost()) {
            return;
        }
        switch ((playMode)((Object)this.modeValue.getValue())) {
            case HYT: {
                if (AutoPlay.mc.playerController.getCurrentGameType() != WorldSettings.GameType.SPECTATOR) {
                    return;
                }
                ItemStack itemStack = AutoPlay.mc.thePlayer.inventoryContainer.getSlot(43).getStack();
                if (this.waiting) {
                    if (this.timer.hasReached(((Double)this.delayValue.getValue()).longValue() * 1000L)) {
                        this.drop(44);
                    }
                    return;
                }
                if (!itemStack.getDisplayName().contains("\u518d\u6765\u4e00\u5c40")) {
                    return;
                }
                if ((!itemStack.getItem().equals(Items.minecart) || !((Boolean)this.swValue.getValue()).booleanValue()) && (!itemStack.getItem().equals(Items.chest_minecart) || !((Boolean)this.bwValue.getValue()).booleanValue())) break;
                this.waiting = true;
                this.timer.reset();
            }
        }
    }

    @EventTarget
    public void onPacketReceiveEvent(EventPacketReceive event) {
        Packet<?> packet = event.getPacket();
        if (event.getPacket() instanceof S45PacketTitle) {
            S45PacketTitle s45 = (S45PacketTitle)packet;
            if (s45.getMessage() == null) {
                return;
            }
            if (s45.getMessage().getUnformattedText().equals("\u00a7b\u00a7l\u7a7a\u5c9b\u6218\u4e89")) {
                Timer timer = new Timer();
                TimerTask task = new TimerTask(){

                    @Override
                    public void run() {
                        PathUtils.findBlinkPath(-21.0, mc.thePlayer.posY, 3.0).forEach(vector3d -> {
                            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(vector3d.x, vector3d.y, vector3d.z, true));
                            mc.thePlayer.setPosition(-21.0, mc.thePlayer.posY, 3.0);
                            mc.thePlayer.inventory.currentItem = 2;
                            mc.playerController.updateController();
                        });
                    }
                };
                timer.schedule(task, 50L);
                NotificationManager.post(NotificationType.SUCCESS, "AutoClip", "Teleport!", 5.0f);
            }
        }
        if (AutoPlay.mc.thePlayer == null || AutoPlay.mc.theWorld == null) {
            return;
        }
        if (event.getPacket() instanceof S02PacketChat) {
            S02PacketChat packet1 = (S02PacketChat)event.getPacket();
            String text = packet1.getChatComponent().getUnformattedText();
            Pattern pattern = Pattern.compile("\u73a9\u5bb6(.*?)\u5728\u672c\u5c40\u6e38\u620f\u4e2d\u884c\u4e3a\u5f02\u5e38");
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                NotificationManager.post(NotificationType.WARNING, "BanChecker", "A player was banned.", 5.0f);
            }
            if ((matcher = (pattern = Pattern.compile("\u4f60\u5728\u5730\u56fe(.*?)\u4e2d\u8d62\u5f97\u4e86(.*?)")).matcher(text)).find() && ((Boolean)this.toggleModule.getValue()).booleanValue()) {
                Client.instance.moduleManager.getModule(InvCleaner.class).setState(false);
                Client.instance.moduleManager.getModule(ChestStealer.class).setState(false);
                Client.instance.moduleManager.getModule(KillAura.class).setState(false);
                NotificationManager.post(NotificationType.SUCCESS, "Game Ending", "Sending you to next game in " + this.delayValue.getValue() + "s", 5.0f);
            }
            if (text.contains("      \u559c\u6b22      \u4e00\u822c      \u4e0d\u559c\u6b22") || text.contains("[\u8d77\u5e8a\u6218\u4e89] Game \u7ed3\u675f\uff01\u611f\u8c22\u60a8\u7684\u53c2\u4e0e\uff01")) {
                NotificationManager.post(NotificationType.SUCCESS, "Game Ending", "Your Health: " + MathUtil.DF_1.format(AutoPlay.mc.thePlayer.getHealth()), 5.0f);
            }
            if (text.contains("\u5f00\u59cb\u5012\u8ba1\u65f6: 1 \u79d2")) {
                if (!Client.instance.moduleManager.getModule(PlayerWarn.class).getState()) {
                    NotificationManager.post(NotificationType.WARNING, "Skywars Warning (Wait 15s)", "\u4e3b\u64ad\u8bf7\u5f00\u542fPlayerTracker.", 15.0f);
                } else {
                    NotificationManager.post(NotificationType.INFO, "Skywars Starting", "\u559c\u62a5\uff0c\u4e3b\u64ad\u4f60\u7684\u8111\u5b50\u5728\u6b63\u5e38\u5de5\u4f5c", 5.0f);
                }
                if (((Boolean)this.toggleModule.getValue()).booleanValue()) {
                    Client.instance.moduleManager.getModule(KillAura.class).setState(true);
                    Client.instance.moduleManager.getModule(InvCleaner.class).setState(true);
                    Client.instance.moduleManager.getModule(ChestStealer.class).setState(true);
                }
            }
            if (text.contains("\u7a7a\u5c9b\u6218\u4e89")) {
                NotificationManager.post(NotificationType.WARNING, "Bedwars Warning (Wait 15s)", "Using OldScaffold May Result In A Ban.", 5.0f);
            }
        }
    }

    public void drop(int slot) {
        AutoPlay.mc.playerController.windowClick(AutoPlay.mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, AutoPlay.mc.thePlayer);
    }

    public static enum playMode {
        HYT;

    }
}

