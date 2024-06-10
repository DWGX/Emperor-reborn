package dev.emperor.module.values;

import java.awt.Color;

public class ColorValue
extends Value<Integer> {
    private int color;

    public ColorValue(String name, int color, Value.Dependency dependenc) {
        super(name, dependenc);
        this.setValue(color);
        this.color = color;
    }

    public ColorValue(String name, int color) {
        super(name);
        this.setValue(color);
        this.color = color;
    }

    public int getColor() {
        return this.color;
    }

    public Color getColorC() {
        return new Color(this.color);
    }

    public void setColor(int color) {
        this.setValue(color);
        this.color = color;
    }

    public float[] getHSB() {
        float hue;
        float saturation;
        if (this.value == null) {
            return new float[]{0.0f, 0.0f, 0.0f};
        }
        float[] hsbValues = new float[3];
        int cMax = Math.max(this.value >>> 16 & 0xFF, this.value >>> 8 & 0xFF);
        if ((this.value & 0xFF) > cMax) {
            cMax = this.value & 0xFF;
        }
        int cMin = Math.min(this.value >>> 16 & 0xFF, this.value >>> 8 & 0xFF);
        if ((this.value & 0xFF) < cMin) {
            cMin = this.value & 0xFF;
        }
        float brightness = (float)cMax / 255.0f;
        float f = saturation = cMax != 0 ? (float)(cMax - cMin) / (float)cMax : 0.0f;
        if (saturation == 0.0f) {
            hue = 0.0f;
        } else {
            float redC = (float)(cMax - (this.value >>> 16 & 0xFF)) / (float)(cMax - cMin);
            float greenC = (float)(cMax - (this.value >>> 8 & 0xFF)) / (float)(cMax - cMin);
            float blueC = (float)(cMax - (this.value & 0xFF)) / (float)(cMax - cMin);
            hue = ((this.value >>> 16 & 0xFF) == cMax ? blueC - greenC : ((this.value >>> 8 & 0xFF) == cMax ? 2.0f + redC - blueC : 4.0f + greenC - redC)) / 6.0f;
            if (hue < 0.0f) {
                hue += 1.0f;
            }
        }
        hsbValues[0] = hue;
        hsbValues[1] = saturation;
        hsbValues[2] = brightness;
        return hsbValues;
    }

    @Override
    public Integer getConfigValue() {
        return this.color;
    }
}

