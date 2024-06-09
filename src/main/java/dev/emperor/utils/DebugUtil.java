package dev.emperor.utils;

import dev.emperor.Client;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class DebugUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();

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
        String text = EnumChatFormatting.BOLD + "[" + Client.NAME + "] " + EnumChatFormatting.RESET + " " + message;
        DebugUtil.mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(text));
    }

    public static void print(boolean prefix, String message) {
        if (DebugUtil.mc.thePlayer != null) {
            if (prefix) {
                message = "§2[§d§eEmperor§r§5] " + message;
            }
            DebugUtil.mc.thePlayer.addChatMessage(new ChatComponentText(message));
        }
    }

    public static void log(String prefix, Object message) {
        String text = EnumChatFormatting.BOLD + "[" + Client.NAME + "-" + prefix + "] " + EnumChatFormatting.RESET + " " + message;
        DebugUtil.mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(text));
    }

    public static void logcheater(String prefix, Object message) {
        String text = EnumChatFormatting.BOLD + "[" + Client.NAME + "-" + prefix + "] " + EnumChatFormatting.RESET + " " + message;
        DebugUtil.mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(text));
    }

    private static boolean isDev() {
        return true;
    }
}

