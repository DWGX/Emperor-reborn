package dev.emperor.utils.render;

import java.awt.Color;

public class Particle {
    public float x;
    public float y;
    public float adjustedX;
    public float adjustedY;
    public float deltaX;
    public float deltaY;
    public float size;
    public float opacity;
    public Color color;

    public void render2D() {
        RenderUtil.drawGoodCircle(this.x + this.adjustedX + this.size / 2.0f, this.y + this.adjustedY + this.size / 2.0f, this.size / 2.0f, ColorUtil.applyOpacity(this.color, this.opacity / 255.0f).getRGB());
    }

    public void updatePosition() {
        for (int i = 1; i <= 2; ++i) {
            this.adjustedX += this.deltaX;
            this.adjustedY += this.deltaY;
            this.deltaY = (float)((double)this.deltaY * 0.975);
            this.deltaX = (float)((double)this.deltaX * 0.975);
            this.opacity -= 3.0f;
            if (!(this.opacity < 1.0f)) continue;
            this.opacity = 1.0f;
        }
    }

    public void init(float x2, float y2, float deltaX, float deltaY, float size, Color color) {
        this.x = x2;
        this.y = y2;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.size = size;
        this.opacity = 254.0f;
        this.color = color;
    }
}

