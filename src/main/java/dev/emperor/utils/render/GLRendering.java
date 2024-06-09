package dev.emperor.utils.render;

import dev.emperor.utils.client.MathUtil;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;

public class GLRendering {
    public static void glDrawPoint(double x2, double y2, float radius, ScaledResolution scaledResolution, int colour) {
        boolean restore = GLRendering.glEnableBlend();
        GL11.glEnable((int)2832);
        GL11.glHint((int)3153, (int)4354);
        GL11.glDisable((int)3553);
        GLRendering.glColour(colour);
        GL11.glPointSize((float)(radius * GL11.glGetFloat((int)2982) * (float)scaledResolution.getScaleFactor()));
        GL11.glBegin((int)0);
        GL11.glVertex2d((double)x2, (double)y2);
        GL11.glEnd();
        GLRendering.glRestoreBlend(restore);
        GL11.glDisable((int)2832);
        GL11.glHint((int)3153, (int)4352);
        GL11.glEnable((int)3553);
    }

    public static void glDrawRoundedQuad(double x2, double y2, float width, float height, float radius, int colour) {
        boolean restore = GLRendering.glEnableBlend();
        boolean alphaTest = GL11.glIsEnabled((int)3008);
        if (alphaTest) {
            GL11.glDisable((int)3008);
        }
        GL11.glDisable((int)3553);
        GL11.glBegin((int)7);
        GL11.glTexCoord2f((float)0.0f, (float)0.0f);
        GL11.glVertex2d((double)x2, (double)y2);
        GL11.glTexCoord2f((float)0.0f, (float)1.0f);
        GL11.glVertex2d((double)x2, (double)(y2 + (double)height));
        GL11.glTexCoord2f((float)1.0f, (float)1.0f);
        GL11.glVertex2d((double)(x2 + (double)width), (double)(y2 + (double)height));
        GL11.glTexCoord2f((float)1.0f, (float)0.0f);
        GL11.glVertex2d((double)(x2 + (double)width), (double)y2);
        GL11.glEnd();
        GL20.glUseProgram((int)0);
        GL11.glEnable((int)3553);
        if (alphaTest) {
            GL11.glEnable((int)3008);
        }
        GLRendering.glRestoreBlend(restore);
    }

    public static void glDrawGradientLine(double x2, double y2, double x1, double y1, float lineWidth, int colour) {
        boolean restore = GLRendering.glEnableBlend();
        GL11.glDisable((int)3553);
        GL11.glLineWidth((float)lineWidth);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glShadeModel((int)7425);
        int noAlpha = Colors.removeAlphaComponent(colour);
        GL11.glDisable((int)3008);
        GL11.glBegin((int)3);
        GLRendering.glColour(noAlpha);
        GL11.glVertex2d((double)x2, (double)y2);
        double dif = x1 - x2;
        GLRendering.glColour(colour);
        GL11.glVertex2d((double)(x2 + dif * 0.4), (double)y2);
        GL11.glVertex2d((double)(x2 + dif * 0.6), (double)y2);
        GLRendering.glColour(noAlpha);
        GL11.glVertex2d((double)x1, (double)y1);
        GL11.glEnd();
        GL11.glEnable((int)3008);
        GL11.glShadeModel((int)7424);
        GLRendering.glRestoreBlend(restore);
        GL11.glDisable((int)2848);
        GL11.glHint((int)3154, (int)4352);
        GL11.glEnable((int)3553);
    }

    public static void glDrawOutlinedQuad(double x2, double y2, double width, double height, float thickness, int colour) {
        boolean restore = GLRendering.glEnableBlend();
        GL11.glDisable((int)3553);
        GLRendering.glColour(colour);
        GL11.glLineWidth((float)thickness);
        GL11.glBegin((int)2);
        GL11.glVertex2d((double)x2, (double)y2);
        GL11.glVertex2d((double)x2, (double)(y2 + height));
        GL11.glVertex2d((double)(x2 + width), (double)(y2 + height));
        GL11.glVertex2d((double)(x2 + width), (double)y2);
        GL11.glEnd();
        GL11.glEnable((int)3553);
        GLRendering.glRestoreBlend(restore);
    }

    public static void glDrawFilledQuad(double x2, double y2, double width, double height, int colour) {
        boolean restore = GLRendering.glEnableBlend();
        GL11.glDisable((int)3553);
        GLRendering.glColour(colour);
        GL11.glBegin((int)7);
        GL11.glVertex2d((double)x2, (double)y2);
        GL11.glVertex2d((double)x2, (double)(y2 + height));
        GL11.glVertex2d((double)(x2 + width), (double)(y2 + height));
        GL11.glVertex2d((double)(x2 + width), (double)y2);
        GL11.glEnd();
        GLRendering.glRestoreBlend(restore);
        GL11.glEnable((int)3553);
    }

    public static void glDrawRoundedRectEllipse(double x2, double y2, double width, double height, RoundingMode roundingMode, int roundingDef, double roundingLevel, int colour) {
        boolean bLeft = false;
        boolean tLeft = false;
        boolean bRight = false;
        boolean tRight = false;
        switch (roundingMode) {
            case TOP: {
                tLeft = true;
                tRight = true;
                break;
            }
            case BOTTOM: {
                bLeft = true;
                bRight = true;
                break;
            }
            case FULL: {
                tLeft = true;
                tRight = true;
                bLeft = true;
                bRight = true;
                break;
            }
            case LEFT: {
                bLeft = true;
                tLeft = true;
                break;
            }
            case RIGHT: {
                bRight = true;
                tRight = true;
                break;
            }
            case TOP_LEFT: {
                tLeft = true;
                break;
            }
            case TOP_RIGHT: {
                tRight = true;
                break;
            }
            case BOTTOM_LEFT: {
                bLeft = true;
                break;
            }
            case BOTTOM_RIGHT: {
                bRight = true;
            }
        }
        GL11.glTranslated((double)x2, (double)y2, (double)0.0);
        GL11.glEnable((int)2881);
        GL11.glHint((int)3154, (int)4354);
        boolean restore = GLRendering.glEnableBlend();
        if (tLeft) {
            GLRendering.glDrawFilledEllipse(roundingLevel, roundingLevel, roundingLevel, (int)((double)roundingDef * 0.5), (int)((double)roundingDef * 0.75), roundingDef, false, colour);
        }
        if (tRight) {
            GLRendering.glDrawFilledEllipse(width - roundingLevel, roundingLevel, roundingLevel, (int)((double)roundingDef * 0.75), roundingDef, roundingDef, false, colour);
        }
        if (bLeft) {
            GLRendering.glDrawFilledEllipse(roundingLevel, height - roundingLevel, roundingLevel, (int)((double)roundingDef * 0.25), (int)((double)roundingDef * 0.5), roundingDef, false, colour);
        }
        if (bRight) {
            GLRendering.glDrawFilledEllipse(width - roundingLevel, height - roundingLevel, roundingLevel, 0, (int)((double)roundingDef * 0.25), roundingDef, false, colour);
        }
        GL11.glDisable((int)2881);
        GL11.glHint((int)3154, (int)4352);
        GL11.glDisable((int)3553);
        GLRendering.glColour(colour);
        GL11.glBegin((int)9);
        if (tLeft) {
            GL11.glVertex2d((double)roundingLevel, (double)roundingLevel);
            GL11.glVertex2d((double)0.0, (double)roundingLevel);
        } else {
            GL11.glVertex2d((double)0.0, (double)0.0);
        }
        if (bLeft) {
            GL11.glVertex2d((double)0.0, (double)(height - roundingLevel));
            GL11.glVertex2d((double)roundingLevel, (double)(height - roundingLevel));
            GL11.glVertex2d((double)roundingLevel, (double)height);
        } else {
            GL11.glVertex2d((double)0.0, (double)height);
        }
        if (bRight) {
            GL11.glVertex2d((double)(width - roundingLevel), (double)height);
            GL11.glVertex2d((double)(width - roundingLevel), (double)(height - roundingLevel));
            GL11.glVertex2d((double)width, (double)(height - roundingLevel));
        } else {
            GL11.glVertex2d((double)width, (double)height);
        }
        if (tRight) {
            GL11.glVertex2d((double)width, (double)roundingLevel);
            GL11.glVertex2d((double)(width - roundingLevel), (double)roundingLevel);
            GL11.glVertex2d((double)(width - roundingLevel), (double)0.0);
        } else {
            GL11.glVertex2d((double)width, (double)0.0);
        }
        if (tLeft) {
            GL11.glVertex2d((double)roundingLevel, (double)0.0);
        }
        GL11.glEnd();
        GLRendering.glRestoreBlend(restore);
        GL11.glTranslated((double)(-x2), (double)(-y2), (double)0.0);
        GL11.glEnable((int)3553);
    }

    public static void glDrawFilledQuad(double x2, double y2, double width, double height, int startColour, int endColour) {
        boolean restore = GLRendering.glEnableBlend();
        GL11.glDisable((int)3553);
        GL11.glShadeModel((int)7425);
        GL11.glBegin((int)7);
        GLRendering.glColour(startColour);
        GL11.glVertex2d((double)x2, (double)y2);
        GLRendering.glColour(endColour);
        GL11.glVertex2d((double)x2, (double)(y2 + height));
        GL11.glVertex2d((double)(x2 + width), (double)(y2 + height));
        GLRendering.glColour(startColour);
        GL11.glVertex2d((double)(x2 + width), (double)y2);
        GL11.glEnd();
        GL11.glShadeModel((int)7424);
        GLRendering.glRestoreBlend(restore);
        GL11.glEnable((int)3553);
    }

    public static void glDrawArcOutline(double x2, double y2, float radius, float angleStart, float angleEnd, float lineWidth, int colour) {
        float[][] vertices;
        int segments = (int)(radius * 4.0f);
        boolean restore = GLRendering.glEnableBlend();
        GL11.glDisable((int)3553);
        GL11.glLineWidth((float)lineWidth);
        GLRendering.glColour(colour);
        GL11.glEnable((int)2848);
        GL11.glHint((int)3154, (int)4354);
        GL11.glTranslated((double)x2, (double)y2, (double)0.0);
        GL11.glBegin((int)3);
        for (float[] vertex : vertices = MathUtil.getArcVertices(radius, angleStart, angleEnd, segments)) {
            GL11.glVertex2f((float)vertex[0], (float)vertex[1]);
        }
        GL11.glEnd();
        GL11.glTranslated((double)(-x2), (double)(-y2), (double)0.0);
        GL11.glDisable((int)2848);
        GL11.glHint((int)3154, (int)4352);
        GLRendering.glRestoreBlend(restore);
        GL11.glEnable((int)3553);
    }

    public static void glDrawRoundedOutline(double x2, double y2, double width, double height, float lineWidth, RoundingMode roundingMode, float rounding, int colour) {
        boolean bLeft = false;
        boolean tLeft = false;
        boolean bRight = false;
        boolean tRight = false;
        switch (roundingMode) {
            case TOP: {
                tLeft = true;
                tRight = true;
                break;
            }
            case BOTTOM: {
                bLeft = true;
                bRight = true;
                break;
            }
            case FULL: {
                tLeft = true;
                tRight = true;
                bLeft = true;
                bRight = true;
                break;
            }
            case LEFT: {
                bLeft = true;
                tLeft = true;
                break;
            }
            case RIGHT: {
                bRight = true;
                tRight = true;
                break;
            }
            case TOP_LEFT: {
                tLeft = true;
                break;
            }
            case TOP_RIGHT: {
                tRight = true;
                break;
            }
            case BOTTOM_LEFT: {
                bLeft = true;
                break;
            }
            case BOTTOM_RIGHT: {
                bRight = true;
            }
        }
        GL11.glTranslated((double)x2, (double)y2, (double)0.0);
        boolean restore = GLRendering.glEnableBlend();
        if (tLeft) {
            GLRendering.glDrawArcOutline(rounding, rounding, rounding, 270.0f, 360.0f, lineWidth, colour);
        }
        if (tRight) {
            GLRendering.glDrawArcOutline(width - (double)rounding, rounding, rounding, 0.0f, 90.0f, lineWidth, colour);
        }
        if (bLeft) {
            GLRendering.glDrawArcOutline(rounding, height - (double)rounding, rounding, 180.0f, 270.0f, lineWidth, colour);
        }
        if (bRight) {
            GLRendering.glDrawArcOutline(width - (double)rounding, height - (double)rounding, rounding, 90.0f, 180.0f, lineWidth, colour);
        }
        GL11.glDisable((int)3553);
        GLRendering.glColour(colour);
        GL11.glBegin((int)1);
        if (tLeft) {
            GL11.glVertex2d((double)0.0, (double)rounding);
        } else {
            GL11.glVertex2d((double)0.0, (double)0.0);
        }
        if (bLeft) {
            GL11.glVertex2d((double)0.0, (double)(height - (double)rounding));
            GL11.glVertex2d((double)rounding, (double)height);
        } else {
            GL11.glVertex2d((double)0.0, (double)height);
            GL11.glVertex2d((double)0.0, (double)height);
        }
        if (bRight) {
            GL11.glVertex2d((double)(width - (double)rounding), (double)height);
            GL11.glVertex2d((double)width, (double)(height - (double)rounding));
        } else {
            GL11.glVertex2d((double)width, (double)height);
            GL11.glVertex2d((double)width, (double)height);
        }
        if (tRight) {
            GL11.glVertex2d((double)width, (double)rounding);
            GL11.glVertex2d((double)(width - (double)rounding), (double)0.0);
        } else {
            GL11.glVertex2d((double)width, (double)0.0);
            GL11.glVertex2d((double)width, (double)0.0);
        }
        if (tLeft) {
            GL11.glVertex2d((double)rounding, (double)0.0);
        } else {
            GL11.glVertex2d((double)0.0, (double)0.0);
        }
        GL11.glEnd();
        GLRendering.glRestoreBlend(restore);
        GL11.glTranslated((double)(-x2), (double)(-y2), (double)0.0);
        GL11.glEnable((int)3553);
    }

    public static void glScissorBox(double x2, double y2, double width, double height, ScaledResolution scaledResolution) {
        if (!GL11.glIsEnabled((int)3089)) {
            GL11.glEnable((int)3089);
        }
        int scaling = scaledResolution.getScaleFactor();
        GL11.glScissor((int)((int)(x2 * (double)scaling)), (int)((int)(((double)scaledResolution.getScaledHeight() - (y2 + height)) * (double)scaling)), (int)((int)(width * (double)scaling)), (int)((int)(height * (double)scaling)));
    }

    public static void glDrawRoundedRect(double x2, double y2, double width, double height, RoundingMode roundingMode, float rounding, float scaleFactor, int colour) {
        boolean bLeft = false;
        boolean tLeft = false;
        boolean bRight = false;
        boolean tRight = false;
        switch (roundingMode) {
            case TOP: {
                tLeft = true;
                tRight = true;
                break;
            }
            case BOTTOM: {
                bLeft = true;
                bRight = true;
                break;
            }
            case FULL: {
                tLeft = true;
                tRight = true;
                bLeft = true;
                bRight = true;
                break;
            }
            case LEFT: {
                bLeft = true;
                tLeft = true;
                break;
            }
            case RIGHT: {
                bRight = true;
                tRight = true;
                break;
            }
            case TOP_LEFT: {
                tLeft = true;
                break;
            }
            case TOP_RIGHT: {
                tRight = true;
                break;
            }
            case BOTTOM_LEFT: {
                bLeft = true;
                break;
            }
            case BOTTOM_RIGHT: {
                bRight = true;
            }
        }
        float alpha = (float)(colour >> 24 & 0xFF) / 255.0f;
        boolean restore = GLRendering.glEnableBlend();
        GLRendering.glColour(colour);
        GL11.glTranslated((double)x2, (double)y2, (double)0.0);
        GL11.glDisable((int)3553);
        GL11.glBegin((int)9);
        if (tLeft) {
            GL11.glVertex2d((double)rounding, (double)rounding);
            GL11.glVertex2d((double)0.0, (double)rounding);
        } else {
            GL11.glVertex2d((double)0.0, (double)0.0);
        }
        if (bLeft) {
            GL11.glVertex2d((double)0.0, (double)(height - (double)rounding));
            GL11.glVertex2d((double)rounding, (double)(height - (double)rounding));
            GL11.glVertex2d((double)rounding, (double)height);
        } else {
            GL11.glVertex2d((double)0.0, (double)height);
        }
        if (bRight) {
            GL11.glVertex2d((double)(width - (double)rounding), (double)height);
            GL11.glVertex2d((double)(width - (double)rounding), (double)(height - (double)rounding));
            GL11.glVertex2d((double)width, (double)(height - (double)rounding));
        } else {
            GL11.glVertex2d((double)width, (double)height);
        }
        if (tRight) {
            GL11.glVertex2d((double)width, (double)rounding);
            GL11.glVertex2d((double)(width - (double)rounding), (double)rounding);
            GL11.glVertex2d((double)(width - (double)rounding), (double)0.0);
        } else {
            GL11.glVertex2d((double)width, (double)0.0);
        }
        if (tLeft) {
            GL11.glVertex2d((double)rounding, (double)0.0);
        }
        GL11.glEnd();
        GL11.glEnable((int)2832);
        GL11.glHint((int)3153, (int)4354);
        GL11.glPointSize((float)(rounding * 2.0f * GL11.glGetFloat((int)2982) * scaleFactor));
        GL11.glBegin((int)0);
        if (tLeft) {
            GL11.glVertex2d((double)rounding, (double)rounding);
        }
        if (tRight) {
            GL11.glVertex2d((double)(width - (double)rounding), (double)rounding);
        }
        if (bLeft) {
            GL11.glVertex2d((double)rounding, (double)(height - (double)rounding));
        }
        if (bRight) {
            GL11.glVertex2d((double)(width - (double)rounding), (double)(height - (double)rounding));
        }
        GL11.glEnd();
        GL11.glDisable((int)2832);
        GL11.glHint((int)3153, (int)4352);
        GLRendering.glRestoreBlend(restore);
        GL11.glTranslated((double)(-x2), (double)(-y2), (double)0.0);
        GL11.glEnable((int)3553);
    }

    public static void glDrawFilledEllipse(double x2, double y2, double radius, int startIndex, int endIndex, int polygons, boolean smooth, int colour) {
        boolean restore = GLRendering.glEnableBlend();
        if (smooth) {
            GL11.glEnable((int)2881);
            GL11.glHint((int)3155, (int)4354);
        }
        GL11.glDisable((int)3553);
        GLRendering.glColour(colour);
        GL11.glDisable((int)2884);
        GL11.glBegin((int)9);
        GL11.glVertex2d((double)x2, (double)y2);
        for (double i = (double)startIndex; i <= (double)endIndex; i += 1.0) {
            double theta = Math.PI * 2 * i / (double)polygons;
            GL11.glVertex2d((double)(x2 + radius * Math.cos(theta)), (double)(y2 + radius * Math.sin(theta)));
        }
        GL11.glEnd();
        GLRendering.glRestoreBlend(restore);
        if (smooth) {
            GL11.glDisable((int)2881);
            GL11.glHint((int)3155, (int)4352);
        }
        GL11.glEnable((int)2884);
        GL11.glEnable((int)3553);
    }

    public static void glDrawFilledEllipse(double x2, double y2, float radius, int colour) {
        boolean restore = GLRendering.glEnableBlend();
        GL11.glEnable((int)2832);
        GL11.glHint((int)3153, (int)4354);
        GL11.glDisable((int)3553);
        GLRendering.glColour(colour);
        GL11.glPointSize((float)radius);
        GL11.glBegin((int)0);
        GL11.glVertex2d((double)x2, (double)y2);
        GL11.glEnd();
        GLRendering.glRestoreBlend(restore);
        GL11.glDisable((int)2832);
        GL11.glHint((int)3153, (int)4352);
        GL11.glEnable((int)3553);
    }

    public static void glEndScissor() {
        GL11.glDisable((int)3089);
    }

    public static void glDrawFilledRect(double x2, double y2, double x1, double y1, int color) {
        boolean restore = GLRendering.glEnableBlend();
        GL11.glDisable((int)3553);
        GLRendering.glColour(color);
        GL11.glBegin((int)7);
        GL11.glVertex2d((double)x2, (double)y2);
        GL11.glVertex2d((double)x2, (double)y1);
        GL11.glVertex2d((double)x1, (double)y1);
        GL11.glVertex2d((double)x1, (double)y2);
        GL11.glEnd();
        GLRendering.glRestoreBlend(restore);
        GL11.glEnable((int)3553);
    }

    public static void glDrawSidewaysGradientRect(double x2, double y2, double width, double height, int startColour, int endColour) {
        boolean restore = GLRendering.glEnableBlend();
        GL11.glDisable((int)3553);
        GL11.glShadeModel((int)7425);
        GL11.glBegin((int)7);
        GLRendering.glColour(startColour);
        GL11.glVertex2d((double)x2, (double)y2);
        GL11.glVertex2d((double)x2, (double)(y2 + height));
        GLRendering.glColour(endColour);
        GL11.glVertex2d((double)(x2 + width), (double)(y2 + height));
        GL11.glVertex2d((double)(x2 + width), (double)y2);
        GL11.glEnd();
        GL11.glShadeModel((int)7424);
        GL11.glEnable((int)3553);
        GLRendering.glRestoreBlend(restore);
    }

    public static void glDrawFilledRect(double x2, double y2, double x1, double y1, int startColour, int endColour) {
        boolean restore = GLRendering.glEnableBlend();
        GL11.glDisable((int)3553);
        GL11.glShadeModel((int)7425);
        GL11.glBegin((int)7);
        GLRendering.glColour(startColour);
        GL11.glVertex2d((double)x2, (double)y2);
        GLRendering.glColour(endColour);
        GL11.glVertex2d((double)x2, (double)y1);
        GL11.glVertex2d((double)x1, (double)y1);
        GLRendering.glColour(startColour);
        GL11.glVertex2d((double)x1, (double)y2);
        GL11.glEnd();
        GL11.glShadeModel((int)7424);
        GL11.glEnable((int)3553);
        GLRendering.glRestoreBlend(restore);
    }

    public static void glFilledQuad(double x2, double y2, double width, double height, int color) {
        GL11.glDisable((int)3553);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glBegin((int)7);
        RenderUtil.color(color);
        GL11.glVertex2d((double)x2, (double)y2);
        GL11.glVertex2d((double)x2, (double)(y2 + height));
        GL11.glVertex2d((double)(x2 + width), (double)(y2 + height));
        GL11.glVertex2d((double)(x2 + width), (double)y2);
        GL11.glEnd();
        GL11.glEnable((int)3553);
    }

    public static void glFilledEllipse(double x2, double y2, float radius, int color) {
        GL11.glDisable((int)3553);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)2832);
        GL11.glPointSize((float)radius);
        GL11.glBegin((int)0);
        RenderUtil.color(color);
        GL11.glVertex2d((double)x2, (double)y2);
        GL11.glEnd();
        GL11.glDisable((int)2832);
        GL11.glEnable((int)3553);
    }

    public static void glColour(int color) {
        GL11.glColor4ub((byte)((byte)(color >> 16 & 0xFF)), (byte)((byte)(color >> 8 & 0xFF)), (byte)((byte)(color & 0xFF)), (byte)((byte)(color >> 24 & 0xFF)));
    }

    public static boolean glEnableBlend() {
        boolean wasEnabled = GL11.glIsEnabled((int)3042);
        if (!wasEnabled) {
            GL11.glEnable((int)3042);
            GL14.glBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        }
        return wasEnabled;
    }

    public static void glRestoreBlend(boolean wasEnabled) {
        if (!wasEnabled) {
            GL11.glDisable((int)3042);
        }
    }

    public static enum RoundingMode {
        TOP_LEFT,
        BOTTOM_LEFT,
        TOP_RIGHT,
        BOTTOM_RIGHT,
        LEFT,
        RIGHT,
        TOP,
        BOTTOM,
        FULL;

    }
}

