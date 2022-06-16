package net.william278.huskchat.event;

public interface IBroadcastMessageEvent extends EventBase {
    String getMessage();

    void setMessage(String message);
}
