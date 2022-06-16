package net.william278.huskchat.event;

public interface EventBase {
    void setCancelled(boolean cancelled);
    boolean isCancelled();
}
