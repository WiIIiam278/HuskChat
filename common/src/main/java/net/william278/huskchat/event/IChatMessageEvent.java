package net.william278.huskchat.event;

import net.william278.huskchat.player.Player;

public interface IChatMessageEvent extends EventBase {
    Player getSender();
    String getMessage();
    String getChannelId();

    void setSender(Player sender);
    void setMessage(String message);
    void setChannelId(String channelId);
}
