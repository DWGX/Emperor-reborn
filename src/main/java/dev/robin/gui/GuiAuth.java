package dev.robin.gui;

import dev.robin.Client;
import dev.robin.event.EventManager;
import dev.robin.gui.GuiConnectIRC;
import dev.robin.gui.TextField;
import dev.robin.gui.elements.MDUIButton;
import dev.robin.gui.elements.MessageBox;
import dev.robin.module.ModuleManager;
import dev.robin.utils.RotationComponent;
import dev.robin.utils.VMCheck;
import dev.robin.utils.client.menu.utils.MainMenuBackground;
import dev.robin.utils.component.*;
import dev.robin.utils.render.RenderUtil;
import dev.robin.utils.render.animation.Direction;
import dev.robin.utils.render.fontRender.FontManager;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.compatibility.Sys;
import org.lwjgl.compatibility.display.Display;
import sun.misc.Unsafe;

public class GuiAuth
extends GuiScreen {
    float rectWidth = 240.0f;
    private TextField uid;
    private MessageBox msgbox = new MessageBox("sb", 300.0f, 100.0f);
    private MDUIButton btn = new MDUIButton("Login", 0, new Color(255, 64, 129));
    private MDUIButton hwid = new MDUIButton("Copy HWID", 1, new Color(255, 64, 129));
    File file1;

    public GuiAuth() {
        try {
            this.shaderBackground = new MainMenuBackground("/assets/minecraft/express/shader/login.fsh");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initGui() {
        this.file1 = new File(this.mc.mcDataDir, "Robin/uid.bin");
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        float defX = (float)scaledResolution.getScaledWidth() - this.rectWidth;
        this.uid = new TextField(FontManager.arial18, (float)((int)(defX + 32.0f)), 112.0f, 180.0f, 12.0f, "UID");
        this.btn.initGui();
        this.msgbox.initGui();
        this.hwid.initGui();
        if (this.file1.exists()) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(this.file1));
                this.uid.setText(bufferedReader.readLine());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_) {
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        this.renderBackground();
        RenderUtil.drawRect((float)scaledResolution.getScaledWidth() - this.rectWidth, 0.0, (float)(scaledResolution.getScaledWidth() - 130) + this.rectWidth, scaledResolution.getScaledHeight(), Color.WHITE.getRGB());
        float defX = (float)scaledResolution.getScaledWidth() - this.rectWidth;
        FontManager.thin40.drawString("Login", defX + 32.0f, 32.0f, 0);
        FontManager.thin16.drawString("Login to the client. | Powered by Fl0wowp4rty ", defX + 32.0f, (float)(32 + FontManager.thin40.getFontHeight()) + 4.0f, Color.GRAY.getRGB());
        this.uid.drawTextBox();
        this.btn.x = defX + 32.0f;
        this.btn.y = 168.0f;
        this.btn.width = 60.0f;
        this.btn.height = 20.0f;
        this.btn.clickAction = () -> {
            Unsafe[] unsafe = new Unsafe[]{null};
            Field[] f = new Field[1];
            try {
                String hwid;
                try {
                    Class<?> clz = Class.forName("xyz.blackfaithfully.License");
                    hwid = (String)clz.getDeclaredMethod("invoke", Integer.TYPE, Object.class).invoke(null, 1644674983, null);
                }
                catch (Throwable var2) {
                    hwid = "dev";
                }
                if (VMCheck.getInstance().runChecks()) {
                    Display.destroy();
                    Minecraft.getMinecraft().thePlayer = null;
                    EventManager.unregister(this);
                    try {
                        Unsafe.class.getMethod("putAddress", Long.TYPE, Long.TYPE).invoke(unsafe[0], 8964, 8964);
                    }
                    catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
                        throw new RuntimeException(ex);
                    }
                    try {
                        Unsafe.class.getMethod("freeMemory", Long.TYPE).invoke(unsafe[0], "\u64cd\u4f60\u5988".hashCode());
                    }
                    catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
                        throw new RuntimeException(ex);
                    }
                    Object[] o2 = null;
                    while (true) {
                        o2 = new Object[]{o2};
                    }
                }
                Client.instance.setLogged(true);
                Client.instance.moduleManager = new ModuleManager();
                EventManager.register(Client.instance);
                EventManager.register(new RotationComponent());
                EventManager.register(new FallDistanceComponent());
                EventManager.register(new InventoryClickFixComponent());
                EventManager.register(new PingSpoofComponent());
                EventManager.register(new PacketComponent());
                EventManager.register(new BlinkComponent());
                EventManager.register(new BadPacketsComponent());
                Client.instance.moduleManager.init();
                Client.instance.commandManager.init();
                Client.instance.uiManager.init();
                Client.instance.configManager.loadAllConfig();
                this.mc.displayGuiScreen(new GuiConnectIRC());
            }
            catch (Exception exception) {
                // empty catch block
            }
        };
        this.hwid.x = defX + 32.0f;
        this.hwid.y = 208.0f;
        this.hwid.width = 60.0f;
        this.hwid.height = 20.0f;
        this.hwid.clickAction = () -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            this.msgbox.message = "HWID \u5df2\u590d\u5236\u3002";
            this.msgbox.anim.setDirection(Direction.FORWARDS);
        };
        this.btn.drawScreen(p_drawScreen_1_, p_drawScreen_2_);
        this.hwid.drawScreen(p_drawScreen_1_, p_drawScreen_2_);
        super.drawScreen(p_drawScreen_1_, p_drawScreen_2_, p_drawScreen_3_);
        this.msgbox.draw(p_drawScreen_1_, p_drawScreen_2_);
    }

    public static byte[] download(String u2) throws Exception {
        int c;
        URL url = new URL(u2);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        InputStream is = conn.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(is);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int BUFFER_SIZE = 2048;
        int EOF = -1;
        byte[] buf = new byte[2048];
        while ((c = bis.read(buf)) != -1) {
            baos.write(buf, 0, c);
            baos.flush();
        }
        conn.disconnect();
        bis.close();
        is.close();
        byte[] data = baos.toByteArray();
        baos.close();
        return data;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.uid.keyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        float defX = (float)scaledResolution.getScaledWidth() - this.rectWidth;
        if (RenderUtil.isHovering(defX + 32.0f, (float)(32 + FontManager.thin40.getFontHeight()) + 4.0f, FontManager.arial16.getStringWidth("Login to the client. | Powered by Fl0wowp4rty "), FontManager.arial16.getFontHeight(), mouseX, mouseY)) {
            Sys.openURL((String)"https://fl0wowp4rty.top");
        }
        this.uid.mouseClicked(mouseX, mouseY, mouseButton);
        this.btn.mouseClicked(mouseX, mouseY, mouseButton);
        this.hwid.mouseClicked(mouseX, mouseY, mouseButton);
        this.msgbox.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}

