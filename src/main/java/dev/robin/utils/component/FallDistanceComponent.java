package dev.robin.utils.component;

import dev.robin.Client;
import dev.robin.event.EventTarget;
import dev.robin.event.world.EventMotion;

public final class FallDistanceComponent {
    public static float distance;
    private float lastDistance;

    @EventTarget
    private void onMotion(EventMotion event) {
        if (event.isPre()) {
            float fallDistance = Client.mc.thePlayer.fallDistance;
            if (fallDistance == 0.0f) {
                distance = 0.0f;
            }
            distance += fallDistance - this.lastDistance;
            this.lastDistance = fallDistance;
        }
    }
}

