package me.william278.huskchat.listeners;

import me.william278.huskchat.HuskChat;
import me.william278.huskchat.MessageManager;
import me.william278.huskchat.channels.Channel;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.HashSet;
import java.util.Locale;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChat(ChatEvent e) {
        if (e.isCommand() || e.isProxyCommand()) {
            return;
        }
        e.setCancelled(true);
        ProxiedPlayer player = (ProxiedPlayer) e.getSender();
        sendChatMessage(HuskChat.getPlayerChannel(player.getUniqueId()), player, e.getMessage());
    }

    public static void sendChatMessage(String targetChannelId, ProxiedPlayer sender, String message) {
        for (Channel channel : HuskChat.getConfig().getChannels()) {
            if (channel.getChannelId().equalsIgnoreCase(targetChannelId)) {
                if (channel.getSendPermission() != null) {
                    if (!sender.hasPermission(channel.getSendPermission())) {
                        MessageManager.sendMessage(sender, "error_no_permission_send", channel.getChannelId());
                        return;
                    }
                }

                String messageToSend;
                messageToSend = message; //CensorUtil.censor(message);

                // Get players who will receive the message
                Channel.Scope scope = channel.getBroadcastType();
                HashSet<ProxiedPlayer> messageRecipients = new HashSet<>();
                if (scope == Channel.Scope.GLOBAL) {
                    messageRecipients.addAll(ProxyServer.getInstance().getPlayers());
                } else if (scope == Channel.Scope.LOCAL) {
                    messageRecipients.addAll(sender.getServer().getInfo().getPlayers());
                }

                for (ProxiedPlayer recipient : messageRecipients) {
                    if (channel.getReceivePermission() != null) {
                        if (!recipient.hasPermission(channel.getReceivePermission()) && (recipient.getUniqueId() != sender.getUniqueId())) {
                            continue;
                        }
                    }
                    recipient.sendMessage(channel.getFormattedMessage(sender, messageToSend));
                }
                if (channel.isLogToConsole()) {
                    ProxyServer.getInstance().getLogger().info("[CHAT] [" + channel.getChannelId().toUpperCase(Locale.ROOT) + "] " + sender.getName() + ": " + message);
                }
                return;
            }
        }
        MessageManager.sendMessage(sender, "error_no_channel");
    }

}
