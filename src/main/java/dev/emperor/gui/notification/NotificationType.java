package dev.emperor.gui.notification;

import java.awt.Color;

public enum NotificationType {
    SUCCESS(new Color(20, 250, 90), "A"),
    DISABLE(new Color(255, 30, 30), "B"),
    INFO(Color.WHITE, "C"),
    WARNING(Color.YELLOW, "D");

    private final Color color;
    private final String icon;

    public Color getColor() {
        return this.color;
    }

    public String getIcon() {
        return this.icon;
    }

    private NotificationType(Color color, String icon) {
        this.color = color;
        this.icon = icon;
    }
}

