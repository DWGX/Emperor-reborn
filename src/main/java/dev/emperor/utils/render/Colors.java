package dev.emperor.utils.render;

import dev.emperor.utils.client.MathUtil;
import java.awt.Color;
import java.text.NumberFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class Colors {
    public static final int BLACK = Color.BLACK.getRGB();
    public static final int WHITE = Color.WHITE.getRGB();
    public static final int RED = new Color(16007990).getRGB();
    public static final int PINK = new Color(16744619).getRGB();
    public static final int PURPLE = new Color(12216520).getRGB();
    public static final int DEEP_PURPLE = new Color(8281781).getRGB();
    public static final int INDIGO = new Color(7964363).getRGB();
    public static final int GREY = new Color(-9868951).getRGB();
    public static final int BLUE = new Color(1668818).getRGB();
    public static final int LIGHT_BLUE = new Color(7652351).getRGB();
    public static final int CYAN = new Color(44225).getRGB();
    public static final int TEAL = new Color(11010027).getRGB();
    public static final int GREEN = new Color(65350).getRGB();

    public static int getColor(int red, int green, int blue) {
        return Colors.getColor(red, green, blue, 255);
    }

    public static int getColor(Color color) {
        return Colors.getColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static int getColor(int brightness) {
        return Colors.getColor(brightness, brightness, brightness, 255);
    }

    public static int getColor(int brightness, int alpha) {
        return Colors.getColor(brightness, brightness, brightness, alpha);
    }

    public static int getColor(int red, int green, int blue, int alpha) {
        int color = MathHelper.clamp_int(alpha, 0, 255) << 24;
        color |= MathHelper.clamp_int(red, 0, 255) << 16;
        color |= MathHelper.clamp_int(green, 0, 255) << 8;
        return color |= MathHelper.clamp_int(blue, 0, 255);
    }

    public static int rainbow(long delay) {
        double rainbowState = ((double)System.currentTimeMillis() + (double)delay * 1.2) / 8.0;
        return Color.getHSBColor((float)(rainbowState % 360.0 / 360.0), 0.5f, 0.9f).getRGB();
    }

    public static Color blendColors(float[] fractions, Color[] colors, float progress) {
        if (fractions == null) {
            throw new IllegalArgumentException("Fractions can't be null");
        }
        if (colors == null) {
            throw new IllegalArgumentException("Colours can't be null");
        }
        if (fractions.length == colors.length) {
            int[] indicies = Colors.getFractionIndicies(fractions, progress);
            float[] range = new float[]{fractions[indicies[0]], fractions[indicies[1]]};
            Color[] colorRange = new Color[]{colors[indicies[0]], colors[indicies[1]]};
            float max = range[1] - range[0];
            float value = progress - range[0];
            float weight = value / max;
            Color color = Colors.blend(colorRange[0], colorRange[1], 1.0f - weight);
            return color;
        }
        throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
    }

    public static int removeAlphaComponent(int colour) {
        int red = colour >> 16 & 0xFF;
        int green = colour >> 8 & 0xFF;
        int blue = colour & 0xFF;
        return (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
    }

    public static int[] getFractionIndicies(float[] fractions, float progress) {
        int startPoint;
        int[] range = new int[2];
        for (startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; ++startPoint) {
        }
        if (startPoint >= fractions.length) {
            startPoint = fractions.length - 1;
        }
        range[0] = startPoint - 1;
        range[1] = startPoint;
        return range;
    }

    public static Color blend(Color color1, Color color2, double ratio) {
        float r2 = (float)ratio;
        float ir = 1.0f - r2;
        float[] rgb1 = new float[3];
        float[] rgb2 = new float[3];
        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);
        float red = rgb1[0] * r2 + rgb2[0] * ir;
        float green = rgb1[1] * r2 + rgb2[1] * ir;
        float blue = rgb1[2] * r2 + rgb2[2] * ir;
        if (red < 0.0f) {
            red = 0.0f;
        } else if (red > 255.0f) {
            red = 255.0f;
        }
        if (green < 0.0f) {
            green = 0.0f;
        } else if (green > 255.0f) {
            green = 255.0f;
        }
        if (blue < 0.0f) {
            blue = 0.0f;
        } else if (blue > 255.0f) {
            blue = 255.0f;
        }
        Color color3 = null;
        try {
            color3 = new Color(red, green, blue);
        }
        catch (IllegalArgumentException exp) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            System.out.println(nf.format(red) + "; " + nf.format(green) + "; " + nf.format(blue));
            exp.printStackTrace();
        }
        return color3;
    }

    public static Color interpolateColors(Color color1, Color color2, float point) {
        if (point > 1.0f) {
            point = 1.0f;
        }
        return new Color((int)((float)(color2.getRed() - color1.getRed()) * point + (float)color1.getRed()), (int)((float)(color2.getGreen() - color1.getGreen()) * point + (float)color1.getGreen()), (int)((float)(color2.getBlue() - color1.getBlue()) * point + (float)color1.getBlue()));
    }

    public static Color interpolateColorsDynamic(double speed, int index, Color start, Color end) {
        int angle = (int)(((double)System.currentTimeMillis() / speed + (double)index) % 360.0);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return Colors.interpolateColors(start, end, (float)angle / 360.0f);
    }

    public static Color getHealthColor(EntityLivingBase e, int alpha) {
        double health = e.getHealth();
        double maxHealth = e.getMaxHealth();
        double healthPercentage = health / maxHealth;
        Color c1 = Color.RED;
        Color c2 = Color.GREEN;
        Color healthColor = new Color((int)MathUtil.linearInterpolate(c1.getRed(), c2.getRed(), healthPercentage), (int)MathUtil.linearInterpolate(c1.getGreen(), c2.getGreen(), healthPercentage), (int)MathUtil.linearInterpolate(c1.getBlue(), c2.getBlue(), healthPercentage), alpha);
        return healthColor.darker();
    }

    public static Color applyOpacity(Color color, float opacity) {
        opacity = Math.min(1.0f, Math.max(0.0f, opacity));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)((float)color.getAlpha() * opacity));
    }

}

