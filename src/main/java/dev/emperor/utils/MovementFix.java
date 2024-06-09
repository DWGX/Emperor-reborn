package dev.emperor.utils;

public enum MovementFix {
    OFF("Off"),
    NORMAL("Emperor"),
    TRADITIONAL("Traditional"),
    BACKWARDS_SPRINT("Backwards Sprint");

    String name;

    public String toString() {
        return this.name;
    }

    private MovementFix(String name) {
        this.name = name;
    }
}

