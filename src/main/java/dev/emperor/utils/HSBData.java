package dev.emperor.utils;

import java.awt.Color;

public class HSBData {
    private float hue;
    private float saturation;
    private float brightness;
    private float alpha = 1.0f;

    public HSBData(float hue, float saturation, float brightness, float alpha) {
        this.hue = hue;
        this.saturation = saturation;
        this.brightness = brightness;
        this.alpha = alpha;
    }

    public HSBData(Color color) {
        float[] hsbColor = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        this.hue = hsbColor[0];
        this.saturation = hsbColor[1];
        this.brightness = hsbColor[2];
    }

    public Color getAsColor() {
        Color beforeReAlpha = Color.getHSBColor(this.hue, this.saturation, this.brightness);
        return new Color(beforeReAlpha.getRed(), beforeReAlpha.getGreen(), beforeReAlpha.getBlue(), Math.round(255.0f * this.alpha));
    }

    public float getHue() {
        return this.hue;
    }

    public void setHue(float hue) {
        this.hue = hue;
    }

    public float getSaturation() {
        return this.saturation;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    public float getBrightness() {
        return this.brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public float getAlpha() {
        return this.alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }
}

