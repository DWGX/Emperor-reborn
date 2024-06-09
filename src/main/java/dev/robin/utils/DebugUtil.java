package dev.robin.utils;

import dev.robin.Client;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class DebugUtil {
    private static Minecraft mc = Minecraft.getMinecraft();

    public static void print(Object ... debug) {
        if (DebugUtil.isDev()) {
            String message = Arrays.toString(debug);
            DebugUtil.mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(message));
        }
    }

    public static void log(Object message) {
        String text = String.valueOf(message);
        DebugUtil.mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(text));
    }

    public static void log(boolean prefix, Object message) {
        String text = (Object)((Object)EnumChatFormatting.BOLD) + "[" + Client.NAME + "] " + (Object)((Object)EnumChatFormatting.RESET) + " " + message;
        DebugUtil.mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(text));
    }

    public static void print(boolean prefix, String message) {
        if (DebugUtil.mc.thePlayer != null) {
            if (prefix) {
                message = "\u00a72[\u00a7d\u00a7Robin\u00a7r\u00a75] " + message;
            }
            DebugUtil.mc.thePlayer.addChatMessage(new ChatComponentText(message));
        }
    }

    public static void log(String prefix, Object message) {
        String text = (Object)((Object)EnumChatFormatting.BOLD) + "[" + Client.NAME + "-" + prefix + "] " + (Object)((Object)EnumChatFormatting.RESET) + " " + message;
        DebugUtil.mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(text));
    }

    public static void logcheater(String prefix, Object message) {
        String text = (Object)((Object)EnumChatFormatting.BOLD) + "[" + Client.NAME + "-" + prefix + "] " + (Object)((Object)EnumChatFormatting.RESET) + " " + message;
        DebugUtil.mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(text));
    }

    private static boolean isDev() {
        return true;
    }
}

