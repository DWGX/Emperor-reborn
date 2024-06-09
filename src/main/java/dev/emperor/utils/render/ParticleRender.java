package dev.emperor.utils.render;

import dev.emperor.module.modules.render.HUD;
import dev.emperor.utils.render.animation.AnimTimeUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;

public class ParticleRender {
    public static final List<Particle> particles = new ArrayList<Particle>();
    public static int rendered;
    public static final AnimTimeUtil timer;
    private static boolean sentParticles;

    public static void render(float x2, float y2, EntityLivingBase target) {
        for (Particle p : particles) {
            p.x = x2 + 20.0f;
            p.y = y2 + 20.0f;
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            if (!(p.opacity > 4.0f)) continue;
            p.render2D();
        }
        if (timer.hasTimeElapsed(16L, true)) {
            for (Particle p : particles) {
                p.updatePosition();
                if (!(p.opacity < 1.0f)) continue;
                particles.remove(p);
            }
        }
        if (target.hurtTime == 9 && !sentParticles) {
            for (int i = 0; i <= 15; ++i) {
                Particle particle = new Particle();
                particle.init(x2 + 20.0f, y2 + 20.0f, (float)((Math.random() - 0.5) * 2.0 * 1.4), (float)((Math.random() - 0.5) * 2.0 * 1.4), (float)(Math.random() * 4.0), new Color(HUD.mainColor.getColor()));
                particles.add(particle);
            }
            sentParticles = true;
        }
        if (target.hurtTime == 8) {
            sentParticles = false;
        }
    }

    public static void add(Particle particle) {
        particles.add(particle);
    }

    static {
        timer = new AnimTimeUtil();
    }
}

