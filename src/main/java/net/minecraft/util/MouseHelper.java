package net.minecraft.util;

import org.lwjgl.compatibility.display.Display;
import org.lwjgl.input.Mouse;

public class MouseHelper {
    public int deltaX;
    public int deltaY;

    public void grabMouseCursor() {
        Mouse.setGrabbed((boolean)true);
        this.deltaX = 0;
        this.deltaY = 0;
    }

    public void ungrabMouseCursor() {
        Mouse.setCursorPosition((int)(Display.getWidth() / 2), (int)(Display.getHeight() / 2));
        Mouse.setGrabbed((boolean)false);
    }

    public void mouseXYChange() {
        this.deltaX = Mouse.getDX();
        this.deltaY = Mouse.getDY();
    }
}

