package dev.robin.utils;

public enum MovementFix {
    OFF("Off"),
    NORMAL("Robin"),
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

