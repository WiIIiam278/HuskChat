package net.william278.huskchat.event;

import net.william278.huskchat.player.Player;
import org.jetbrains.annotations.NotNull;

public interface IBroadcastMessageEvent extends EventBase {
    Player getSender();
    String getMessage();

    void setSender(@NotNull Player sender);
    void setMessage(@NotNull String message);
}
