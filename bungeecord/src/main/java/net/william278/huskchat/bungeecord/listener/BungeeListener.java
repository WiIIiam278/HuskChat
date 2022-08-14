package net.william278.huskchat.bungeecord.listener;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.william278.huskchat.bungeecord.HuskChatBungee;
import net.william278.huskchat.bungeecord.player.BungeePlayer;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.listener.PlayerListener;
import net.william278.huskchat.message.ChatMessage;
import net.william278.huskchat.player.PlayerCache;

public class BungeeListener extends PlayerListener implements Listener {

    private static final HuskChatBungee plugin = HuskChatBungee.getInstance();

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChat(ChatEvent e) {
        if (e.isCommand() || e.isProxyCommand() || e.isCancelled()) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) e.getSender();

        boolean shouldCancel = new ChatMessage(PlayerCache.getPlayerChannel(player.getUniqueId()),
                BungeePlayer.adaptCrossPlatform(player), e.getMessage(), plugin).dispatch();
        if (shouldCancel) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChangeServer(ServerConnectedEvent e) {
        final String server = e.getServer().getInfo().getName();
        final BungeePlayer player = BungeePlayer.adaptCrossPlatform(e.getPlayer());
        handlePlayerSwitchServer(player, server, plugin);
    }

}
