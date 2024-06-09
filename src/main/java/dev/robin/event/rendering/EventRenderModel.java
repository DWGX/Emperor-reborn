package dev.robin.event.rendering;

import dev.robin.event.api.events.Event;
import net.minecraft.entity.EntityLivingBase;

public class EventRenderModel
implements Event {
    private boolean pre = true;
    private final EntityLivingBase entity;
    private final Runnable modelRenderer;
    private final Runnable layerRenderer;

    public EventRenderModel(EntityLivingBase entity, Runnable modelRenderer, Runnable layerRenderer) {
        this.entity = entity;
        this.modelRenderer = modelRenderer;
        this.layerRenderer = layerRenderer;
    }

    public EntityLivingBase getEntity() {
        return this.entity;
    }

    public void setPost() {
        this.pre = false;
    }

    public boolean isPost() {
        return !this.pre;
    }

    public void drawModel() {
        this.modelRenderer.run();
    }

    public void drawLayers() {
        this.layerRenderer.run();
    }
}

