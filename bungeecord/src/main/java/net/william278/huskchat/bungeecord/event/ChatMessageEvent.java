package net.william278.huskchat.bungeecord.event;

import net.william278.huskchat.event.IChatMessageEvent;
import net.william278.huskchat.player.Player;

public class ChatMessageEvent extends BaseEvent implements IChatMessageEvent {
    private Player sender;
    private String message;
    private String channelId;

    public ChatMessageEvent(Player sender, String message, String channelId) {
        this.sender = sender;
        this.message = message;
        this.channelId = channelId;
    }

    public Player getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getChannelId() {
        return channelId;
    }

    @Override
    public void setSender(Player sender) {
        this.sender = sender;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
}
