package net.william278.huskchat.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import net.william278.huskchat.HuskChat;
import org.jetbrains.annotations.NotNull;

public record VelocityEventChatListener(@NotNull HuskChat plugin) implements VelocityChatListener {

    @Subscribe(order = PostOrder.LATE)
    public void onPlayerChat(PlayerChatEvent e) {
        if (!e.getResult().isAllowed()) {
            return;
        }
        if (!this.handlePlayerChat(e)) {
            e.setResult(PlayerChatEvent.ChatResult.denied());
        }
    }

    @Override
    @NotNull
    public HuskChat plugin() {
        return plugin;
    }

}
