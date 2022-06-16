package net.william278.huskchat.velocity.event;

import net.william278.huskchat.event.EventBase;

public class BaseEvent implements EventBase {
    private boolean cancelled = false;

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}
