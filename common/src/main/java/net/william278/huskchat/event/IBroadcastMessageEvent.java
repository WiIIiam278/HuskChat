package net.william278.huskchat.event;

import org.jetbrains.annotations.NotNull;

public interface IBroadcastMessageEvent extends EventBase {
    String getMessage();

    void setMessage(@NotNull String message);
}
