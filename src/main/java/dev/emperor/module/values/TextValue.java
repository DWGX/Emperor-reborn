package dev.emperor.module.values;

public class TextValue
extends Value<String> {
    protected static TextValue self;
    public String selectedString = "";
    public static String DEFAULT_STRING;

    public TextValue(String name, String description, String value) {
        super(name);
    }

    public TextValue(String name, String description, String value, Value.Dependency dependenc) {
        super(name, dependenc);
    }

    public static TextValue create(String name) {
        self = new TextValue(name, DEFAULT_STRING, DEFAULT_STRING);
        return self;
    }

    public TextValue withDescription(String description) {
        return self;
    }

    public TextValue defaultTo(String value) {
        DEFAULT_STRING = value;
        return self;
    }

    @Override
    public String getConfigValue() {
        return this.selectedString;
    }

    public String getSelectedString() {
        return this.selectedString;
    }

    public void setSelectedString(String selectedString) {
        this.selectedString = selectedString;
    }
}

