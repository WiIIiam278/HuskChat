package net.william278.huskchat.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.message.ChatMessage;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;
import net.william278.huskchat.velocity.HuskChatVelocity;
import net.william278.huskchat.velocity.player.VelocityPlayer;

public class VelocityListener {

    private static final HuskChatVelocity plugin = HuskChatVelocity.getInstance();

    @Subscribe
    public void onPlayerChat(PlayerChatEvent e) {
        if (e.getMessage().startsWith("/") || !e.getResult().isAllowed()) {
            return;
        }
        e.setResult(PlayerChatEvent.ChatResult.denied());
        Player player = VelocityPlayer.adaptCrossPlatform(e.getPlayer());
        new ChatMessage(PlayerCache.getPlayerChannel(player.getUuid()),
                player, e.getMessage(), plugin).dispatch();
    }

    @Subscribe
    public void onPlayerChangeServer(ServerConnectedEvent e) {
        final String server = e.getServer().getServerInfo().getName();
        if (Settings.serverDefaultChannels.containsKey(server)) {
            PlayerCache.switchPlayerChannel(VelocityPlayer.adaptCrossPlatform(e.getPlayer()),
                    Settings.serverDefaultChannels.get(server), plugin.getMessageManager());
        }
    }
}
