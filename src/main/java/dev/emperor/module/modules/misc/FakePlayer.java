package dev.emperor.module.modules.misc;

import dev.emperor.module.Category;
import dev.emperor.module.Module;
import java.util.LinkedList;
import net.minecraft.client.entity.EntityOtherPlayerMP;

public class FakePlayer
extends Module {
    private EntityOtherPlayerMP fakePlayer = null;
    private final LinkedList<double[]> positions = new LinkedList();

    public FakePlayer() {
        super("FakePlayer", Category.Misc);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onEnable() {
        if (FakePlayer.mc.thePlayer == null) {
            return;
        }
        this.fakePlayer = new EntityOtherPlayerMP(FakePlayer.mc.theWorld, FakePlayer.mc.thePlayer.getGameProfile());
        this.fakePlayer.clonePlayer(FakePlayer.mc.thePlayer, true);
        this.fakePlayer.copyLocationAndAnglesFrom(FakePlayer.mc.thePlayer);
        this.fakePlayer.rotationYawHead = FakePlayer.mc.thePlayer.rotationYawHead;
        FakePlayer.mc.theWorld.addEntityToWorld(-1337, this.fakePlayer);
        LinkedList<double[]> linkedList = this.positions;
        synchronized (linkedList) {
            this.positions.add(new double[]{FakePlayer.mc.thePlayer.posX, FakePlayer.mc.thePlayer.getEntityBoundingBox().minY + (double)(FakePlayer.mc.thePlayer.getEyeHeight() / 2.0f), FakePlayer.mc.thePlayer.posZ});
            this.positions.add(new double[]{FakePlayer.mc.thePlayer.posX, FakePlayer.mc.thePlayer.getEntityBoundingBox().minY, FakePlayer.mc.thePlayer.posZ});
        }
    }

    @Override
    public void onDisable() {
        if (FakePlayer.mc.thePlayer == null) {
            return;
        }
        if (this.fakePlayer != null) {
            FakePlayer.mc.theWorld.removeEntityFromWorld(this.fakePlayer.getEntityId());
            this.fakePlayer = null;
        }
    }
}

