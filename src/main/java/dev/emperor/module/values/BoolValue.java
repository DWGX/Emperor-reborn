package dev.emperor.module.values;

public class BoolValue
extends Value<Boolean> {
    public float alpha = 255.0f;

    public BoolValue(String name, Boolean value) {
        super(name);
        this.setValue(value);
    }

    public BoolValue(String name, Boolean value, Value.Dependency dependenc) {
        super(name, dependenc);
        this.setValue(value);
    }

    @Override
    public Boolean getConfigValue() {
        return (Boolean)this.value;
    }
}

