package net.william278.huskchat.event;

import net.william278.huskchat.player.Player;
import org.jetbrains.annotations.NotNull;

public interface IChatMessageEvent extends EventBase {
    Player getSender();
    String getMessage();
    String getChannelId();

    void setSender(@NotNull Player sender);
    void setMessage(@NotNull String message);
    void setChannelId(@NotNull String channelId);
}
