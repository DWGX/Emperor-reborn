package dev.robin.gui.notification;

import dev.robin.gui.notification.Notification;
import dev.robin.gui.notification.NotificationType;
import dev.robin.module.modules.render.HUD;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationManager {
    private static float toggleTime = 2.0f;
    private static final CopyOnWriteArrayList<Notification> notifications = new CopyOnWriteArrayList();

    public static void post(NotificationType type, String title, String description) {
        NotificationManager.post(new Notification(type, title, description));
    }

    public static void post(NotificationType type, String title, String description, float time) {
        NotificationManager.post(new Notification(type, title, description, time));
    }

    public static void post(Notification notification) {
        if (((Boolean)HUD.notifications.getValue()).booleanValue()) {
            notifications.add(notification);
        }
    }

    public static float getToggleTime() {
        return toggleTime;
    }

    public static void setToggleTime(float toggleTime) {
        NotificationManager.toggleTime = toggleTime;
    }

    public static CopyOnWriteArrayList<Notification> getNotifications() {
        return notifications;
    }
}

