package dev.robin.gui;

import dev.robin.utils.client.TimeUtil;
import dev.robin.utils.render.ColorUtil;
import dev.robin.utils.render.RenderUtil;
import dev.robin.utils.render.animation.Animation;
import dev.robin.utils.render.animation.Direction;
import dev.robin.utils.render.animation.impl.DecelerateAnimation;
import dev.robin.utils.render.fontRender.RapeMasterFontManager;
import java.awt.Color;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class TextField
extends Gui {
    public RapeMasterFontManager font;
    private float xPosition;
    private float yPosition;
    private float radius = 2.0f;
    private float alpha = 1.0f;
    private float width;
    private float height;
    private float textAlpha = 1.0f;
    private Color fill = ColorUtil.tripleColor(32);
    private Color focusedTextColor = new Color(32, 32, 32);
    private Color unfocusedTextColor = new Color(130, 130, 130);
    private String text = "";
    private String backgroundText;
    private int maxStringLength = 32;
    private boolean drawingBackground = true;
    private boolean canLoseFocus = true;
    private boolean isFocused;
    private int lineScrollOffset;
    private int cursorPosition;
    private int selectionEnd;
    private final TimeUtil timerUtil = new TimeUtil();
    private final Animation scaleAnim = new DecelerateAnimation(550, 1.0);
    private final Animation cursorBlinkAnimation = new DecelerateAnimation(350, 1.0);
    private final Animation widthAnim = new DecelerateAnimation(350, 1.0);
    private boolean visible = true;

    public TextField(RapeMasterFontManager font) {
        this.font = font;
    }

    public TextField(RapeMasterFontManager font, float x2, float y2, float par5Width, float par6Height, String bgtext) {
        this.font = font;
        this.xPosition = x2;
        this.yPosition = y2;
        this.width = par5Width;
        this.height = par6Height;
        this.backgroundText = bgtext;
    }

    public TextField(RapeMasterFontManager font, float x2, float y2, float par5Width, float par6Height, float radius) {
        this.font = font;
        this.xPosition = x2;
        this.yPosition = y2;
        this.width = par5Width;
        this.height = par6Height;
        this.radius = radius;
    }

    public void setText(String text) {
        this.text = text.length() > this.maxStringLength ? text.substring(0, this.maxStringLength) : text;
        this.setCursorPositionZero();
    }

    public String getText() {
        return this.text;
    }

    public String getSelectedText() {
        int i = Math.min(this.cursorPosition, this.selectionEnd);
        int j2 = Math.max(this.cursorPosition, this.selectionEnd);
        return this.text.substring(i, j2);
    }

    public void writeText(String text) {
        int l2;
        String s2 = "";
        String s1 = ChatAllowedCharacters.filterAllowedCharacters(text);
        int min = Math.min(this.cursorPosition, this.selectionEnd);
        int max = Math.max(this.cursorPosition, this.selectionEnd);
        int len = this.maxStringLength - this.text.length() - (min - max);
        if (this.text.length() > 0) {
            s2 = s2 + this.text.substring(0, min);
        }
        if (len < s1.length()) {
            s2 = s2 + s1.substring(0, len);
            l2 = len;
        } else {
            s2 = s2 + s1;
            l2 = s1.length();
        }
        if (this.text.length() > 0 && max < this.text.length()) {
            s2 = s2 + this.text.substring(max);
        }
        this.text = s2;
        this.moveCursorBy(min - this.selectionEnd + l2);
    }

    public void deleteWords(int num) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                this.deleteFromCursor(this.getNthWordFromCursor(num) - this.cursorPosition);
            }
        }
    }

    public void deleteFromCursor(int num) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                boolean negative = num < 0;
                int i = negative ? this.cursorPosition + num : this.cursorPosition;
                int j2 = negative ? this.cursorPosition : this.cursorPosition + num;
                String s2 = "";
                if (i >= 0) {
                    s2 = this.text.substring(0, i);
                }
                if (j2 < this.text.length()) {
                    s2 = s2 + this.text.substring(j2);
                }
                this.text = s2;
                if (negative) {
                    this.moveCursorBy(num);
                }
            }
        }
    }

    public int getNthWordFromCursor(int n) {
        return this.getNthWordFromPos(n, this.getCursorPosition());
    }

    public int getNthWordFromPos(int n, int pos) {
        return this.func_146197_a(n, pos);
    }

    public int func_146197_a(int n, int pos) {
        int i = pos;
        boolean negative = n < 0;
        int j2 = Math.abs(n);
        for (int k2 = 0; k2 < j2; ++k2) {
            if (!negative) {
                int l2 = this.text.length();
                if ((i = this.text.indexOf(32, i)) == -1) {
                    i = l2;
                    continue;
                }
                while (i < l2 && this.text.charAt(i) == ' ') {
                    ++i;
                }
                continue;
            }
            while (i > 0 && this.text.charAt(i - 1) == ' ') {
                --i;
            }
            while (i > 0 && this.text.charAt(i - 1) != ' ') {
                --i;
            }
        }
        return i;
    }

    public void moveCursorBy(int p_146182_1_) {
        this.setCursorPosition(this.selectionEnd + p_146182_1_);
    }

    public void setCursorPosition(int p_146190_1_) {
        this.cursorPosition = p_146190_1_;
        int i = this.text.length();
        this.cursorPosition = MathHelper.clamp_int(this.cursorPosition, 0, i);
        this.setSelectionPos(this.cursorPosition);
    }

    public void setCursorPositionZero() {
        this.setCursorPosition(0);
    }

    public void setCursorPositionEnd() {
        this.setCursorPosition(this.text.length());
    }

    public boolean keyTyped(char cha, int keyCode) {
        if (!this.isFocused) {
            return false;
        }
        this.timerUtil.reset();
        if (GuiScreen.isKeyComboCtrlA(keyCode)) {
            this.setCursorPositionEnd();
            this.setSelectionPos(0);
            return true;
        }
        if (GuiScreen.isKeyComboCtrlC(keyCode)) {
            GuiScreen.setClipboardString(this.getSelectedText());
            return true;
        }
        if (GuiScreen.isKeyComboCtrlV(keyCode)) {
            this.writeText(GuiScreen.getClipboardString());
            return true;
        }
        if (GuiScreen.isKeyComboCtrlX(keyCode)) {
            GuiScreen.setClipboardString(this.getSelectedText());
            this.writeText("");
            return true;
        }
        switch (keyCode) {
            case 14: {
                if (GuiScreen.isCtrlKeyDown()) {
                    this.deleteWords(-1);
                } else {
                    this.deleteFromCursor(-1);
                }
                return true;
            }
            case 199: {
                if (GuiScreen.isShiftKeyDown()) {
                    this.setSelectionPos(0);
                } else {
                    this.setCursorPositionZero();
                }
                return true;
            }
            case 203: {
                if (GuiScreen.isShiftKeyDown()) {
                    if (GuiScreen.isCtrlKeyDown()) {
                        this.setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
                    } else {
                        this.setSelectionPos(this.getSelectionEnd() - 1);
                    }
                } else if (GuiScreen.isCtrlKeyDown()) {
                    this.setCursorPosition(this.getNthWordFromCursor(-1));
                } else {
                    this.moveCursorBy(-1);
                }
                return true;
            }
            case 205: {
                if (GuiScreen.isShiftKeyDown()) {
                    if (GuiScreen.isCtrlKeyDown()) {
                        this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
                    } else {
                        this.setSelectionPos(this.getSelectionEnd() + 1);
                    }
                } else if (GuiScreen.isCtrlKeyDown()) {
                    this.setCursorPosition(this.getNthWordFromCursor(1));
                } else {
                    this.moveCursorBy(1);
                }
                return true;
            }
            case 207: {
                if (GuiScreen.isShiftKeyDown()) {
                    this.setSelectionPos(this.text.length());
                } else {
                    this.setCursorPositionEnd();
                }
                return true;
            }
            case 211: {
                if (GuiScreen.isCtrlKeyDown()) {
                    this.deleteWords(1);
                } else {
                    this.deleteFromCursor(1);
                }
                return true;
            }
        }
        if (ChatAllowedCharacters.isAllowedCharacter(cha)) {
            this.writeText(Character.toString(cha));
            return true;
        }
        return false;
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean flag = RenderUtil.isHovering(this.xPosition, this.yPosition, this.width, this.height, mouseX, mouseY);
        if (this.canLoseFocus) {
            this.setFocused(flag);
        }
        if (this.isFocused && flag && mouseButton == 0) {
            float xPos = this.xPosition;
            if (this.backgroundText != null && this.backgroundText.equals("Search")) {
                xPos += 13.0f;
            }
            float i = (float)mouseX - xPos;
            String s2 = this.font.trimStringToWidth(this.text.substring(this.lineScrollOffset), (int)this.getWidth(), true);
            this.setCursorPosition(this.font.trimStringToWidth(s2, (int)i, true).length() + this.lineScrollOffset);
        }
    }

    public void drawTextBox() {
        if (this.getVisible()) {
            boolean cursorBlink;
            if (this.isFocused()) {
                Keyboard.enableRepeatEvents((boolean)true);
            }
            Color textColorWithAlpha = this.focusedTextColor;
            if (this.textAlpha != 1.0f) {
                textColorWithAlpha = ColorUtil.applyOpacity(this.focusedTextColor, this.textAlpha);
            }
            float xPos = this.xPosition + 3.0f;
            float yPos = this.yPosition + this.font.getMiddleOfBox(this.height);
            this.widthAnim.setDirection(this.isFocused() ? Direction.FORWARDS : Direction.BACKWARDS);
            if (this.isDrawingBackground()) {
                RenderUtil.drawRect(this.xPosition, this.yPosition + 13.0f, this.xPosition + this.width, this.yPosition + 14.0f, new Color(148, 148, 148).getRGB());
                RenderUtil.drawRectWH(this.xPosition, this.yPosition + 13.0f, this.widthAnim.getOutput() * (double)this.width, 1.0, new Color(255, 23, 68).getRGB());
            }
            if (this.backgroundText != null) {
                if (this.backgroundText.equals("Search")) {
                    xPos += 15.0f;
                }
                GL11.glPushMatrix();
                this.scaleAnim.setDirection(this.isFocused() ? Direction.BACKWARDS : Direction.FORWARDS);
                float out = (double)((float)this.scaleAnim.getOutput()) < 0.5 ? 0.5f : (float)this.scaleAnim.getOutput();
                int color = out < 0.7f ? new Color(197, 17, 98).getRGB() : new Color(32, 32, 32).getRGB();
                GL11.glScalef((float)out, (float)out, (float)out);
                if (this.text.equals("")) {
                    this.font.drawString(this.backgroundText, xPos / out, yPos / out, color);
                }
                GL11.glPopMatrix();
            }
            int cursorPos = this.cursorPosition - this.lineScrollOffset;
            int selEnd = this.selectionEnd - this.lineScrollOffset;
            String text = this.font.trimStringToWidth(this.text.substring(this.lineScrollOffset), (int)this.getWidth(), true);
            boolean cursorInBounds = cursorPos >= 0 && cursorPos <= 1;
            boolean canShowCursor = this.isFocused && cursorInBounds;
            float j1 = xPos;
            if (selEnd > text.length()) {
                selEnd = text.length();
            }
            if (text.length() > 0) {
                String s1 = cursorInBounds ? text.substring(0, cursorPos) : text;
                j1 = xPos + (float)this.font.drawString(s1, xPos, yPos, textColorWithAlpha.getRGB());
            }
            boolean cursorEndPos = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
            float k1 = j1;
            if (!cursorInBounds) {
                k1 = cursorPos > 0 ? xPos + this.width : xPos;
            } else if (cursorEndPos) {
                k1 = j1;
                j1 -= 1.0f;
            }
            if (text.length() > 0 && cursorInBounds && cursorPos < text.length()) {
                j1 = xPos + (float)this.font.drawString(text.substring(cursorPos), j1 + 2.0f, yPos, textColorWithAlpha.getRGB());
            }
            boolean bl = cursorBlink = this.timerUtil.hasTimeElapsed(2000L) || cursorEndPos;
            if (canShowCursor) {
                if (cursorBlink) {
                    if (this.cursorBlinkAnimation.isDone()) {
                        this.cursorBlinkAnimation.changeDirection();
                    }
                } else {
                    this.cursorBlinkAnimation.setDirection(Direction.FORWARDS);
                }
                RenderUtil.drawRectWH(k1 + 1.0f, yPos + 4.0f, 0.5, this.font.getHeight() - 4, ColorUtil.applyOpacity(textColorWithAlpha, (float)this.cursorBlinkAnimation.getOutput()).getRGB());
            }
        }
    }

    private void drawSelectionBox(float x2, float y2, float width, float height) {
        if (x2 < width) {
            float i = x2;
            x2 = width;
            width = i;
        }
        if (y2 < height) {
            float j2 = y2;
            y2 = height;
            height = j2;
        }
        if (width > this.xPosition + this.width) {
            width = this.xPosition + this.width;
        }
        if (x2 > this.xPosition + this.width) {
            x2 = this.xPosition + this.width;
        }
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.color(0.0f, 0.0f, 255.0f, 255.0f);
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(5387);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(x2, height, 0.0).endVertex();
        worldrenderer.pos(width, height, 0.0).endVertex();
        worldrenderer.pos(width, y2, 0.0).endVertex();
        worldrenderer.pos(x2, y2, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }

    public void setMaxStringLength(int len) {
        this.maxStringLength = len;
        if (this.text.length() > len) {
            this.text = this.text.substring(0, len);
        }
    }

    public int getMaxStringLength() {
        return this.maxStringLength;
    }

    public int getCursorPosition() {
        return this.cursorPosition;
    }

    public void setTextColor(Color color) {
        this.focusedTextColor = color;
    }

    public void setDisabledTextColour(Color color) {
        this.unfocusedTextColor = color;
    }

    public int getSelectionEnd() {
        return this.selectionEnd;
    }

    public float getWidth() {
        boolean flag;
        boolean bl = flag = this.backgroundText != null && this.backgroundText.equals("Search");
        return this.isDrawingBackground() ? this.width - (float)(flag ? 17 : 4) : this.width;
    }

    public float getRealWidth() {
        return this.isDrawingBackground() ? this.width - 4.0f : this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public void setSelectionPos(int selectionPos) {
        int i = this.text.length();
        if (selectionPos > i) {
            selectionPos = i;
        }
        if (selectionPos < 0) {
            selectionPos = 0;
        }
        this.selectionEnd = selectionPos;
        if (this.font != null) {
            if (this.lineScrollOffset > i) {
                this.lineScrollOffset = i;
            }
            float j2 = this.getWidth();
            String s2 = this.font.trimStringToWidth(this.text.substring(this.lineScrollOffset), (int)j2, true);
            int k2 = s2.length() + this.lineScrollOffset;
            if (selectionPos == this.lineScrollOffset) {
                this.lineScrollOffset -= this.font.trimStringToWidth(this.text, (int)j2, true).length();
            }
            if (selectionPos > k2) {
                this.lineScrollOffset += selectionPos - k2;
            } else if (selectionPos <= this.lineScrollOffset) {
                this.lineScrollOffset -= this.lineScrollOffset - selectionPos;
            }
            this.lineScrollOffset = MathHelper.clamp_int(this.lineScrollOffset, 0, i);
        }
    }

    public void setCanLoseFocus(boolean canLoseFocus) {
        this.canLoseFocus = canLoseFocus;
    }

    public boolean getVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public RapeMasterFontManager getFont() {
        return this.font;
    }

    public void setFont(RapeMasterFontManager font) {
        this.font = font;
    }

    public float getxPosition() {
        return this.xPosition;
    }

    public void setxPosition(float xPosition) {
        this.xPosition = xPosition;
    }

    public float getyPosition() {
        return this.yPosition;
    }

    public void setyPosition(float yPosition) {
        this.yPosition = yPosition;
    }

    public float getRadius() {
        return this.radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getAlpha() {
        return this.alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public Color getFill() {
        return this.fill;
    }

    public void setFill(Color fill) {
        this.fill = fill;
    }

    public boolean isDrawingBackground() {
        return this.drawingBackground;
    }

    public boolean isFocused() {
        return this.isFocused;
    }

    public void setFocused(boolean focused) {
        this.isFocused = focused;
    }

    public boolean isVisible() {
        return this.visible;
    }
}

