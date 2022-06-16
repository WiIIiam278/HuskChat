package net.william278.huskchat.velocity.event;

import net.william278.huskchat.event.IPrivateMessageEvent;
import net.william278.huskchat.player.Player;

import java.util.ArrayList;

public class PrivateMessageEvent extends BaseEvent implements IPrivateMessageEvent {
    private Player sender;
    private ArrayList<Player> receivers;
    private String message;

    public PrivateMessageEvent(Player sender, ArrayList<Player> receivers, String message) {
        this.sender = sender;
        this.receivers = receivers;
        this.message = message;
    }

    public Player getSender() {
        return sender;
    }

    public ArrayList<Player> getReceivers() {
        return receivers;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public void setSender(Player sender) {
        this.sender = sender;
    }

    @Override
    public void setReceivers(ArrayList<Player> receivers) {
        this.receivers = receivers;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }
}
