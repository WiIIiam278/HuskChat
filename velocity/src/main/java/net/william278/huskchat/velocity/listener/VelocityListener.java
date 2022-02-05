package net.william278.huskchat.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import net.william278.huskchat.message.ChatMessage;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;
import net.william278.huskchat.velocity.HuskChatVelocity;
import net.william278.huskchat.velocity.player.VelocityPlayer;

public class VelocityListener {

    private static final HuskChatVelocity plugin = HuskChatVelocity.getInstance();

    @Subscribe
    public void onPlayerChat(PlayerChatEvent e) {
        if (e.getMessage().startsWith("/") || e.getResult().isAllowed()) {
            return;
        }
        e.setResult(PlayerChatEvent.ChatResult.denied());
        Player player = VelocityPlayer.adaptCrossPlatform(e.getPlayer());
        new ChatMessage(PlayerCache.getPlayerChannel(player.getUuid()),
                player, e.getMessage(), plugin).dispatch();
    }
}
