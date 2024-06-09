package dev.emperor.module.modules.combat;

import dev.emperor.event.EventTarget;
import dev.emperor.event.attack.EventAttack;
import dev.emperor.event.world.EventPacketSend;
import dev.emperor.event.world.EventUpdate;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.values.BoolValue;
import dev.emperor.module.values.NumberValue;
import dev.emperor.utils.item.ItemUtils;
import java.util.Objects;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

public class AutoWeapon
extends Module {
    public final BoolValue silentValue = new BoolValue("SpoofItem", false);
    private final NumberValue ticksValue = new NumberValue("SpoofTicks", 10.0, 1.0, 20.0, 1.0);
    private final BoolValue itemTool = new BoolValue("ItemTool", true);
    private boolean attackEnemy = false;
    private int spoofedSlot = 0;

    public AutoWeapon() {
        super("AutoWeapon", Category.Combat);
    }

    @EventTarget
    public void onAttack(EventAttack event) {
        this.attackEnemy = true;
    }

    @EventTarget
    public void onPacketSend(EventPacketSend event) {
        if (event.getPacket() instanceof C02PacketUseEntity && ((C02PacketUseEntity)event.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK && this.attackEnemy) {
            this.attackEnemy = false;
            int slot = -1;
            double maxDamage = 0.0;
            for (int i = 0; i < 9; ++i) {
                double damage;
                if (AutoWeapon.mc.thePlayer.inventory.getStackInSlot(i) == null || !(AutoWeapon.mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemSword) && (!(AutoWeapon.mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemTool) || !((Boolean)this.itemTool.getValue()).booleanValue()) || !((damage = (AutoWeapon.mc.thePlayer.inventory.getStackInSlot(i).getAttributeModifiers().get(((Object)"generic.attackDamage").toString()).stream().findFirst().orElse(null) != null ? ((AttributeModifier)Objects.requireNonNull(AutoWeapon.mc.thePlayer.inventory.getStackInSlot(i).getAttributeModifiers().get((String) "generic.attackDamage").stream().findFirst().orElse(null))).getAmount() : 0.0) + 1.25 * (double)ItemUtils.getEnchantment(AutoWeapon.mc.thePlayer.inventory.getStackInSlot(i), Enchantment.sharpness)) > maxDamage)) continue;
                maxDamage = damage;
                slot = i;
            }
            if (slot == AutoWeapon.mc.thePlayer.inventory.currentItem || slot == -1) {
                return;
            }
            if (((Boolean)this.silentValue.getValue()).booleanValue()) {
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(slot));
                this.spoofedSlot = ((Double)this.ticksValue.getValue()).intValue();
            } else {
                AutoWeapon.mc.thePlayer.inventory.currentItem = slot;
                AutoWeapon.mc.playerController.updateController();
            }
            mc.getNetHandler().addToSendQueue(event.getPacket());
            event.setCancelled(true);
        }
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (this.spoofedSlot > 0) {
            if (this.spoofedSlot == 1) {
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(AutoWeapon.mc.thePlayer.inventory.currentItem));
            }
            --this.spoofedSlot;
        }
    }
}
