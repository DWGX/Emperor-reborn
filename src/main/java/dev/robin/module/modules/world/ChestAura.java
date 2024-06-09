package dev.robin.module.modules.world;

import dev.robin.Client;
import dev.robin.event.EventTarget;
import dev.robin.event.rendering.EventRender3D;
import dev.robin.event.world.EventMotion;
import dev.robin.event.world.EventWorldLoad;
import dev.robin.module.Category;
import dev.robin.module.Module;
import dev.robin.module.modules.combat.KillAura;
import dev.robin.module.modules.world.Scaffold;
import dev.robin.module.values.NumberValue;
import dev.robin.utils.RotationComponent;
import dev.robin.utils.TimeHelper;
import dev.robin.utils.client.PacketUtil;
import dev.robin.utils.player.RotationUtil;
import dev.robin.utils.render.RenderUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import org.lwjgl.compatibility.util.vector.Vector2f;

public class ChestAura
extends Module {
    private final NumberValue range = new NumberValue("Range", 3.0, 1.0, 7.0, 0.1);
    private final ArrayList<BlockPos> opened = new ArrayList();
    public TimeHelper waitBoxOpenTimer = new TimeHelper();
    public static boolean isWaitingOpen = false;
    private BlockPos globalPos;
    private BlockPos openingPos;
    public static List<BlockPos> list = new ArrayList<BlockPos>();

    public ChestAura() {
        super("ContainerAura", Category.World);
    }

    @EventTarget
    public void onPre(EventMotion e) {
        float radius;
        this.globalPos = null;
        if (ChestAura.mc.thePlayer.ticksExisted % 20 == 0 || KillAura.target != null || ChestAura.mc.currentScreen instanceof GuiContainer || Client.instance.moduleManager.getModule(Scaffold.class).getState()) {
            return;
        }
        if (list.size() >= 50) {
            return;
        }
        for (float y2 = radius = ((Double)this.range.getValue()).floatValue(); y2 >= -radius; y2 -= 1.0f) {
            for (float x2 = -radius; x2 <= radius; x2 += 1.0f) {
                for (float z = -radius; z <= radius; z += 1.0f) {
                    BlockPos pos = new BlockPos(ChestAura.mc.thePlayer.posX - 0.5 + (double)x2, ChestAura.mc.thePlayer.posY - 0.5 + (double)y2, ChestAura.mc.thePlayer.posZ - 0.5 + (double)z);
                    Block block = ChestAura.mc.theWorld.getBlockState(pos).getBlock();
                    BlockPos targetPos = new BlockPos(ChestAura.mc.thePlayer.posX + (double)x2, ChestAura.mc.thePlayer.posY + (double)y2, ChestAura.mc.thePlayer.posZ + (double)z);
                    if (!(ChestAura.mc.thePlayer.getDistance(targetPos.getX(), targetPos.getY(), targetPos.getZ()) < (double)ChestAura.mc.playerController.getBlockReachDistance()) || !(block instanceof BlockChest) || list.contains(pos)) continue;
                    float[] rotations = RotationUtil.getBlockRotations(pos.getX(), pos.getY(), pos.getZ());
                    RotationComponent.setRotations(new Vector2f(rotations[0], rotations[1]), 360.0f, true);
                    this.globalPos = pos;
                    return;
                }
            }
        }
    }

    @EventTarget
    public void onPost(EventMotion e) {
        if (e.isPost()) {
            if (isWaitingOpen) {
                if (this.waitBoxOpenTimer.isDelayComplete(600.0)) {
                    isWaitingOpen = false;
                } else if (this.openingPos != null && ChestAura.mc.thePlayer.openContainer instanceof ContainerChest) {
                    list.add(this.openingPos);
                    this.openingPos = null;
                    isWaitingOpen = false;
                }
            }
            if (!(this.globalPos == null || ChestAura.mc.currentScreen instanceof GuiContainer || list.size() >= 50 || isWaitingOpen || list.contains(this.globalPos))) {
                this.sendClick(this.globalPos);
                PacketUtil.sendPacketNoEvent(new C0APacketAnimation());
            }
        }
    }

    @EventTarget
    public void onWOrld(EventWorldLoad e) {
        list.clear();
    }

    @EventTarget
    public void onRender(EventRender3D e) {
        for (BlockPos pos : list) {
            double x2 = (double)pos.getX() - ChestAura.mc.getRenderManager().renderPosX;
            double y2 = (double)pos.getY() - ChestAura.mc.getRenderManager().renderPosY;
            double z = (double)pos.getZ() - ChestAura.mc.getRenderManager().renderPosZ;
            RenderUtil.drawEntityESP(x2, y2, z, x2 + 1.0, y2 + 1.0, z + 1.0, 0.0f, 255.0f, 255.0f, 1.0f);
        }
    }

    public void sendClick(BlockPos pos) {
        C08PacketPlayerBlockPlacement packet = new C08PacketPlayerBlockPlacement(pos, (double)pos.getY() + 0.5 < ChestAura.mc.thePlayer.posY + 1.7 ? 1 : 0, ChestAura.mc.thePlayer.getCurrentEquippedItem(), 0.0f, 0.0f, 0.0f);
        ChestAura.mc.thePlayer.sendQueue.addToSendQueue(packet);
        this.waitBoxOpenTimer.reset();
        isWaitingOpen = true;
        this.openingPos = this.globalPos;
    }
}

