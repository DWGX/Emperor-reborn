package dev.emperor.module.modules.world;

import dev.emperor.Client;
import dev.emperor.event.EventTarget;
import dev.emperor.event.rendering.EventRender3D;
import dev.emperor.event.world.EventMotion;
import dev.emperor.event.world.EventWorldLoad;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.modules.combat.KillAura;
import dev.emperor.module.values.NumberValue;
import dev.emperor.utils.RotationComponent;
import dev.emperor.utils.TimeHelper;
import dev.emperor.utils.client.PacketUtil;
import dev.emperor.utils.player.RotationUtil;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.lwjgl.compatibility.util.vector.Vector2f;
import org.lwjgl.opengl.GL11;

public class ChestAura extends Module {
    private final NumberValue range = new NumberValue("Range", 3.0, 1.0, 7.0, 0.1);
    public TimeHelper waitBoxOpenTimer = new TimeHelper();
    public static boolean isWaitingOpen = false;
    private BlockPos globalPos;
    private BlockPos openingPos;
    public static List<BlockPos> list = new ArrayList<>();

    public ChestAura() {
        super("ChestAura", Category.Player);
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
        for (float y2 = radius = this.range.getValue().floatValue(); y2 >= -radius; y2 -= 1.0f) {
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
    public void onWorld(EventWorldLoad e) {
        list.clear();
    }

    @EventTarget
    public void onRender(EventRender3D e) {
        for (BlockPos pos : list) {
            double x = pos.getX() - ChestAura.mc.getRenderManager().viewerPosX + 0.5;
            double y = pos.getY() - ChestAura.mc.getRenderManager().viewerPosY + 1;
            double z = pos.getZ() - ChestAura.mc.getRenderManager().viewerPosZ + 0.5;

            drawInvertedRedTriangle(x, y, z, 0.6f);
        }
    }

    private void drawInvertedRedTriangle(double x, double y, double z, float alpha) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0f, 0.0f, 0.0f, alpha);

        GlStateManager.disableLighting();
        GlStateManager.disableCull();

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

        GL11.glBegin(GL11.GL_TRIANGLES);

        GL11.glVertex3d(x, y - 0.5 / 2, z);
        GL11.glVertex3d(x - 0.5 / 2, y + 0.5 / 2, z);
        GL11.glVertex3d(x + 0.5 / 2, y + 0.5 / 2, z);

        GL11.glEnd();

        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);

        GlStateManager.enableCull();
        GlStateManager.enableLighting();

        GL11.glPopMatrix();
    }

    public void sendClick(BlockPos pos) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5;

        float[] rotations = RotationUtil.getRotationToBlock(pos);

        double diffX = x - ChestAura.mc.thePlayer.posX;
        double diffY = (y - ChestAura.mc.thePlayer.posY) - 1.7;
        double diffZ = z - ChestAura.mc.thePlayer.posZ;
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) (-(Math.atan2(diffY, dist) * 180.0D / Math.PI));

        RotationComponent.setRotations(new Vector2f(yaw, pitch), 360.0f, true);

        C08PacketPlayerBlockPlacement packet = new C08PacketPlayerBlockPlacement(pos, 1, ChestAura.mc.thePlayer.getCurrentEquippedItem(), 0.0f, 0.0f, 0.0f);
        ChestAura.mc.thePlayer.sendQueue.addToSendQueue(packet);

        this.waitBoxOpenTimer.reset();
        isWaitingOpen = true;
        this.openingPos = pos;
    }
}
