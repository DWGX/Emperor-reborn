package dev.robin.gui.clickgui.book;

import dev.robin.utils.render.AnimationUtil;
import dev.robin.utils.render.RenderUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;

public class RippleAnimation {
    public final List<Ripple> ripples = new ArrayList<Ripple>();

    public void addRipple(float x2, float y2, float radius, float speed) {
        this.ripples.add(new Ripple(x2, y2, radius, speed));
    }

    public void mouseClicked(float mouseX, float mouseY, float speed) {
        this.ripples.add(new Ripple(mouseX, mouseY, 100.0f, speed));
    }

    public void mouseClicked(float mouseX, float mouseY) {
        this.ripples.add(new Ripple(mouseX, mouseY, 100.0f, 1.0f));
    }

    public void draw(float x2, float y2, float width, float height) {
        GL11.glDepthMask((boolean)true);
        GL11.glClearDepth((double)1.0);
        GL11.glClear((int)256);
        GL11.glDepthFunc((int)519);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glColorMask((boolean)false, (boolean)false, (boolean)false, (boolean)false);
        RenderUtil.drawRect(x2, y2, width, height, -1);
        GL11.glDepthMask((boolean)false);
        GL11.glColorMask((boolean)true, (boolean)true, (boolean)true, (boolean)true);
        GL11.glDepthFunc((int)514);
        for (Ripple c : this.ripples) {
            c.progress = AnimationUtil.animateSmooth(c.progress, c.topRadius, c.speed / 50.0f);
            RenderUtil.drawCircle2(c.x, c.y, c.progress, new Color(1.0f, 1.0f, 1.0f, (1.0f - Math.min(1.0f, Math.max(0.0f, c.progress / c.topRadius))) / 2.0f).getRGB());
        }
        GL11.glDepthMask((boolean)true);
        GL11.glClearDepth((double)1.0);
        GL11.glClear((int)256);
        GL11.glDepthFunc((int)515);
        GL11.glDepthMask((boolean)false);
        GL11.glDisable((int)2929);
    }

    public void draw(Runnable context) {
        GL11.glDepthMask((boolean)true);
        GL11.glClearDepth((double)1.0);
        GL11.glClear((int)256);
        GL11.glDepthFunc((int)519);
        GL11.glEnable((int)2929);
        GL11.glDepthMask((boolean)true);
        GL11.glColorMask((boolean)false, (boolean)false, (boolean)false, (boolean)false);
        context.run();
        GL11.glDepthMask((boolean)false);
        GL11.glColorMask((boolean)true, (boolean)true, (boolean)true, (boolean)true);
        GL11.glDepthFunc((int)514);
        for (Ripple c : this.ripples) {
            c.progress = AnimationUtil.animateSmooth(c.progress, c.topRadius, c.speed / 50.0f);
            RenderUtil.drawCircle2(c.x, c.y, c.progress, new Color(1.0f, 1.0f, 1.0f, (1.0f - Math.min(1.0f, Math.max(0.0f, c.progress / c.topRadius))) / 2.0f).getRGB());
        }
        GL11.glDepthMask((boolean)true);
        GL11.glClearDepth((double)1.0);
        GL11.glClear((int)256);
        GL11.glDepthFunc((int)515);
        GL11.glDepthMask((boolean)false);
        GL11.glDisable((int)2929);
    }

    public static class Ripple {
        public float x;
        public float y;
        public float topRadius;
        public float speed;
        public float alpha;
        public float progress;
        public boolean complete;

        public Ripple(float x2, float y2, float rad, float speed) {
            this.x = x2;
            this.y = y2;
            this.alpha = 200.0f;
            this.topRadius = rad;
            this.speed = speed;
        }
    }
}

