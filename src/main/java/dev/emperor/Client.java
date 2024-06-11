package dev.emperor;

import dev.emperor.command.CommandManager;
import dev.emperor.config.ConfigManager;
import dev.emperor.event.EventManager;
import dev.emperor.gui.altmanager.AltManager;
import dev.emperor.gui.ui.UiManager;
import dev.emperor.module.Module;
import dev.emperor.module.ModuleManager;
import dev.emperor.module.values.Value;
import dev.emperor.utils.component.*;
import dev.emperor.utils.RotationComponent;
import dev.emperor.utils.SlotSpoofManager;
import dev.emperor.utils.YawPitchHelper;
import dev.emperor.utils.client.menu.BetterMainMenu;
import dev.emperor.utils.concurrent.CustomRejectedExecutionHandler;
import dev.emperor.utils.concurrent.CustomThreadFactory;
import dev.emperor.utils.concurrent.CustomThreadPoolExecutor;
import dev.emperor.utils.novoshader.BackgroundShader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import org.apache.commons.compress.utils.IOUtils;
import org.lwjgl.compatibility.display.Display;
import sun.misc.Unsafe;

public class Client {
    @Getter
    private ExecutorService executor;
    public static Minecraft mc = Minecraft.getMinecraft();
    public static Client instance;
    public static String NAME = "Emperor";
    public static String VERSION;
    public static ResourceLocation cape;
    public String USER = "";
    private static boolean logged;
    public String commandPrefix = ".";
    public ConfigManager configManager;
    @Getter
    public AltManager altManager;
    public ModuleManager moduleManager;
    public CommandManager commandManager;
    public UiManager uiManager;
    @Getter
    public SlotSpoofManager slotSpoofManager;
    @Getter
    public YawPitchHelper yawPitchHelper;
    public List<Float> cGUIPosX = new ArrayList<>();
    public List<Float> cGUIPosY = new ArrayList<>();
    public List<Module> cGUIInSetting = new ArrayList<>();
    public List<Value<?>> cGUIInMode = new ArrayList();
    public static Unsafe theUnsafe;
    public BackgroundShader blobShader;

    public String getUser() {
        return this.USER;
    }

    public String getVersion() {
        return VERSION;
    }

    public boolean isLogged() {
        return logged;
    }

    public void setLogged(boolean state) {
        logged = state;
    }

    public Client() {
        logged = false;
    }

    public void init() {
        var corePoolSize = 10;
        var maximumPoolSize = 50;
        var keepAliveTime = 60L;
        var workQueue = new LinkedBlockingQueue<Runnable>(100);
        Client.logged = true;
        this.executor = new CustomThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                workQueue,
                new CustomThreadFactory(),
                new CustomRejectedExecutionHandler()
        );
        try {
            try {
                //SplashScreen.setProgress(10, "ModuleManager");
                Client.instance = this;
                //SplashScreen.setProgress(11, "EventManager");
                this.altManager = new AltManager();
                this.commandManager = new CommandManager();
                this.configManager = new ConfigManager();
                this.uiManager = new UiManager();
                this.slotSpoofManager = new SlotSpoofManager();
                this.yawPitchHelper = new YawPitchHelper();
                this.setWindowIcon();
                try {
                    Client.instance.setLogged(true);
                    Client.instance.USER = "Loser";
                    Client.instance.moduleManager = new ModuleManager();
                    EventManager.register(Client.instance);
                    EventManager.register(new RotationComponent());
                    EventManager.register(new FallDistanceComponent());
                    EventManager.register(new InventoryClickFixComponent());
                    EventManager.register(new PingSpoofComponent());
                    EventManager.register(new BadPacketsComponent());
                    Client.instance.moduleManager.init();
                    Client.instance.commandManager.init();
                    Client.instance.uiManager.init();
                    Client.instance.configManager.loadAllConfig();
                    mc.displayGuiScreen(new BetterMainMenu());
                }
                catch (Exception ignored) {}
            }
            catch (Exception ignored) {
            }
        }
        catch (Throwable ignored) {
        }
    }


    public static void displayGuiScreen(GuiScreen guiScreenIn) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void setWindowIcon() {
        Util.EnumOS util$enumos = Util.getOSType();
        if (util$enumos != Util.EnumOS.OSX) {
            InputStream inputstream1;
            InputStream inputstream;
            block5: {
                inputstream = null;
                inputstream1 = null;
                try {
                    inputstream = Client.mc.mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("/assets/minecraft/express/icon/bh16.png"));
                    inputstream1 = Client.mc.mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("/assets/minecraft/express/icon/bh32.png"));
                    if (inputstream == null || inputstream1 == null) break block5;
                    Display.setIcon(new ByteBuffer[]{mc.readImageToBuffer(inputstream), mc.readImageToBuffer(inputstream1)});
                }
                catch (IOException ioexception) {
                    try {
                        Minecraft.logger.error("Couldn't set icon", ioexception);
                    }
                    catch (Throwable throwable) {
                        IOUtils.closeQuietly(inputstream);
                        IOUtils.closeQuietly(inputstream1);
                        throw throwable;
                    }
                    IOUtils.closeQuietly(inputstream);
                    IOUtils.closeQuietly(inputstream1);
                }
            }
            IOUtils.closeQuietly(inputstream);
            IOUtils.closeQuietly(inputstream1);
        }
    }
}

