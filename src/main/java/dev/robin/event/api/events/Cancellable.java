package dev.robin.event.api.events;

public interface Cancellable {
    public boolean isCancelled();

    public void setCancelled(boolean var1);

    public void setCancelled();
}

