package dev.robin.module.modules.misc;

import dev.robin.event.EventTarget;
import dev.robin.event.attack.EventAttack;
import dev.robin.event.world.EventMotion;
import dev.robin.module.Category;
import dev.robin.module.Module;
import dev.robin.module.values.ModeValue;
import dev.robin.module.values.NumberValue;
import dev.robin.utils.component.BadPacketsComponent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.lang3.RandomUtils;

public class Insults
extends Module {
    private final ModeValue<MODE> mode = new ModeValue("Mode", (Enum[])MODE.values(), (Enum)MODE.Default);
    public final Map<String, List<String>> map = new HashMap<String, List<String>>();
    private final NumberValue delay = new NumberValue("Delay", 0.0, 0.0, 50.0, 1.0);
    private final String[] defaultInsults = new String[]{"L Why don't you use Robin Client?", "L My name is \u9ec4\u89c1\u7ea2,I'm using Robin Client!", "L Buy Robin Client"};
    private EntityPlayer target;
    private int ticks;

    public Insults() {
        super("Insults", Category.World);
    }

    @EventTarget
    private void onMotion(EventMotion event) {
        if (event.isPost()) {
            return;
        }
        if (this.target != null && !Insults.mc.theWorld.playerEntities.contains(this.target) && this.target.isDead) {
            if ((double)this.ticks >= (double)((Double)this.delay.getValue()).intValue() + Math.random() * 5.0 && !BadPacketsComponent.bad()) {
                String insult = "";
                if (((MODE)((Object)this.mode.getValue())).equals((Object)MODE.Default)) {
                    insult = this.defaultInsults[RandomUtils.nextInt((int)0, (int)this.defaultInsults.length)];
                }
                Insults.mc.thePlayer.sendChatMessage(insult);
                this.target = null;
            }
            ++this.ticks;
        }
    }

    @EventTarget
    private void onMotion(EventAttack event) {
        if (event.isPost()) {
            return;
        }
        Entity target = event.getTarget();
        if (target instanceof EntityPlayer) {
            this.target = (EntityPlayer)target;
            this.ticks = 0;
        }
    }

    public static enum MODE {
        Default;

    }
}

