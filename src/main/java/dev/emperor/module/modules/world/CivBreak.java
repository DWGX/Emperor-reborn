package dev.emperor.module.modules.world;

import dev.emperor.event.EventTarget;
import dev.emperor.event.rendering.EventRender2D;
import dev.emperor.event.rendering.EventRender3D;
import dev.emperor.event.world.EventClickBlock;
import dev.emperor.event.world.EventMotion;
import dev.emperor.event.world.EventUpdate;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.modules.render.HUD;
import dev.emperor.module.values.NumberValue;
import dev.emperor.utils.client.PacketUtil;
import dev.emperor.utils.player.BlockUtil;
import dev.emperor.utils.render.AnimationUtil;
import dev.emperor.utils.render.RenderUtil;
import dev.emperor.utils.render.RoundedUtils;
import dev.emperor.utils.render.fontRender.FontManager;
import java.awt.Color;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class CivBreak
extends Module {
    private BlockPos blockPos = null;
    private EnumFacing enumFacing = null;
    private final NumberValue range = new NumberValue("Range", 5.0, 1.0, 6.0, 0.5);
    private boolean breaking = false;
    private float breakPercent = 0.0f;
    private float widthAnim = 0.0f;
    private float alphaAnim = 0.0f;
    private float moveinAnim = 0.0f;
    private boolean canBreak = false;

    public CivBreak() {
        super("CivBreak", Category.Player);
    }

    @EventTarget
    public void onBlockClick(EventClickBlock event) {
        this.breaking = true;
        this.blockPos = event.getClickedBlock();
        this.enumFacing = event.getEnumFacing();
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (this.blockPos == null || this.enumFacing == null) {
            return;
        }
        this.canBreak = this.breakPercent * 50.0f >= 100.0f ? BlockUtil.getCenterDistance(this.blockPos) < (Double)this.range.getValue() : false;
        if (this.canBreak) {
            PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.blockPos, this.enumFacing));
            this.blockPos = null;
            this.enumFacing = null;
            this.breaking = false;
            this.breakPercent = 0.0f;
        }
        if (this.breaking) {
            this.breakPercent += CivBreak.mc.theWorld.getBlockState(this.blockPos).getBlock().getPlayerRelativeBlockHardness(CivBreak.mc.thePlayer, CivBreak.mc.theWorld, this.blockPos);
        }
    }

    @EventTarget
    public void onRender2D(EventRender2D event) {
        ScaledResolution sr = new ScaledResolution(mc);
        if (this.breaking) {
            float progress = Math.min(this.breakPercent / CivBreak.mc.theWorld.getBlockState(this.blockPos).getBlock().getBlockHardness(CivBreak.mc.theWorld, this.blockPos), 1.0f);
            String string = String.format("%.1f", Float.valueOf(progress * 100.0f)) + "%";
            float x2 = (float)(sr.getScaledWidth() / 2) - 72.0f - (float)FontManager.arial16.getStringWidth("100.0%") + 140.0f - 36.0f;
            FontManager.arial16.drawCenteredStringWithShadow(string, x2, (float)sr.getScaledHeight() - 70.0f + 2.0f, -1);
            this.widthAnim = AnimationUtil.animateSmooth(this.widthAnim, progress * 140.0f, 8.0f / (float)mc.getDebugFPS());
            this.moveinAnim = AnimationUtil.animateSmooth(this.moveinAnim, 18.0f, 4.0f / (float)mc.getDebugFPS());
            this.alphaAnim = AnimationUtil.animateSmooth(this.alphaAnim, 255.0f, 2.0f / (float)mc.getDebugFPS());
        } else {
            this.widthAnim = AnimationUtil.animateSmooth(this.widthAnim, 0.0f, 8.0f / (float)mc.getDebugFPS());
            this.moveinAnim = AnimationUtil.animateSmooth(this.moveinAnim, 0.0f, 4.0f / (float)mc.getDebugFPS());
            this.alphaAnim = AnimationUtil.animateSmooth(this.alphaAnim, 0.0f, 2.0f / (float)mc.getDebugFPS());
        }
        RoundedUtils.drawRound((float)(sr.getScaledWidth() / 2) - 72.0f, (float)(sr.getScaledHeight() - 60 - 10) - this.moveinAnim, this.widthAnim, 6.0f, 3.0f, RenderUtil.reAlpha(HUD.color(8), (int)this.alphaAnim));
    }

    @EventTarget
    public void onRender3D(EventRender3D event) {
        RenderUtil.drawBlockBox(this.blockPos, Color.WHITE, true);
    }

    @EventTarget
    public void onMotion(EventMotion event) {
        if (event.isPost() && this.breaking) {
            PacketUtil.sendPacketC0F();
            PacketUtil.sendPacketNoEvent(new C0APacketAnimation());
        }
    }
}

