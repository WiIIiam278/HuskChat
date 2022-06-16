package net.william278.huskchat.event;

import net.william278.huskchat.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public interface IPrivateMessageEvent extends EventBase {
    Player getSender();
    ArrayList<Player> getRecipients();
    String getMessage();

    void setSender(@NotNull Player sender);
    void setRecipients(@NotNull ArrayList<Player> Recipients);
    void setMessage(@NotNull String message);
}
