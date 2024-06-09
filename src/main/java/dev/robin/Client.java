package dev.robin;

import dev.robin.command.CommandManager;
import dev.robin.config.ConfigManager;
import dev.robin.event.EventManager;
import dev.robin.gui.altmanager.AltManager;
import dev.robin.gui.ui.UiManager;
import dev.robin.module.Module;
import dev.robin.module.ModuleManager;
import dev.robin.module.values.Value;
import dev.robin.utils.component.*;
import dev.robin.utils.RotationComponent;
import dev.robin.utils.SlotSpoofManager;
import dev.robin.utils.YawPitchHelper;
import dev.robin.utils.client.menu.BetterMainMenu;
import dev.robin.utils.concurrent.CustomRejectedExecutionHandler;
import dev.robin.utils.concurrent.CustomThreadFactory;
import dev.robin.utils.concurrent.CustomThreadPoolExecutor;
import dev.robin.utils.novoshader.BackgroundShader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import org.apache.commons.compress.utils.IOUtils;
import org.lwjgl.compatibility.display.Display;
import sun.misc.Unsafe;

public class Client {
    private ExecutorService executor;
    public static Minecraft mc = Minecraft.getMinecraft();
    public static Client instance;
    public static String NAME;
    public static String VERSION;
    public static ResourceLocation cape;
    public String USER = "";
    private static boolean logged;
    public String commandPrefix = ".";
    public ConfigManager configManager;
    public AltManager altManager;
    public ModuleManager moduleManager;
    public CommandManager commandManager;
    public UiManager uiManager;
    public SlotSpoofManager slotSpoofManager;
    public YawPitchHelper yawPitchHelper;
    public List<Float> cGUIPosX = new ArrayList<Float>();
    public List<Float> cGUIPosY = new ArrayList<Float>();
    public List<Module> cGUIInSetting = new ArrayList<Module>();
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
                //SplashScreen.setProgress(12, "Fonts");
                //SplashScreen.setProgress(13, "MemCleaner");
                //SplashScreen.setProgress(14, "Done");
                this.setWindowIcon();
                try {
                    Client.instance.setLogged(true);
                    Client.instance.USER = "Loser Xylitol";
                    Client.instance.moduleManager = new ModuleManager();
                    EventManager.register(Client.instance);
                    EventManager.register(new RotationComponent());
                    EventManager.register(new FallDistanceComponent());
                    EventManager.register(new InventoryClickFixComponent());
                    EventManager.register(new PingSpoofComponent());
                    //EventManager.register(new PacketComponent());
                    //EventManager.register(new BlinkComponent());
                    EventManager.register(new BadPacketsComponent());
                    Client.instance.moduleManager.init();
                    Client.instance.commandManager.init();
                    Client.instance.uiManager.init();
                    Client.instance.configManager.loadAllConfig();
                    mc.displayGuiScreen(new BetterMainMenu());
                }
                catch (Exception ex13) {}
            }
            catch (Exception e) {
            }
        }
        catch (Throwable throwable) {
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
                    inputstream = Client.mc.mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("icons/icon_16x16.png"));
                    inputstream1 = Client.mc.mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("icons/icon_32x32.png"));
                    if (inputstream == null || inputstream1 == null) break block5;
                    Display.setIcon(new ByteBuffer[]{mc.readImageToBuffer(inputstream), mc.readImageToBuffer(inputstream1)});
                }
                catch (IOException ioexception) {
                    try {
                        Minecraft.logger.error("Couldn't set icon", (Throwable)ioexception);
                    }
                    catch (Throwable throwable) {
                        IOUtils.closeQuietly(inputstream);
                        IOUtils.closeQuietly(inputstream1);
                        throw throwable;
                    }
                    IOUtils.closeQuietly((Closeable)inputstream);
                    IOUtils.closeQuietly(inputstream1);
                }
            }
            IOUtils.closeQuietly((Closeable)inputstream);
            IOUtils.closeQuietly((Closeable)inputstream1);
        }
    }

    public AltManager getAltManager() {
        return this.altManager;
    }

    public SlotSpoofManager getSlotSpoofManager() {
        return this.slotSpoofManager;
    }

    public YawPitchHelper getYawPitchHelper() {
        return this.yawPitchHelper;
    }

    public List<Float> getCGUIPosX() {
        return this.cGUIPosX;
    }
    public ExecutorService getExecutor() {
        return this.executor;
    }


}

