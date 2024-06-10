package dev.emperor.module.values;

public class NumberValue
extends Value<Double> {
    public double animatedPercentage;
    public boolean sliding;
    double max;
    double min;
    double inc;

    public NumberValue(String name, double val, double min, double max, double inc) {
        super(name);
        this.setValue(val);
        this.max = max;
        this.min = min;
        this.inc = inc;
    }

    public NumberValue(String name, double val, double min, double max, double inc, Value.Dependency dependenc) {
        super(name, dependenc);
        this.setValue(val);
        this.max = max;
        this.min = min;
        this.inc = inc;
    }

    public Double getMax() {
        return this.max;
    }

    public Double getMin() {
        return this.min;
    }

    public Double getInc() {
        return this.inc;
    }

    @Override
    public Double getConfigValue() {
        return this.getValue();
    }
}

