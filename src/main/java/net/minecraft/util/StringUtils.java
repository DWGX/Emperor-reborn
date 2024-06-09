package net.minecraft.util;

import java.util.regex.Pattern;

public class StringUtils {
    private static final Pattern patternControlCode = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");

    public static String removeFormattingCodes(String text) {
        return Pattern.compile("(?i)\u00a7[0-9A-FK-OR]").matcher(text).replaceAll("");
    }

    public static String ticksToElapsedTime(int ticks) {
        int i = ticks / 20;
        int j2 = i / 60;
        return (i %= 60) < 10 ? j2 + ":0" + i : j2 + ":" + i;
    }

    public static String stripControlCodes(String text) {
        return patternControlCode.matcher(text).replaceAll("");
    }

    public static boolean isNullOrEmpty(String string) {
        return org.apache.commons.lang3.StringUtils.isEmpty((CharSequence)string);
    }
}

