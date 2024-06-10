package dev.emperor.module.modules.world;

import dev.emperor.Client;
import dev.emperor.event.EventTarget;
import dev.emperor.event.rendering.EventRender2D;
import dev.emperor.event.world.EventMotion;
import dev.emperor.event.world.EventMoveInput;
import dev.emperor.gui.notification.NotificationManager;
import dev.emperor.gui.notification.NotificationType;
import dev.emperor.module.Category;
import dev.emperor.module.Module;
import dev.emperor.module.values.BoolValue;
import dev.emperor.utils.DebugUtil;
import dev.emperor.utils.InventoryUtil;
import dev.emperor.utils.ProjectileUtil;
import dev.emperor.utils.client.MathUtil;
import dev.emperor.utils.client.TimeUtil;
import dev.emperor.utils.component.FallDistanceComponent;
import dev.emperor.utils.player.PlayerUtil;
import dev.emperor.utils.render.fontRender.FontManager;
import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;
import javax.vecmath.Vector2f;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;

public class AutoPearl
extends Module {
    private final BoolValue debugValue = new BoolValue("Debug", false);
    private static final double T = 10.0;
    private static final double T_MIN = 1.0E-4;
    private static final double ALPHA = 0.997;
    private CalculateThread calculateThread;
    private final TimeUtil timer = new TimeUtil();
    private boolean attempted;
    private boolean calculating;
    private int bestPearlSlot;

    public AutoPearl() {
        super("AutoPearl", Category.World);
    }

    @EventTarget
    public void onMoveInput(EventMoveInput event) {
        if (this.calculating) {
            event.setCancelled(true);
        }
    }

    @EventTarget
    public void onRender(EventRender2D event) {
        if (!this.debugValue.getValue()) {
            return;
        }
        FontManager.arial12.drawString("assessment: " + new ProjectileUtil.EnderPearlPredictor(AutoPearl.mc.thePlayer.posX, AutoPearl.mc.thePlayer.posY, AutoPearl.mc.thePlayer.posZ, AutoPearl.mc.thePlayer.motionY - 0.01, AutoPearl.mc.thePlayer.motionY + 0.02).assessRotation(new Vector2f(AutoPearl.mc.thePlayer.rotationYaw, AutoPearl.mc.thePlayer.rotationPitch)), 20.0f, 20.0f, Color.WHITE.getRGB());
        FontManager.arial12.drawString("(" + AutoPearl.mc.thePlayer.rotationYaw + ", " + AutoPearl.mc.thePlayer.rotationPitch + ")", 20.0f, 30.0f, Color.WHITE.getRGB());
    }

    @EventTarget
    public void onMotion(EventMotion event) throws InterruptedException {
        boolean overVoid;
        if (AutoPearl.mc.thePlayer.onGround) {
            this.attempted = false;
            this.calculating = false;
        }
        if (event.isPost() && this.calculating && (this.calculateThread == null || this.calculateThread.completed)) {
            this.calculating = false;
            Stuck.throwPearl(this.calculateThread.solution);
        }
        boolean bl = overVoid = !AutoPearl.mc.thePlayer.onGround && !PlayerUtil.isBlockUnder(30.0, true);
        if (!this.attempted && !AutoPearl.mc.thePlayer.onGround && overVoid && FallDistanceComponent.distance > 2.0f) {
            FallDistanceComponent.distance = 0.0f;
            DebugUtil.log("1");
            this.attempted = true;
            for (int slot = 5; slot < 45; ++slot) {
                ItemStack stack = AutoPearl.mc.thePlayer.inventoryContainer.getSlot(slot).getStack();
                if (stack == null || !(stack.getItem() instanceof ItemEnderPearl) || slot < 36) continue;
                this.bestPearlSlot = slot;
                if (this.debugValue.getValue()) {
                    DebugUtil.log("Found Pearl:" + (this.bestPearlSlot - 36));
                }
                if (this.bestPearlSlot - 36 == -37) continue;
                AutoPearl.mc.thePlayer.inventory.currentItem = this.bestPearlSlot - 36;
            }
            if (this.bestPearlSlot == 0) {
                return;
            }
            DebugUtil.log(this.bestPearlSlot);
            if (!(AutoPearl.mc.thePlayer.inventoryContainer.getSlot(this.bestPearlSlot).getStack().getItem() instanceof ItemEnderPearl)) {
                return;
            }
            this.calculating = true;
            this.calculateThread = new CalculateThread(AutoPearl.mc.thePlayer.posX, AutoPearl.mc.thePlayer.posY, AutoPearl.mc.thePlayer.posZ, 0.0, 0.0);
            this.calculateThread.start();
            this.getModule(Stuck.class).setState(true);
        }
    }

    private void putItemInSlot(int slot, int slotIn) {
        InventoryUtil.windowClick(mc, slotIn, slot - 36, InventoryUtil.ClickType.SWAP_WITH_HOT_BAR_SLOT);
    }

    private class CalculateThread
    extends Thread {
        private int iteration;
        private boolean completed;
        private double temperature;
        private double energy;
        private double solutionE;
        private Vector2f solution;
        public boolean stop;
        private final ProjectileUtil.EnderPearlPredictor predictor;

        private CalculateThread(double predictX, double predictY, double predictZ, double minMotionY, double maxMotionY) {
            this.predictor = new ProjectileUtil.EnderPearlPredictor(predictX, predictY, predictZ, minMotionY, maxMotionY);
            this.iteration = 0;
            this.temperature = 10.0;
            this.energy = 0.0;
            this.stop = false;
            this.completed = false;
        }

        @Override
        public void run() {
            TimeUtil timer = new TimeUtil();
            timer.reset();
            Vector2f current = this.solution = new Vector2f(MathUtil.getRandomInRange(-180, 180), MathUtil.getRandomInRange(-90, 90));
            this.solutionE = this.energy = this.predictor.assessRotation(this.solution);
            while (this.temperature >= 1.0E-4 && !this.stop) {
                double assessment;
                double deltaE;
                Vector2f rotation = new Vector2f((float)((double)current.x + MathUtil.getRandomInRange(-this.temperature * 18.0, this.temperature * 18.0)), (float)((double)current.y + MathUtil.getRandomInRange(-this.temperature * 9.0, this.temperature * 9.0)));
                if (rotation.y > 90.0f) {
                    rotation.y = 90.0f;
                }
                if (rotation.y < -90.0f) {
                    rotation.y = -90.0f;
                }
                if ((deltaE = (assessment = this.predictor.assessRotation(rotation)) - this.energy) >= 0.0 || (double)MathUtil.getRandomInRange(0, 1) < Math.exp(-deltaE / this.temperature * 100.0)) {
                    this.energy = assessment;
                    current = rotation;
                    if (assessment > this.solutionE) {
                        this.solutionE = assessment;
                        this.solution = new Vector2f(rotation.x, rotation.y);
                        DebugUtil.log("Find a better solution: (" + this.solution.x + ", " + this.solution.y + "), value: " + this.solutionE);
                    }
                }
                this.temperature *= 0.997;
                ++this.iteration;
            }
            NotificationManager.post(NotificationType.SUCCESS, "AutoPearl", "Simulated annealing completed within " + this.iteration + " iterations", 5.0f);
            NotificationManager.post(NotificationType.SUCCESS, "AutoPearl", "Time used: " + timer.getDifference() + " solution energy: " + this.solutionE, 5.0f);
            this.completed = true;
            Timer timer1 = new Timer();
            TimerTask task = new TimerTask(){

                @Override
                public void run() {
                    Client.instance.moduleManager.getModule(Stuck.class).setState(false);
                }
            };
            timer1.schedule(task, 2800L);
        }
    }
}

