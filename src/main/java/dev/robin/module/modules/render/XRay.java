package dev.robin.module.modules.render;

import com.google.common.collect.Lists;
import dev.robin.event.EventTarget;
import dev.robin.event.rendering.EventRender3D;
import dev.robin.event.world.EventTick;
import dev.robin.module.Category;
import dev.robin.module.Module;
import dev.robin.module.values.BoolValue;
import dev.robin.module.values.NumberValue;
import dev.robin.utils.client.TimeUtil;
import dev.robin.utils.render.ColorUtil;
import dev.robin.utils.render.RenderUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class XRay
extends Module {
    public static int alpha;
    public static boolean isEnabled;
    public static ArrayList<Object> blockIdList;
    public static List<BlockPos> blockPosList;
    private TimeUtil timer = new TimeUtil();
    private final NumberValue opacity = new NumberValue("Opacity", 160.0, 0.0, 255.0, 5.0);
    private static final BoolValue esp;
    private final BoolValue tracers = new BoolValue("Tracers", true);
    private final BoolValue dia = new BoolValue("Diamond", true);
    private final BoolValue rs = new BoolValue("Redstone", true);
    private final BoolValue emb = new BoolValue("Emerald", true);
    private final BoolValue lap = new BoolValue("Lapis", true);
    private final BoolValue iron = new BoolValue("Iron", true);
    private final BoolValue coal = new BoolValue("Coal", true);
    private final BoolValue gold = new BoolValue("Gold", true);
    private static final NumberValue distance;
    private final BoolValue update = new BoolValue("Chunk-Update", true);
    private final NumberValue delay = new NumberValue("Delay", 10.0, 1.0, 30.0, 0.5);

    public XRay() {
        super("XRay", Category.Render);
    }

    @Override
    public void onEnable() {
        this.onToggle(true);
    }

    @Override
    public void onDisable() {
        this.onToggle(false);
        this.timer.reset();
    }

    private void onToggle(boolean enabled) {
        blockPosList.clear();
        XRay.mc.renderGlobal.loadRenderers();
        isEnabled = enabled;
    }

    @EventTarget
    public void update(EventTick event) {
        if ((double)alpha != (Double)this.opacity.getValue()) {
            XRay.mc.renderGlobal.loadRenderers();
            alpha = ((Double)this.opacity.getValue()).intValue();
        } else if (((Boolean)this.update.getValue()).booleanValue() && this.timer.delay(1000L * ((Double)this.delay.getValue()).longValue())) {
            XRay.mc.renderGlobal.loadRenderers();
            this.timer.reset();
        }
    }

    @EventTarget
    public void onRender3D(EventRender3D e) {
        if (((Boolean)esp.getValue()).booleanValue()) {
            for (BlockPos pos : blockPosList) {
                if (!(XRay.mc.thePlayer.getDistance(pos.getX(), pos.getZ()) <= (Double)distance.getValue())) continue;
                Block block = XRay.mc.theWorld.getBlockState(pos).getBlock();
                if (block == Blocks.diamond_ore && ((Boolean)this.dia.getValue()).booleanValue()) {
                    this.render3D(pos, 0, 255, 255);
                    continue;
                }
                if (block == Blocks.iron_ore && ((Boolean)this.iron.getValue()).booleanValue()) {
                    this.render3D(pos, 225, 225, 225);
                    continue;
                }
                if (block == Blocks.lapis_ore && ((Boolean)this.lap.getValue()).booleanValue()) {
                    this.render3D(pos, 0, 0, 255);
                    continue;
                }
                if (block == Blocks.redstone_ore && ((Boolean)this.rs.getValue()).booleanValue()) {
                    this.render3D(pos, 255, 0, 0);
                    continue;
                }
                if (block == Blocks.coal_ore && ((Boolean)this.coal.getValue()).booleanValue()) {
                    this.render3D(pos, 0, 30, 30);
                    continue;
                }
                if (block == Blocks.emerald_ore && ((Boolean)this.emb.getValue()).booleanValue()) {
                    this.render3D(pos, 0, 255, 0);
                    continue;
                }
                if (block != Blocks.gold_ore || !((Boolean)this.gold.getValue()).booleanValue()) continue;
                this.render3D(pos, 255, 255, 0);
            }
        }
    }

    private void render3D(BlockPos pos, int red, int green, int blue) {
        if (((Boolean)esp.getValue()).booleanValue()) {
            RenderUtil.drawSolidBlockESP(pos, ColorUtil.getColor(red, green, blue));
        }
        if (((Boolean)this.tracers.getValue()).booleanValue()) {
            RenderUtil.drawLine(pos, ColorUtil.getColor(red, green, blue));
        }
    }

    public static boolean showESP() {
        return (Boolean)esp.getValue();
    }

    public static double getDistance() {
        return (Double)distance.getValue();
    }

    static {
        blockIdList = Lists.newArrayList((Object[])new Integer[]{10, 11, 8, 9, 14, 15, 16, 21, 41, 42, 46, 48, 52, 56, 57, 61, 62, 73, 74, 84, 89, 103, 116, 117, 118, 120, 129, 133, 137, 145, 152, 153, 154});
        blockPosList = new CopyOnWriteArrayList<BlockPos>();
        esp = new BoolValue("ESP", true);
        distance = new NumberValue("Distance", 42.0, 16.0, 256.0, 4.0);
    }
}

