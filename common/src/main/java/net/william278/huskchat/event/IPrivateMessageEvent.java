package net.william278.huskchat.event;

import net.william278.huskchat.player.Player;

import java.util.ArrayList;

public interface IPrivateMessageEvent extends EventBase {
    Player getSender();
    ArrayList<Player> getReceivers();
    String getMessage();

    void setSender(Player sender);
    void setReceivers(ArrayList<Player> receivers);
    void setMessage(String message);
}
