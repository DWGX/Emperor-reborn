package dev.robin.utils.misc;

public final class MouseUtils {
    public static boolean isHovering(float x2, float y2, float width, float height, int mouseX, int mouseY) {
        return (float)mouseX >= x2 && (float)mouseY >= y2 && (float)mouseX < x2 + width && (float)mouseY < y2 + height;
    }

    private MouseUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

