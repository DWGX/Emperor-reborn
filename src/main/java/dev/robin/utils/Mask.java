package dev.robin.utils;

import org.lwjgl.opengl.GL11;

public class Mask {
    public static void defineMask() {
        GL11.glDepthMask((boolean)true);
        GL11.glClearDepth((double)1.0);
        GL11.glClear((int)256);
        GL11.glDepthFunc((int)519);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glColorMask((boolean)false, (boolean)false, (boolean)false, (boolean)false);
    }

    public static void finishDefineMask() {
        GL11.glDepthMask((boolean)false);
        GL11.glColorMask((boolean)true, (boolean)true, (boolean)true, (boolean)true);
    }

    public static void drawOnMask() {
        GL11.glDepthFunc((int)514);
    }

    public static void drawOffMask() {
        GL11.glDepthFunc((int)517);
    }

    public static void drawMCMask() {
        GL11.glDepthFunc((int)515);
    }

    public static void resetMask() {
        GL11.glDepthMask((boolean)true);
        GL11.glClearDepth((double)1.0);
        GL11.glClear((int)256);
        Mask.drawMCMask();
        GL11.glDepthMask((boolean)false);
        GL11.glDisable((int)2929);
    }
}

