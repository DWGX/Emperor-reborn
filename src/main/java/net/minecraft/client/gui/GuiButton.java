package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiButton
extends Gui {
    protected static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");
    protected int width;
    protected int height;
    public int xPosition;
    public int yPosition;
    public String displayString;
    public int id;
    public boolean enabled = true;
    public boolean visible = true;
    protected boolean hovered;

    public GuiButton(int buttonId, int x2, int y2, String buttonText) {
        this(buttonId, x2, y2, 200, 20, buttonText);
    }

    public GuiButton(int buttonId, int x2, int y2, int widthIn, int heightIn, String buttonText) {
        this.id = buttonId;
        this.xPosition = x2;
        this.yPosition = y2;
        this.width = widthIn;
        this.height = heightIn;
        this.displayString = buttonText;
    }

    protected int getHoverState(boolean mouseOver) {
        int i = 1;
        if (!this.enabled) {
            i = 0;
        } else if (mouseOver) {
            i = 2;
        }
        return i;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            FontRenderer fontrenderer = mc.fontRendererObj;
            mc.getTextureManager().bindTexture(buttonTextures);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int i = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 66, this.width / 2, this.height);
            this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 66, this.width / 2, this.height);
            if (this.hovered && this.enabled) {
                GL11.glPushMatrix();
                GL11.glEnable(3042);
                GL11.glEnable(2848);
                GL11.glBlendFunc(770, 771);
                GL11.glDisable(3553);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glTranslatef((float)this.xPosition, (float)this.yPosition, 0.0f);
                GL11.glLineWidth(3.0f);
                GL11.glBegin(2);
                GL11.glVertex2f(0.0f, 0.0f);
                GL11.glVertex2f((float)this.width, 0.0f);
                GL11.glVertex2f((float)this.width, (float)this.height);
                GL11.glVertex2f(0.0f, (float)this.height);
                GL11.glEnd();
                GL11.glEnable(3553);
                GL11.glDisable(2848);
                GL11.glDisable(3042);
                GL11.glPopMatrix();
            }
            this.mouseDragged(mc, mouseX, mouseY);
            int j2 = 0xE0E0E0;
            if (!this.enabled) {
                j2 = 0xA0A0A0;
            } else if (this.hovered) {
                j2 = 0xFFFFA0;
            }
            GuiButton.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j2);
        }
    }

    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
    }

    public void mouseReleased(int mouseX, int mouseY) {
    }

    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
    }

    public boolean isMouseOver() {
        return this.hovered;
    }

    public void drawButtonForegroundLayer(int mouseX, int mouseY) {
    }

    public void playPressSound(SoundHandler soundHandlerIn) {
        soundHandlerIn.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0f));
    }

    public int getButtonWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}

