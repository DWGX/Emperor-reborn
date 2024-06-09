package dev.emperor.module;

import dev.emperor.event.EventManager;
import dev.emperor.event.EventTarget;
import dev.emperor.event.misc.EventKey;
import dev.emperor.event.rendering.EventRender2D;
import dev.emperor.module.modules.combat.*;
import dev.emperor.module.modules.misc.*;
import dev.emperor.module.modules.movement.*;
import dev.emperor.module.modules.player.*;
import dev.emperor.module.modules.render.*;
import dev.emperor.module.modules.world.*;
import dev.emperor.module.values.Value;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class ModuleManager {
    private static final List<Module> modules = new ArrayList<Module>();
    private boolean enabledNeededMod = true;

    private void addModule(Module module) {
        for (Field field : module.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object obj = field.get(module);
                if (!(obj instanceof Value)) continue;
                module.getValues().add((Value)obj);
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        this.modules.add(module);
    }
    public static List<Module> getModulesInType(Category t) {
        ArrayList<Module> output = new ArrayList<Module>();
        for (Module m : modules) {
            if (m.getCategory() != t) continue;
            output.add(m);
        }
        return output;
    }
    public List<Module> getModules() {
        return this.modules;
    }

    public <T extends Module> T getModule(Class<T> cls) {
        for (Module m : this.modules) {
            if (m.getClass() != cls) continue;
            return (T)m;
        }
        return null;
    }

    public Module getModule(String name) {
        for (Module m : this.modules) {
            if (!m.getName().equalsIgnoreCase(name)) continue;
            return m;
        }
        return null;
    }

    public boolean haveModules(Category category, String key) {
        ArrayList<Module> array = new ArrayList<Module>(this.modules);
        array.removeIf(module -> module.getCategory() != category);
        array.removeIf(module -> !module.getName().toLowerCase().replaceAll(" ", "").contains(key));
        return array.size() == 0;
    }

    @EventTarget
    public void onKey(EventKey e) {
        for (Module m : this.modules) {
            if (m.getKey() != e.getKey() || e.getKey() == -1) continue;
            m.toggle();
        }
    }

    public List<Module> getModsByCategory(Category m) {
        ArrayList<Module> findList = new ArrayList<Module>();
        for (Module mod : this.modules) {
            if (mod.getCategory() != m) continue;
            findList.add(mod);
        }
        return findList;
    }

    @EventTarget
    private void on2DRender(EventRender2D e) {
        if (this.enabledNeededMod) {
            this.enabledNeededMod = false;
            for (Module m : this.modules) {
                if (!m.isDefaultOn()) continue;
                m.setState(true);
            }
        }
    }
    public void init() {
        EventManager.register(this);
        System.out.println("Init Modules...");
        this.addModule(new AutoClicker());
        this.addModule(new AntiBot());
        this.addModule(new AutoSoup());
        this.addModule(new AutoWeapon());
        this.addModule(new BackTrack());
        this.addModule(new Criticals());
        this.addModule(new KillAura());
        this.addModule(new Reach());
        this.addModule(new SuperKnockback());
        this.addModule(new Velocity());
        this.addModule(new BowAimbot());
        this.addModule(new TimerRange());
        this.addModule(new AutoClip());
        this.addModule(new NoSlow());
        this.addModule(new Speed());
        this.addModule(new FastLadder());
        this.addModule(new Sprint());
        this.addModule(new Step());
        this.addModule(new Fly());
        this.addModule(new Strafe());
        this.addModule(new Timer());
        this.addModule(new TargetStrafe());
        this.addModule(new GuiMove());
        this.addModule(new WallClimb());
        this.addModule(new BlockESP());
        this.addModule(new BlockHit());
        this.addModule(new Camera());
        this.addModule(new Chams());
        this.addModule(new ClickGui());
        this.addModule(new ChinaHat());
        this.addModule(new ESP());
        this.addModule(new GlowESP());
        this.addModule(new NameTags());
        this.addModule(new FullBright());
        this.addModule(new Health());
        this.addModule(new HUD());
        this.addModule(new ItemPhysics());
        this.addModule(new Skeletal());
        this.addModule(new KillEffect());
        this.addModule(new PostProcessing());
        this.addModule(new MotionBlur());
        this.addModule(new Trail());
        this.addModule(new XRay());
        this.addModule(new Projectile());
        this.addModule(new MoBendsMod());
        this.addModule(new Ambience());
        this.addModule(new AutoLobby());
        this.addModule(new AutoPlay());
        this.addModule(new PacketFix());
        this.addModule(new Disabler());
        this.addModule(new Protocol());
        this.addModule(new FakePlayer());
        this.addModule(new MemoryFix());
        //this.addModule(new Protocol());
        this.addModule(new PingSpoof());
        this.addModule(new ModuleHelper());
        this.addModule(new NoRotateSet());
        this.addModule(new NoPitchLimit());
        this.addModule(new Teams());
        this.addModule(new GrimAC());
        this.addModule(new AntiVoid());
        this.addModule(new AntiFireBall());
        this.addModule(new AutoArmor());
        this.addModule(new AutoTool());
        this.addModule(new Blink());
        this.addModule(new FastPlace());
        this.addModule(new InvCleaner());
        this.addModule(new NoFall());
        this.addModule(new FastEat());
        this.addModule(new Regen());
        this.addModule(new SpeedMine());
        this.addModule(new ChestStealer());
        //this.addModule(new NoWater());
        this.addModule(new NoWeb());
        this.addModule(new CivBreak());
        this.addModule(new Scaffold());
        this.addModule(new Eagle());
        this.addModule(new PlayerWarn());
        this.addModule(new ChestAura());
        this.addModule(new Stuck());
        this.addModule(new AutoPearl());
        this.addModule(new Insults());
        this.addModule(new Hub());
        modules.sort(Comparator.comparing((Function<? super Module, ? extends Comparable>)Module::getName));
    }
}

