package dev.robin.module.modules.world;

import dev.robin.event.EventTarget;
import dev.robin.event.world.EventMotion;
import dev.robin.module.Category;
import dev.robin.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

public class Eagle
extends Module {
    public Eagle() {
        super("Eagle", Category.World);
    }

    public static Block getBlock(BlockPos pos) {
        return Eagle.mc.theWorld.getBlockState(pos).getBlock();
    }

    public static Block getBlockUnderPlayer(EntityPlayer player) {
        return Eagle.getBlock(new BlockPos(player.posX, player.posY - 1.0, player.posZ));
    }

    @EventTarget
    public void onUpdate(EventMotion event) {
        if (event.isPre()) {
            if (Eagle.getBlockUnderPlayer(Eagle.mc.thePlayer) instanceof BlockAir) {
                if (Eagle.mc.thePlayer.onGround) {
                    KeyBinding.setKeyBindState(Eagle.mc.gameSettings.keyBindSneak.getKeyCode(), true);
                }
            } else if (Eagle.mc.thePlayer.onGround) {
                KeyBinding.setKeyBindState(Eagle.mc.gameSettings.keyBindSneak.getKeyCode(), false);
            }
        }
    }

    @Override
    public void onEnable() {
        if (Eagle.mc.thePlayer == null) {
            return;
        }
        Eagle.mc.thePlayer.setSneaking(false);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        KeyBinding.setKeyBindState(Eagle.mc.gameSettings.keyBindSneak.getKeyCode(), false);
        super.onDisable();
    }
}

