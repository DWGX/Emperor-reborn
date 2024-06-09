package dev.emperor.utils.render;

import java.awt.Font;
import java.io.InputStream;

public final class ResourceUtil {
    public static Font createFontTTF(String path) {
        try {
            return Font.createFont(0, ResourceUtil.getResourceStream(path));
        }
        catch (Exception ignored) {
            return null;
        }
    }

    public static InputStream getResourceStream(String path) {
        String s2 = "/assets/minecraft/express/" + path;
        return ResourceUtil.class.getResourceAsStream(s2);
    }
}

