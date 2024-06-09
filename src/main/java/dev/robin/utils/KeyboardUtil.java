package dev.robin.utils;

import dev.robin.utils.misc.MinecraftInstance;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class KeyboardUtil
implements MinecraftInstance {
    public static boolean isPressed(KeyBinding key) {
        return Keyboard.isKeyDown((int)key.getKeyCode());
    }

    public static void resetKeybinding(KeyBinding key) {
        key.pressed = KeyboardUtil.mc.currentScreen != null ? false : KeyboardUtil.isPressed(key);
    }

    public static void resetKeybindings(KeyBinding ... keys) {
        for (KeyBinding key : keys) {
            KeyboardUtil.resetKeybinding(key);
        }
    }
}

