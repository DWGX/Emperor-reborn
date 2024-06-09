package dev.robin.module.values;

import dev.robin.gui.clickgui.book.RippleAnimation;
import dev.robin.module.values.Value;

public class ModeValue<V extends Enum<?>>
extends Value<V> {
    private final V[] modes;
    public boolean expanded;
    public float height;
    public RippleAnimation animation = new RippleAnimation();

    public ModeValue(String name, V[] modes, V value) {
        super(name);
        this.modes = modes;
        this.setValue(value);
    }

    public ModeValue(String name, V[] modes, V value, Value.Dependency dependenc) {
        super(name, dependenc);
        this.modes = modes;
        this.setValue(value);
    }

    public V[] getModes() {
        return this.modes;
    }

    public boolean is(String sb) {
        return ((Enum)this.getValue()).name().equalsIgnoreCase(sb);
    }

    public void setMode(String mode2) {
        for (V e : this.modes) {
            if (!((Enum)e).name().equalsIgnoreCase(mode2)) continue;
            this.setValue(e);
        }
    }

    public boolean isValid(String name) {
        for (V e : this.modes) {
            if (!((Enum)e).name().equalsIgnoreCase(name)) continue;
            return true;
        }
        return false;
    }

    @Override
    public String getConfigValue() {
        return ((Enum)this.getValue()).name();
    }

    public V get() {
        return (V)((Enum)this.getValue());
    }
}

