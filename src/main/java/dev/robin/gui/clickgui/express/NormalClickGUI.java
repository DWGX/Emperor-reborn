package dev.robin.gui.clickgui.express;

import dev.robin.Client;
import dev.robin.module.Category;
import dev.robin.module.Module;
import dev.robin.module.ModuleManager;
import dev.robin.module.modules.render.HUD;
import dev.robin.module.values.BoolValue;
import dev.robin.module.values.ColorValue;
import dev.robin.module.values.ModeValue;
import dev.robin.module.values.NumberValue;
import dev.robin.module.values.Value;
import dev.robin.utils.render.AnimationUtil;
import dev.robin.utils.render.RenderUtil;
import dev.robin.utils.render.fontRender.FontManager;
import java.awt.Color;
import java.io.IOException;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class NormalClickGUI extends GuiScreen
{
    public static Category currentModuleType;
    public static Module currentModule;
    public float startX = (RenderUtil.width() / 2f) - 210f;
    public float startY = -320;

    public double alphaAnimation = 255;
    public double scaleAnimation = 100;
    public static int moduleStart;
    public static int valueStart;
    boolean previousmouse = true;
    boolean Rpreviousmouse = true;
    boolean mouse;
    public float moveX = 0.0F;
    public float moveY = 0.0F;

    public float lastPercent;
    public float percent;
    public float percent2;
    public float lastPercent2;

    public int mouseWheel;

    public int mouseX;
    public int mouseY;

    static double roller;
    double moduleAnimY = 29.5f;
    double valueAnimY = 30f;
    static double moduleAnim = 0f;
    static double valueAnim = 0f;

    //for animation on clickGui open
    static double animationStartX;
    static double animationStartY;

    private final AnimationUtil animationUtil = new AnimationUtil();

    static {
        currentModuleType = Category.Combat;
        currentModule = ModuleManager.getModulesInType(currentModuleType).size() != 0
                ? ModuleManager.getModulesInType(currentModuleType).get(0)
                : null;
        animationStartX = 100.0F;
        animationStartY = 100.0F;
        roller = 25;
        moduleStart = 0;

        valueStart = 0;
    }
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        animationUtil.resetTime();
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        lastPercent = percent;
        lastPercent2 = percent2;
        if (percent > .98) {
            percent += ((.98 - percent) / (1.45f)) - 0.001;
        }
        if (percent <= .98) {
            if (percent2 < 1) {
                percent2 += ((1 - percent2) / (2.8f)) + 0.002;
            }
        }
        //open clickGui animation
        // for xy
        if(scaleAnimation != 100) {
            startX = (float)animationStartX;
            startY = (float) animationUtil.animateNoFast(startY, animationStartY, 10);
        }else{
            startX = (float)animationStartX;
            startY = (float)animationStartY;
        }

        // for alpha
        alphaAnimation = animationUtil.animateNoFast(alphaAnimation,0,8);
        // for scale
        scaleAnimation = animationUtil.animateNoFast(scaleAnimation,100,10);
        float endX = startX + 420;
        float endY = startY + 320;

        float xAmount = endX - (420 / 2f);
        float yAmount = endY - (320 / 2f);

        float finalXAnim = (float) (xAmount - scaleAnimation / 100f * xAmount);
        float finalYAnim = (float) (yAmount - scaleAnimation / 100f * yAmount);
        GL11.glTranslatef(finalXAnim, finalYAnim, 0);
        GL11.glScalef((float) (scaleAnimation/100), (float) (scaleAnimation/100), 0);
        //check is clickGui move
        if (this.isHovered(startX, startY - 25.0F, startX + 400.0F, startY + 25.0F, mouseX, mouseY)
                && org.lwjgl.input.Mouse.isButtonDown(0)) {
            if (this.moveX == 0.0F && this.moveY == 0.0F) {
                this.moveX = (float) mouseX - startX;
                this.moveY = (float) mouseY - startY;
            } else {
                animationStartX = (float) mouseX - this.moveX;
                animationStartY = (float) mouseY - this.moveY;
            }

            this.previousmouse = true;
        } else if (this.moveX != 0.0F || this.moveY != 0.0F) {
            this.moveX = 0.0F;
            this.moveY = 0.0F;
        }



        //main rect draw
        // main rect
        RenderUtil.drawRect(startX-40, startY, startX + 420.0F, startY + 320.0F,
                getColor(250, 250, 250, 255));
        // middle rect
        RenderUtil.drawRect(startX + 60.0F, startY, startX + 200.0F, startY + 320.0F,
                (getColor(240,240,240,255)));
        // right rect
        RenderUtil.drawRect(startX + 200.0F, startY, startX + 420.0F, startY + 320.0F,
                (getColor(230,230,230,255)));
        // up rect
        RenderUtil.drawRect(startX + 60, startY, startX + 200f, startY+25f,
                getColor(230, 230, 230, 255));
        // main shadow
        RenderUtil.drawShadow(startX-40,startY,startX+420,startY+320);

        //Client name
        FontManager.Tahoma18.drawString("Xiaoda Client",startX- 18f, (float) (startY + 4 + FontManager.Tahoma18.getStringHeight()),getColor(100,100,100,255));

        //draw category icon
        Category[] mY = Category.values();
        for (int m = 0; m < Category.values().length; ++m) {
            int addX = 0;

            if (mY[m] != currentModuleType) {
                FontManager.arial18.drawString(mY[m].toString(),(int)startX+2+ addX,(int)startY + 32+FontManager.arial18.getStringWidth("1") + m * 42,getColor(80,80,80,255));
//				dev.robin.utils.render.RenderUtil.drawRect(startX + 10F, startY + 25.0F + (float) (m * 42),startX + 48f, startY+50f + (float) (m * 42),getColor(0,0,0,60));
            } else {
                addX = 5;
                FontManager.arial20.drawString(mY[m].toString(),(int)startX+2+ addX,(int)startY + 32+FontManager.arial18.getStringWidth("1") + m * 42,getColor(80,80,80,255));
                RenderUtil.drawRect((int)startX-28 + addX, (int)startY + 32 + m * 42,(int)startX-26 + addX, (int)startY + 48 + m * 42, HUD.mainColor.getColor());
            }
            RenderUtil.drawImage(new ResourceLocation("express/icon/clickgui/" + mY[m].toString() + ".png"),(int)startX-18 + addX, (int)startY + 32 + m * 42 , 16, 16, new Color(80,80,80).getRGB());

            try {
                if (this.isCategoryHovered(startX - 20.0F, startY + 32.0F + (float) (m * 42), startX + 56.0F, startY + 48.0F + (float) (m * 42), mouseX, mouseY) && org.lwjgl.input.Mouse.isButtonDown(0)) {
                    currentModuleType = mY[m];
                    currentModule = ModuleManager.getModulesInType(currentModuleType).size() != 0
                            ? (Module) ModuleManager.getModulesInType(currentModuleType).get(0)
                            : null;
                    moduleStart = 0;
                    moduleAnim = 0;
                }
            } catch (Exception var23) {
                System.err.println(var23);
            }
        }

        //get moduleStart and valueStart

        //Draw Sli1
        double moduleSize = ModuleManager.getModulesInType(currentModuleType).size()-1;

        mouseWheel = org.lwjgl.input.Mouse.getDWheel();
        if (this.isCategoryHovered(startX + 60.0F, startY, startX + 200.0F, startY + 320.0F, mouseX, mouseY)) {
//			if (mouseWheel < 0 && moduleStart < ModuleManager.getModulesInType(currentModuleType).size()-1) {
//				++moduleStart;
//			}
            if (mouseWheel < 0 && (235 / moduleSize * (moduleStart * (25f/25f))) < 235) {
                ++moduleStart;
            }



            if (mouseWheel > 0 && moduleStart > 0) {
                --moduleStart;
            }
        }

        if (this.isCategoryHovered(startX + 200.0F, startY, startX + 420.0F, startY + 320.0F, mouseX, mouseY)) {
            if (mouseWheel < 0 && valueStart < currentModule.getValues().size()) {
                ++valueStart;
            }

            if (mouseWheel > 0 && valueStart > 0) {
                --valueStart;
            }
        }
        //check is need anim
        moduleAnim = -moduleStart;
        //set animation
        if(moduleAnimY != moduleAnim * -25f)
            moduleAnimY = animationUtil.animateNoFast(moduleAnimY,(-moduleAnim)*25f,40);
        float moreAddY =(float)(-moduleAnimY);
        //check is need anim
        valueAnim = -moduleStart;
        //set animation
        if(valueAnimY != valueAnim * -20)
            valueAnimY = animationUtil.animateNoFast(moduleAnimY,(-moduleAnim)*25,40);
        float valueMoreAddY =(float)(-moduleAnimY);


        if(ModuleManager.getModulesInType(currentModuleType).size() > 1) {
            roller = Math.min(235f, animationUtil.animateNoFast(roller, (235 / moduleSize * (moduleStart * (25f/25f))), 15)) ;
            RenderUtil.drawRect(startX + 199f, (float) (startY + 25d + roller), startX + 201f, (float) (startY + 25d + 60d + roller), getColor(160, 160, 160, 255));
        }else
            RenderUtil.drawRect(0,0,0,0,Color.white.getRGB());

        //Draw Type Name
        FontManager.Tahoma16.drawString(
                currentModuleType.toString(),
                startX + 70.0F, startY + 12.5F, getColor(80, 80, 80,255));

        //Draw Modules!
        if (currentModule != null) {
            float CStartY = 29F;

            FontManager.Tahoma20.drawString(currentModule.name,startX+210,startY + 12.5f ,getColor(60,60,60,255) );


            RenderUtil.startGlScissor((int) (startX + 60), (int) (startY + 38f), (int) (startX + 200), (int) (startY + 300f));

            //draw module selector
//			HelperUtil.sendMessage(moduleAnim + " ");
            for (int i = 0; i < ModuleManager.getModulesInType(currentModuleType).size(); ++i) {
                Module value = (Module) ModuleManager.getModulesInType(currentModuleType).get(i);

                RenderUtil.drawRoundRect(startX+195, CStartY + startY + moreAddY, startX+65, CStartY +20 + startY + moreAddY, getColor(60,60,60,255));
//				if (i >= moduleStart || i>= moduleStart + moduleAnim) {

                if (!value.state) {
                    RenderUtil.drawCircle(startX + 74f, (float)(CStartY +10 + startY) + moreAddY, 2, new Color(120, 120, 120 - (int)(alphaAnimation/2.125)).getRGB());
                } else {
                    RenderUtil.drawCircle(startX + 74f, (float)(CStartY +10 + startY) + moreAddY, 2, new Color(81, 161, 255 - (int)alphaAnimation).getRGB());
                }

                //Button XY
                if (this.isSettingsButtonHovered(startX + 67F, CStartY +2 + startY + moreAddY,
                        startX + 183.0F,
                        CStartY + 16.0F + (float) FontManager.Tahoma20.getHeight() + startY + moreAddY, mouseX, mouseY)) {
                    if (!this.previousmouse && org.lwjgl.input.Mouse.isButtonDown(0)) {
                        if (value.state) {
                            value.setState(false);
                        } else {
                            value.setState(true);
                        }

                        this.previousmouse = true;
                    }

                    if (!this.previousmouse && org.lwjgl.input.Mouse.isButtonDown(1)) {
                        this.previousmouse = true;

                    }
                    FontManager.Tahoma16.drawString(value.getName(), startX + 86.0F, CStartY + 8.0F + startY + moreAddY,
                            (new Color(120, 120, 120, 255 - (int)alphaAnimation)).getRGB());

                }else
                    FontManager.Tahoma16.drawString(value.getName(), startX + 86.0F, CStartY + 8.0F + startY + moreAddY,
                            (getColor(248, 248, 248, 255)));

                if (!org.lwjgl.input.Mouse.isButtonDown(0)) {
                    this.previousmouse = false;
                }


                if (this.isSettingsButtonHovered(startX + 90.0F, CStartY + startY + moreAddY,
                        startX + 100.0F + (float) FontManager.Tahoma20.getStringWidth(value.getName()),
                        CStartY + 8.0F + (float) FontManager.Tahoma20.getHeight() + startY + moreAddY, mouseX, mouseY)
                        && org.lwjgl.input.Mouse.isButtonDown(1)) {
                    currentModule = value;
                    this.valueStart = 0;
                }
                FontManager.Tahoma20.drawString(":",startX+185, CStartY + 10 - FontManager.Tahoma20.getHeight()/2 + startY + moreAddY,new Color(255,255,255,255 - (int)alphaAnimation).getRGB() );
                if(i>=moduleStart + moduleAnim)
                    CStartY += 25.0F;
//				}

            }

            RenderUtil.stopGlScissor();

            CStartY = startY + 30.0F;
            if (currentModule.getValues().isEmpty()) {
                FontManager.Tahoma18.drawCenteredString("No settings", startX + 310.0F, startY + 150, getColor(60,60,60,255));
            }



            for (int i = 0; i < currentModule.getValues().size() && CStartY <= startY + 300.0F; ++i) {
                if (i >= valueStart) {
                    Value cValue = currentModule.getValues().get(i);
                    float x;

                    //draw number value
                    if (cValue instanceof NumberValue) {
                        x = startX + 295.0F;
                        double current = (double) (62.0F
                                * (((Number) ((NumberValue) cValue).getValue()).floatValue()
                                - ((NumberValue) cValue).getMin().floatValue())
                                / (((NumberValue) cValue).getMax().floatValue()
                                - ((NumberValue) cValue).getMin().floatValue()));

                        RenderUtil.drawRect(x - 4.0F, CStartY +1, (float) ((double) x + 69.0D), CStartY + 4.0F,
                                (new Color(50, 50, 50, 255 - (int)alphaAnimation)).getRGB());
                        RenderUtil.drawBorderedRect(x + 75.0F, CStartY -3, (float) ((double) x + 100.0D), CStartY + 9.0F,1,
                                (new Color(85, 85, 85, 255 - (int)alphaAnimation)).getRGB(),(new Color(55, 55, 55, 255 - (int)alphaAnimation)).getRGB());

                        RenderUtil.drawRect(x - 4.0F, CStartY +1, (float) ((double) x + current + 0.5), CStartY +4.0F,
                                (new Color(61, 141, 255, 255 - (int)alphaAnimation)).getRGB());
                        RenderUtil.drawRect((float) ((double) x + current + 2.0D), CStartY,
                                (float) ((double) x + current + 7.0D), CStartY + 5.0F,
                                (new Color(100, 100, 100, 255 - (int)alphaAnimation)).getRGB());
                        FontManager.Tahoma18.drawString(cValue.getName(),
                                startX + 210.0F, CStartY, new Color(100,100,100,255 - (int)alphaAnimation).getRGB());

                        if (!org.lwjgl.input.Mouse.isButtonDown(0)) {
                            this.previousmouse = false;
                        }
                        if((((Number) cValue.getValue()).doubleValue() - (double) (((Number) cValue.getValue()).intValue())) == 0.0d)
                            FontManager.Tahoma18.drawCenteredStringWithShadow(String.valueOf(((Number) cValue.getValue()).intValue()),x + 67+20f, CStartY, new Color(255,255,255,255 - (int)alphaAnimation).getRGB());
                        else
                            FontManager.Tahoma18.drawCenteredStringWithShadow(String.valueOf(((Number) cValue.getValue())),x + 67+20f, CStartY, new Color(255,255,255,255 - (int)alphaAnimation).getRGB());

                        if (this.isButtonHovered(x, CStartY - 2.0F, x + 100.0F, CStartY + 7.0F, mouseX, mouseY)
                                && org.lwjgl.input.Mouse.isButtonDown(0)) {
                            if (!this.previousmouse && org.lwjgl.input.Mouse.isButtonDown(0)) {
                                current = ((NumberValue) cValue).getMin();
                                double max = ((NumberValue) cValue).getMax();
                                double inc = ((NumberValue) cValue).getInc();
                                double valAbs = (double) mouseX - ((double) x + 1.0D);
                                double perc = valAbs / 64.0D;
                                perc = Math.min(Math.max(0.0D, perc), 1.0D);
                                double valRel = (max - current) * perc;
                                double val = current + valRel;
                                val = (double) Math.round(val * (1.0D / inc)) / (1.0D / inc);
                                ((NumberValue) cValue).setValue(Double.valueOf(val));
                            }

                            if (!org.lwjgl.input.Mouse.isButtonDown(0)) {
                                this.previousmouse = false;
                            }
                        }

                        CStartY += 25.0F;
                    }

                    //draw Option Value
                    if (cValue instanceof BoolValue) {
                        x = startX + 317.0F;
                        FontManager.Tahoma18.drawString(cValue.getName(), startX + 210.0F, CStartY,  new Color(100,100,100,255 - (int)alphaAnimation).getRGB());
                        RenderUtil.drawRoundedRect(x + 56.0F, CStartY,x + 76.0F, CStartY + 8.0F, 3,new Color(60, 60, 60, 255 - (int)alphaAnimation).getRGB());
                        if ((Boolean) cValue.getValue()) {
                            RenderUtil.drawCircle(x + 70.5, CStartY + 4,4,new Color(61,141,255 - (int)alphaAnimation).getRGB());
                        } else {
                            RenderUtil.drawCircle(x + 62, CStartY + 4,4,new Color(120,120,120 - (int)(alphaAnimation / 2.125)).getRGB());
                        }

                        if (this.isCheckBoxHovered(x + 56.0F, CStartY, x + 76.0F, CStartY + 9.0F, mouseX, mouseY)) {
                            if (!this.previousmouse && org.lwjgl.input.Mouse.isButtonDown(0)) {
                                this.previousmouse = true;
                                this.mouse = true;
                            }

                            if (this.mouse) {
                                cValue.setValue(Boolean.valueOf(!((Boolean) cValue.getValue()).booleanValue()));
                                this.mouse = false;
                            }
                        }

                        if (!org.lwjgl.input.Mouse.isButtonDown(0)) {
                            this.previousmouse = false;
                        }

                        CStartY += 25.0F;
                    }

                    //Draw mode value
                    if (cValue instanceof ModeValue) {
                        x = startX + 300.0F;
                        Enum mode = (Enum) ((ModeValue) cValue).getValue();

                        int next_str = mode.ordinal() - 1 < 0 ? ((ModeValue) cValue).getModes().length - 1
                                : mode.ordinal() - 1;
                        int next_str_2 = mode.ordinal() + 1 >= ((ModeValue) cValue).getModes().length ? 0
                                : mode.ordinal() + 1;

                        FontManager.Tahoma18.drawString(cValue.getName(), startX + 210.0F, CStartY +2, getColor(100,100,100,255));
                        RenderUtil.drawRect(x - 10.0F, CStartY - 5.0F, x + 95.0F, CStartY + 15.0F,
                                (new Color(56, 56, 56, 255 - (int)alphaAnimation)).getRGB());
                        RenderUtil.drawBorderRect((double) (x - 10.0F), (double) (CStartY - 5.0F), (double) (x + 95.0F),
                                (double) (CStartY + 15.0F),
                                (new Color(60, 60, 60, 255 - (int)alphaAnimation)).getRGB(), 2.0D);
                        for(int i1=0; i1<2; i1++){
                            FontManager.Tahoma18
                                    .drawStringWithShadow(((ModeValue) cValue).getModes()[next_str].name().substring(((ModeValue)cValue).getModes()[next_str].name().length() - 2 + i1,((ModeValue) cValue).getModes()[next_str].name().length() + i1 - 1), x - 3 + i1 * FontManager.Tahoma18.getStringWidth(((ModeValue)cValue).getModes()[next_str].name().substring(((ModeValue)cValue).getModes()[next_str].name().length() - 3 + i1,((ModeValue) cValue).getModes()[next_str].name().length() + i1 - 2))  , CStartY +2, new Color(255,255,255,80/(3 - (i1+1))).getRGB());
                            FontManager.Tahoma18
                                    .drawStringWithShadow(((ModeValue) cValue).getModes()[next_str_2].name().substring(i1,i1+1), x + 79 + ((i1>0) ? (i1 * FontManager.Tahoma18.getStringWidth(((ModeValue) cValue).getModes()[next_str_2].name().substring(i1-1,i1))):0), CStartY +2,  new Color(255,255,255,80/(i1+1)).getRGB());
                        }


                        FontManager.Tahoma18
                                .drawStringWithShadow(((Enum<?>) ((ModeValue) cValue).getValue()).name(), x + 42.5F - (float) (FontManager.Tahoma18.getStringWidth(((Enum<?>) ((ModeValue)cValue).getValue()).name()) / 2),
                                        CStartY +2,  new Color(255,255,255,255 - (int)alphaAnimation).getRGB());
                        if (this.isStringHovered(x, CStartY - 5.0F, x + 100.0F, CStartY + 15.0F, mouseX, mouseY)) {
                            if(!previousmouse) {
                                if (org.lwjgl.input.Mouse.isButtonDown(0)) {
                                    int next = mode.ordinal() + 1 >= ((ModeValue) cValue).getModes().length ? 0
                                            : mode.ordinal() + 1;
                                    cValue.setValue(((ModeValue) cValue).getModes()[next]);
                                    this.previousmouse = true;
                                }
                            }
                            if (org.lwjgl.input.Mouse.isButtonDown(1) && !Rpreviousmouse) {
                                int next = mode.ordinal() - 1 < 0 ? ((ModeValue) cValue).getModes().length - 1
                                        : mode.ordinal() - 1;
                                cValue.setValue(((ModeValue) cValue).getModes()[next]);
                                this.Rpreviousmouse = true;
                            }


                            if (!org.lwjgl.input.Mouse.isButtonDown(0)) {
                                this.previousmouse = false;
                            }
                            if (!org.lwjgl.input.Mouse.isButtonDown(1)) {
                                this.Rpreviousmouse = false;
                            }
                        }

                        CStartY += 25.0F;
                    }

                    //draw ColorEditBox
                    //by Fadouse
                    if(cValue instanceof ColorValue){
                        //start xy
                        x = startX + 250f;

                        Color lastColor = RenderUtil.getColor(((ColorValue) cValue).getColor());
                        float[] color = Color.RGBtoHSB(lastColor.getRed(), lastColor.getGreen(), lastColor.getBlue(), null);
                        double[] selectXY = new double[]{ 144 - 144 * color[1],70 - 70 * color[2]};
                        double selectX = color[0] * 144f;

                        //prepare for draw color select rect
                        GL11.glPushMatrix();

                        GL11.glEnable(GL11.GL_BLEND);
                        GL11.glDisable(GL11.GL_TEXTURE_2D);
                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        GL11.glEnable(GL11.GL_LINE_SMOOTH);
                        GL11.glDisable(GL11.GL_CULL_FACE);
                        GL11.glShadeModel(7425);

                        RenderUtil.glColor(new Color(60,60,60,255).getRGB());
                        RenderUtil.quickDrawRect(x-1, CStartY + 79, x + 145.8f,CStartY + 91);

                        //draw color select rect
                        for (int H = 0; H <= 360; H+= 2){
                            GL11.glBegin(GL11.GL_POLYGON);
                            RenderUtil.glColor(Color.HSBtoRGB(H/360F,1,1));
                            GL11.glVertex2d(x+(H/2.5f),CStartY+80);
                            RenderUtil.glColor(Color.HSBtoRGB((H)/360F,1,1));
                            GL11.glVertex2d(x+(H/2.5f),CStartY+90);
                            RenderUtil.glColor(Color.HSBtoRGB((H+1)/360F,1,1));
                            GL11.glVertex2d(x+(H/2.5f)+2/2.5f,CStartY+90);
                            RenderUtil.glColor(Color.HSBtoRGB((H+1)/360F,1,1));
                            GL11.glVertex2d(x+(H/2.5f)+2/2.5f,CStartY+80);
                            GL11.glEnd();
                        }

                        //end draw
                        GL11.glShadeModel(7424);
                        GL11.glEnable(GL11.GL_TEXTURE_2D);
                        GL11.glEnable(GL11.GL_CULL_FACE);
                        GL11.glDisable(GL11.GL_BLEND);
                        GL11.glDisable(GL11.GL_LINE_SMOOTH);

                        GL11.glPopMatrix();

                        //get the HUE

                        if (this.isButtonHovered(x, CStartY + 80, x + 144.0F, CStartY + 90.0F, mouseX, mouseY)
                                && org.lwjgl.input.Mouse.isButtonDown(0)) {
                            if (!this.previousmouse && org.lwjgl.input.Mouse.isButtonDown(0)) {
                                selectX = mouseX - x;
                            }
                            if (!org.lwjgl.input.Mouse.isButtonDown(0)) {
                                this.previousmouse = false;
                            }
                        }

                        color = new float[]{(float) ((selectX) / 144f), 1, 1};

                        //draw HUE rect
                        drawRect(selectX + x-0.25f,CStartY+79.5f,selectX + x+0.25f,CStartY+90.5f,new Color(60,60,60).getRGB());

                        //prepare for draw color select rect
                        GL11.glPushMatrix();

                        GL11.glEnable(GL11.GL_BLEND);
                        GL11.glDisable(GL11.GL_TEXTURE_2D);
                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        GL11.glEnable(GL11.GL_LINE_SMOOTH);
                        GL11.glDisable(GL11.GL_CULL_FACE);
                        GL11.glShadeModel(7425);

                        //draw color select rect
                        for(int s = 0; s<= 100; s++){
                            GL11.glBegin(GL11.GL_POLYGON);
                            RenderUtil.glColor(Color.getHSBColor(color[0],(100 - s)/100f,1).getRGB());
                            GL11.glVertex2d(x+s*1.44f,CStartY);
                            RenderUtil.glColor(Color.getHSBColor(color[0],(100 - s)/100f,0).getRGB());
                            GL11.glVertex2d(x+s*1.44f,CStartY+70);
                            RenderUtil.glColor(Color.getHSBColor(color[0],(100 - s)/100f,0).getRGB());
                            GL11.glVertex2d(x+s*1.44f+1.44f,CStartY+70);
                            RenderUtil.glColor(Color.getHSBColor(color[0],(100 - s)/100f,1).getRGB());
                            GL11.glVertex2d(x+s*1.44f+1.44f,CStartY);
                            GL11.glEnd();
                        }

                        //end draw
                        GL11.glShadeModel(7424);
                        GL11.glEnable(GL11.GL_TEXTURE_2D);
                        GL11.glEnable(GL11.GL_CULL_FACE);
                        GL11.glDisable(GL11.GL_BLEND);
                        GL11.glDisable(GL11.GL_LINE_SMOOTH);

                        GL11.glPopMatrix();

                        //Get Saturation and Bright

                        if (this.isButtonHovered(x, CStartY, x + 144F, CStartY + 70.0F, mouseX, mouseY)
                                && org.lwjgl.input.Mouse.isButtonDown(0)) {
                            if (!this.previousmouse && org.lwjgl.input.Mouse.isButtonDown(0)) {
                                selectXY = new double[]{mouseX - x, mouseY - CStartY};
                            }
                            if (!Mouse.isButtonDown(0)) {
                                this.previousmouse = false;
                            }
                        }
                        color = new float[]{color[0], (float) ((144f-selectXY[0])/144f), (float) ((70-selectXY[1])/70f)};

                        drawRect(x+selectXY[0]-1f,CStartY+selectXY[1]-1f,x+selectXY[0]+1f,CStartY+selectXY[1]+1f,new Color(0,0,0).getRGB());
                        drawRect(x+selectXY[0]-0.5f,CStartY+selectXY[1]-0.5f,x+selectXY[0]+0.5f,CStartY+selectXY[1]+0.5f,new Color(200,200,200).getRGB());

                        ((ColorValue)cValue).setColor(Color.getHSBColor(color[0],color[1],color[2]).getRGB());
                        CStartY += 100;
                    }
                }
            }
        }
        GL11.glScalef(1 / (float)(scaleAnimation/100), 1 / (float)(scaleAnimation/100), 0);
        GL11.glTranslatef(-finalXAnim, -finalYAnim, 0);

    }


    public boolean isStringHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
        return (float) mouseX >= f && (float) mouseX <= g && (float) mouseY >= y && (float) mouseY <= y2;
    }

    public boolean isSettingsButtonHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
        return (float) mouseX >= x && (float) mouseX <= x2 && (float) mouseY >= y && (float) mouseY <= y2;
    }

    public boolean isButtonHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
        return (float) mouseX >= f && (float) mouseX <= g && (float) mouseY >= y && (float) mouseY <= y2;
    }

    public boolean isCheckBoxHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
        return (float) mouseX >= f && (float) mouseX <= g && (float) mouseY >= y && (float) mouseY <= y2;
    }

    public boolean isCategoryHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
        return (float) mouseX >= x && (float) mouseX <= x2 && (float) mouseY >= y && (float) mouseY <= y2;
    }

    public boolean isHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
        return (float) mouseX >= x && (float) mouseX <= x2 && (float) mouseY >= y && (float) mouseY <= y2;
    }

    public int getColor(int red,int green, int blue, int alpha){
        return new Color(red,green,blue,(int) Math.max(0,Math.min(alpha - (alphaAnimation * (alpha/255f)),255))).getRGB();
    }
}
