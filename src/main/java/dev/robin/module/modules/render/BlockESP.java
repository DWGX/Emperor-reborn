package dev.robin.module.modules.render;

import dev.robin.event.EventTarget;
import dev.robin.event.rendering.EventRender3D;
import dev.robin.event.world.EventUpdate;
import dev.robin.module.Category;
import dev.robin.module.Module;
import dev.robin.module.values.BoolValue;
import dev.robin.module.values.ColorValue;
import dev.robin.module.values.ModeValue;
import dev.robin.module.values.NumberValue;
import dev.robin.utils.client.TimeUtil;
import dev.robin.utils.player.BlockUtil;
import dev.robin.utils.render.ColorUtil;
import dev.robin.utils.render.RenderUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class BlockESP
extends Module {
    private final ModeValue<renderMode> modeValue = new ModeValue("Mode", (Enum[])renderMode.values(), (Enum)renderMode.Box);
    private final NumberValue blockValue = new NumberValue("BlockID", 3.0, 1.0, 168.0, 1.0);
    private final NumberValue radiusValue = new NumberValue("Radius", 40.0, 5.0, 120.0, 1.0);
    public ColorValue renderColor = new ColorValue("RenderColor", Color.WHITE.getRGB());
    private final BoolValue colorRainbow = new BoolValue("Rainbow", false);
    private final TimeUtil searchTimer = new TimeUtil();
    private final List<BlockPos> posList = new ArrayList<BlockPos>();
    private Thread thread;

    public BlockESP() {
        super("BlockESP", Category.Render);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (this.searchTimer.delay(1000.0f) && (this.thread == null || !this.thread.isAlive())) {
            int radius = ((Double)this.radiusValue.getValue()).intValue();
            Block selectedBlock = Block.getBlockById(((Double)this.blockValue.getValue()).intValue());
            if (selectedBlock == Blocks.air) {
                return;
            }
            this.thread = new Thread(() -> {
                ArrayList<BlockPos> blockList = new ArrayList<BlockPos>();
                for (int x2 = -radius; x2 < radius; ++x2) {
                    for (int y2 = radius; y2 > -radius; --y2) {
                        for (int z = -radius; z < radius; ++z) {
                            int xPos = (int)BlockESP.mc.thePlayer.posX + x2;
                            int yPos = (int)BlockESP.mc.thePlayer.posY + y2;
                            int zPos = (int)BlockESP.mc.thePlayer.posZ + z;
                            BlockPos blockPos = new BlockPos(xPos, yPos, zPos);
                            Block block = BlockUtil.getBlock(blockPos);
                            if (block != selectedBlock) continue;
                            blockList.add(blockPos);
                        }
                    }
                }
                this.searchTimer.reset();
                List<BlockPos> list = this.posList;
                synchronized (list) {
                    this.posList.clear();
                    this.posList.addAll(blockList);
                }
            }, "BlockESP-BlockFinder");
            this.thread.start();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @EventTarget
    public void onRender3D(EventRender3D event) {
        List<BlockPos> list = this.posList;
        synchronized (list) {
            Color color = (Boolean)this.colorRainbow.getValue() != false ? ColorUtil.rainbow() : RenderUtil.getColor((Integer)this.renderColor.getValue());
            for (BlockPos blockPos : this.posList) {
                switch ((renderMode)((Object)this.modeValue.getValue())) {
                    case Box: {
                        RenderUtil.drawBlockBox(blockPos, color, false);
                        break;
                    }
                    case TwoD: {
                        RenderUtil.draw2D(blockPos, color.getRGB(), Color.BLACK.getRGB());
                        break;
                    }
                    case Outline: {
                        RenderUtil.drawBlockBox(blockPos, color, false);
                        RenderUtil.renderOne();
                        RenderUtil.drawBlockBox(blockPos, color, false);
                        RenderUtil.renderTwo();
                        RenderUtil.drawBlockBox(blockPos, color, false);
                        RenderUtil.renderThree();
                        RenderUtil.renderFour(color.getRGB());
                        RenderUtil.drawBlockBox(blockPos, color, true);
                        RenderUtil.renderFive();
                    }
                }
            }
        }
    }

    public static enum renderMode {
        Box,
        TwoD,
        Outline;

    }
}

