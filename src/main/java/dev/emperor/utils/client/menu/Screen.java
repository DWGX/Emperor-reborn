package dev.emperor.utils.client.menu;

import dev.emperor.utils.misc.MinecraftInstance;

public interface Screen
extends MinecraftInstance {
    default public void onDrag(int mouseX, int mouseY) {
    }

    public void initGui();

    public void keyTyped(char var1, int var2);

    public void drawScreen(int var1, int var2);

    public void mouseClicked(int var1, int var2, int var3);

    public void mouseReleased(int var1, int var2, int var3);
}

