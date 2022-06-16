package net.william278.huskchat.velocity.event;

import net.william278.huskchat.event.IBroadcastMessageEvent;

public class BroadcastMessageEvent extends BaseEvent implements IBroadcastMessageEvent {
    private String message;

    public BroadcastMessageEvent(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }
}
