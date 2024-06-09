package dev.robin.gui.ui;

import dev.robin.Client;
import dev.robin.event.EventManager;
import dev.robin.module.Module;
import dev.robin.utils.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class UiModule
extends Gui {
    public static final Minecraft mc = Minecraft.getMinecraft();
    public double moveX = 0.0;
    public double moveY = 0.0;
    public double posX;
    public double posY;
    public double width;
    public double height;
    public String name;
    public boolean state = false;

    public UiModule(String name, double posX, double posY, double width, double height) {
        this.name = name;
        this.posX = posX / (double)RenderUtil.width();
        this.posY = posY / (double)RenderUtil.height();
        this.width = width;
        this.height = height;
    }

    public <T extends Module> T getModule(Class<T> clazz) {
        return Client.instance.moduleManager.getModule(clazz);
    }

    public String getName() {
        return this.name;
    }

    public double getPosX() {
        return this.posX * (double)RenderUtil.width();
    }

    public double getPosY() {
        return this.posY * (double)RenderUtil.height();
    }

    public void setPosX(double posX) {
        this.posX = posX / (double)RenderUtil.width();
    }

    public void setPosY(double posY) {
        this.posY = posY / (double)RenderUtil.height();
    }

    public double getWidth() {
        return this.width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return this.height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setState(boolean state) {
        if (state && !this.state) {
            EventManager.register(this);
        } else if (this.state && !state) {
            EventManager.unregister(this);
        }
        this.state = state;
    }

    public void toggle() {
        this.setState(!this.state);
    }

    public boolean getState() {
        return this.state;
    }
}

