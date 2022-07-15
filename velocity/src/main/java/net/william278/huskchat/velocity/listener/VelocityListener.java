package net.william278.huskchat.velocity.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.message.ChatMessage;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.listener.PlayerListener;
import net.william278.huskchat.player.PlayerCache;
import net.william278.huskchat.velocity.HuskChatVelocity;
import net.william278.huskchat.velocity.player.VelocityPlayer;

public class VelocityListener extends PlayerListener {

    private static final HuskChatVelocity plugin = HuskChatVelocity.getInstance();

    @Subscribe(order = PostOrder.LAST)
    public void onPlayerChat(PlayerChatEvent e) {
        if (e.getMessage().startsWith("/") || !e.getResult().isAllowed()) {
            return;
        }

        final Player player = VelocityPlayer.adaptCrossPlatform(e.getPlayer());
        Channel channel = Settings.channels.get(PlayerCache.getPlayerChannel(player.getUuid()));

        if (channel.broadcastScope.isPassThrough) {
            if (!ChatMessage.passesFilters(plugin, player, new StringBuilder(e.getMessage()), channel)) {
                e.setResult(PlayerChatEvent.ChatResult.denied());
            }

            return;
        }

        e.setResult(PlayerChatEvent.ChatResult.denied());
        new ChatMessage(PlayerCache.getPlayerChannel(player.getUuid()),
                player, e.getMessage(), plugin).dispatch();
    }

    @Subscribe
    public void onPlayerChangeServer(ServerConnectedEvent e) {
        final String server = e.getServer().getServerInfo().getName();
        final VelocityPlayer player = VelocityPlayer.adaptCrossPlatform(e.getPlayer());
        handlePlayerSwitchServer(player, server, plugin);
    }
}
