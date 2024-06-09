package dev.emperor.utils.render.fontRender;

import dev.emperor.Client;

import java.awt.Font;
import java.io.InputStream;

public class FontManager {
    private static final String locate = "express/font/";
    public static RapeMasterFontManager arial10;
    public static RapeMasterFontManager arial12;
    public static RapeMasterFontManager arial14;
    public static RapeMasterFontManager arial16;
    public static RapeMasterFontManager arial18;
    public static RapeMasterFontManager arial20;
    public static RapeMasterFontManager arial22;
    public static RapeMasterFontManager arial24;
    public static RapeMasterFontManager arial26;
    public static RapeMasterFontManager thin40;
    public static RapeMasterFontManager thin16;
    public static RapeMasterFontManager arial64;
    public static RapeMasterFontManager arial28;
    public static RapeMasterFontManager arial32;
    public static RapeMasterFontManager arial40;
    public static RapeMasterFontManager arial42;
    public static RapeMasterFontManager splash40;
    public static RapeMasterFontManager splash18;
    public static RapeMasterFontManager icon22;
    public static RapeMasterFontManager Tahoma12;
    public static RapeMasterFontManager Tahoma14;
    public static RapeMasterFontManager Tahoma16;
    public static RapeMasterFontManager Tahoma18;
    public static RapeMasterFontManager Tahoma20;
    public static RapeMasterFontManager Tahoma22;
    public static RapeMasterFontManager Tahoma24;
    public static RapeMasterFontManager Tahoma26;
    public static RapeMasterFontManager Tahoma28;
    public static RapeMasterFontManager Tahoma32;
    public static RapeMasterFontManager Tahoma40;
    public static RapeMasterFontManager Tahoma42;
    public static RapeMasterFontManager icontestFont35;
    public static RapeMasterFontManager icontestFont90;
    public static RapeMasterFontManager icontestFont80;
    public static RapeMasterFontManager icontestFont20;
    public static RapeMasterFontManager icontestFont40;
    public static RapeMasterFontManager lettuceFont20;
    public static RapeMasterFontManager lettuceFont24;
    public static RapeMasterFontManager lettuceBoldFont26;
    public static RapeMasterFontManager infoFontBold;
    public static RapeMasterFontManager titleFontBold;
    public static RapeMasterFontManager bold22;
    public static RapeMasterFontManager infoFont;
    public static RapeMasterFontManager titleFont;

    public static void init() {
        thin40 = new RapeMasterFontManager(FontManager.getFont("sfthin.ttf", 40.0f));
        thin16 = new RapeMasterFontManager(FontManager.getFont("sfthin.ttf", 16.0f));
        arial10 = new RapeMasterFontManager(FontManager.getFont("font.ttf", 10.0f));
        arial12 = new RapeMasterFontManager(FontManager.getFont("font.ttf", 12.0f));
        arial14 = new RapeMasterFontManager(FontManager.getFont("font.ttf", 14.0f));
        arial16 = new RapeMasterFontManager(FontManager.getFont("font.ttf", 16.0f));
        arial18 = new RapeMasterFontManager(FontManager.getFont("font.ttf", 18.0f));
        arial20 = new RapeMasterFontManager(FontManager.getFont("font.ttf", 20.0f));
        arial22 = new RapeMasterFontManager(FontManager.getFont("font.ttf", 22.0f));
        arial24 = new RapeMasterFontManager(FontManager.getFont("font.ttf", 24.0f));
        arial26 = new RapeMasterFontManager(FontManager.getFont("font.ttf", 26.0f));
        arial28 = new RapeMasterFontManager(FontManager.getFont("font.ttf", 28.0f));
        arial32 = new RapeMasterFontManager(FontManager.getFont("font.ttf", 32.0f));
        arial40 = new RapeMasterFontManager(FontManager.getFont("font.ttf", 40.0f));
        arial42 = new RapeMasterFontManager(FontManager.getFont("font.ttf", 42.0f));
        splash40 = new RapeMasterFontManager(FontManager.getFont("font.ttf", 40.0f));
        splash18 = new RapeMasterFontManager(FontManager.getFont("font.ttf", 18.0f));
        arial64 = new RapeMasterFontManager(FontManager.getFont("font.ttf", 64.0f));
        Tahoma12 = new RapeMasterFontManager(FontManager.getFont("font.ttf", 12.0f));
        Tahoma14 = new RapeMasterFontManager(FontManager.getFont("font.ttf", 14.0f));
        Tahoma16 = new RapeMasterFontManager(FontManager.getFont("font.ttf", 16.0f));
        Tahoma18 = new RapeMasterFontManager(FontManager.getFont("font.ttf", 18.0f));
        Tahoma20 = new RapeMasterFontManager(FontManager.getFont("font.ttf", 20.0f));
        Tahoma22 = new RapeMasterFontManager(FontManager.getFont("font.ttf", 22.0f));
        Tahoma24 = new RapeMasterFontManager(FontManager.getFont("font.ttf", 24.0f));
        Tahoma26 = new RapeMasterFontManager(FontManager.getFont("font.ttf", 26.0f));
        Tahoma28 = new RapeMasterFontManager(FontManager.getFont("font.ttf", 28.0f));
        Tahoma32 = new RapeMasterFontManager(FontManager.getFont("font.ttf", 32.0f));
        Tahoma40 = new RapeMasterFontManager(FontManager.getFont("font.ttf", 40.0f));
        Tahoma42 = new RapeMasterFontManager(FontManager.getFont("font.ttf", 42.0f));
        bold22 = new RapeMasterFontManager(FontManager.getFont("bold.ttf", 18.0f));
        titleFontBold = new RapeMasterFontManager(FontManager.getFont("tahoma.ttf", 18.0f));
        infoFontBold = new RapeMasterFontManager(FontManager.getFont("tahoma.ttf", 15.0f));
        titleFont = new RapeMasterFontManager(FontManager.getFont("tahoma.ttf", 19.0f));
        infoFont = new RapeMasterFontManager(FontManager.getFont("tahoma.ttf", 12.0f));
        icontestFont90 = new RapeMasterFontManager(FontManager.getFont("icont.ttf", 90.0f));
        icontestFont80 = new RapeMasterFontManager(FontManager.getFont("icont.ttf", 80.0f));
        icontestFont35 = new RapeMasterFontManager(FontManager.getFont("icont.ttf", 35.0f));
        icon22 = new RapeMasterFontManager(FontManager.getFont("iconfont.ttf", 22.0f));
        icontestFont20 = new RapeMasterFontManager(FontManager.getFont("icont.ttf", 20.0f));
        icontestFont40 = new RapeMasterFontManager(FontManager.getFont("icont.ttf", 40.0f));
        lettuceFont20 = new RapeMasterFontManager(FontManager.getFont("geologica.ttf", 20.0f));
        lettuceFont24 = new RapeMasterFontManager(FontManager.getFont("geologica.ttf", 24.0f));
        lettuceBoldFont26 = new RapeMasterFontManager(FontManager.getFont("geologica-bold.ttf", 26.0f));
    }

    private static Font getFont(String fontName, float fontSize) {
        Font font = null;
        try {
            InputStream inputStream = Client.class.getResourceAsStream("/assets/minecraft/express/font/" + fontName);
            assert (inputStream != null);
            font = Font.createFont(0, inputStream);
            font = font.deriveFont(fontSize);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return font;
    }
}

