package net.william278.huskchat.velocity.event;

import net.william278.huskchat.event.IBroadcastMessageEvent;
import net.william278.huskchat.player.Player;
import org.jetbrains.annotations.NotNull;

public class BroadcastMessageEvent extends VelocityEvent implements IBroadcastMessageEvent {
    private Player sender;
    private String message;

    public BroadcastMessageEvent(Player sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    @Override
    public Player getSender() {
        return sender;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setSender(@NotNull Player sender) {
        this.sender = sender;
    }

    @Override
    public void setMessage(@NotNull String message) {
        this.message = message;
    }
}
