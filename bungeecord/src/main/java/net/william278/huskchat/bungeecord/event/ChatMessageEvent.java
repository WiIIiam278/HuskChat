package net.william278.huskchat.bungeecord.event;

import net.william278.huskchat.event.IChatMessageEvent;
import net.william278.huskchat.player.Player;
import org.jetbrains.annotations.NotNull;

public class ChatMessageEvent extends BungeeEvent implements IChatMessageEvent {
    private Player sender;
    private String message;
    private String channelId;

    public ChatMessageEvent(Player sender, String message, String channelId) {
        this.sender = sender;
        this.message = message;
        this.channelId = channelId;
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
    public String getChannelId() {
        return channelId;
    }

    @Override
    public void setSender(@NotNull Player sender) {
        this.sender = sender;
    }

    @Override
    public void setMessage(@NotNull String message) {
        this.message = message;
    }

    @Override
    public void setChannelId(@NotNull String channelId) {
        this.channelId = channelId;
    }
}
