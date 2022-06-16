package net.william278.huskchat.bungeecord.event;

import net.william278.huskchat.event.IPrivateMessageEvent;
import net.william278.huskchat.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PrivateMessageEvent extends BungeeEvent implements IPrivateMessageEvent {
    private Player sender;
    private ArrayList<Player> recipients;
    private String message;

    public PrivateMessageEvent(Player sender, ArrayList<Player> recipients, String message) {
        this.sender = sender;
        this.recipients = recipients;
        this.message = message;
    }

    @Override
    public Player getSender() {
        return sender;
    }

    @Override
    public ArrayList<Player> getRecipients() {
        return recipients;
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
    public void setRecipients(@NotNull ArrayList<Player> recipients) {
        this.recipients = recipients;
    }

    @Override
    public void setMessage(@NotNull String message) {
        this.message = message;
    }
}
