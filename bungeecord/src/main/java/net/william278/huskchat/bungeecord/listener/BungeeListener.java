package net.william278.huskchat.bungeecord.listener;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.william278.huskchat.bungeecord.HuskChatBungee;
import net.william278.huskchat.bungeecord.player.BungeePlayer;
import net.william278.huskchat.message.ChatMessage;
import net.william278.huskchat.player.PlayerCache;

public class BungeeListener implements Listener {

    private static final HuskChatBungee plugin = HuskChatBungee.getInstance();

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChat(ChatEvent e) {
        if (e.isCommand() || e.isProxyCommand() || e.isCancelled()) {
            return;
        }
        e.setCancelled(true);
        ProxiedPlayer player = (ProxiedPlayer) e.getSender();
        new ChatMessage(PlayerCache.getPlayerChannel(player.getUniqueId()),
                BungeePlayer.adaptCrossPlatform(player), e.getMessage(), plugin).dispatch();
    }

}
