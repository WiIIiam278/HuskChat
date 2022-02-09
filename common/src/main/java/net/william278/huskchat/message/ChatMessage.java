package net.william278.huskchat.message;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.filter.ChatFilter;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

/**
 * Represents a message to be sent in a chat channel
 */
public record ChatMessage(String targetChannelId, Player sender,
                          String message, HuskChat implementor) {

    /**
     * Dispatch the message to be sent
     */
    public void dispatch() {
        for (Channel channel : Settings.channels) {
            if (channel.id.equalsIgnoreCase(targetChannelId)) {
                // Verify that the player has permission to send in the channel
                if (channel.sendPermission != null) {
                    if (!sender.hasPermission(channel.sendPermission)) {
                        implementor.getMessageManager().sendMessage(sender, "error_no_permission_send", channel.id);
                        return;
                    }
                }

                // Verify that the player is not sending a message from a server where channel access is restricted
                for (String restrictedServer : channel.restrictedServers) {
                    if (restrictedServer.equalsIgnoreCase(sender.getServerName())) {
                        implementor.getMessageManager().sendMessage(sender, "error_channel_restricted_server", channel.id);
                        return;
                    }
                }

                // Determine the players who will receive the message;
                Channel.BroadcastScope broadcastScope = channel.broadcastScope;

                // If the message is to be filtered, then perform filter checks
                if (channel.filter && !sender.hasPermission("huskchat.bypass_filters")) {
                    for (ChatFilter filter : Settings.chatFilters) {
                        if (!filter.isAllowed(message)) {
                            implementor.getMessageManager().sendMessage(sender, filter.getFailureErrorMessageId());
                            return;
                        }
                    }
                }

                // If the message is to be passed through, then do so
                if (broadcastScope.isPassThrough) {
                    sender.passthroughChat(message);
                }

                HashSet<Player> messageRecipients = new HashSet<>();
                switch (broadcastScope) {
                    case GLOBAL, GLOBAL_PASSTHROUGH -> messageRecipients.addAll(implementor.getOnlinePlayers());
                    case LOCAL, LOCAL_PASSTHROUGH -> messageRecipients.addAll(implementor.getOnlinePlayersOnServer(sender));
                    default -> {
                    } // No message recipients if the channel is exclusively passed through; let bukkit handle it
                }

                // Dispatch message to all applicable users in the scope with permission who are not on a restricted server
                MESSAGE_DISPATCH:
                for (Player recipient : messageRecipients) {
                    if (channel.receivePermission != null) {
                        if (!recipient.hasPermission(channel.receivePermission) && !(recipient.getUuid().equals(sender.getUuid()))) {
                            continue;
                        }
                    }

                    for (String restrictedServer : channel.restrictedServers) {
                        if (restrictedServer.equalsIgnoreCase(recipient.getServerName())) {
                            continue MESSAGE_DISPATCH;
                        }
                    }

                    implementor.getMessageManager().sendFormattedChannelMessage(recipient, sender, channel, message);
                }

                // If the message is on a local channel, dispatch local spy messages to appropriate spies.
                if (broadcastScope == Channel.BroadcastScope.LOCAL || broadcastScope == Channel.BroadcastScope.LOCAL_PASSTHROUGH) {
                    if (Settings.doLocalSpyCommand) {
                        if (!Settings.isLocalSpyChannelExcluded(channel)) {
                            final HashMap<Player, PlayerCache.SpyColor> spies = PlayerCache.getLocalSpyMessageReceivers(messageRecipients, sender.getServerName(), implementor);
                            for (Player spy : spies.keySet()) {
                                final PlayerCache.SpyColor color = spies.get(spy);
                                implementor.getMessageManager().sendFormattedLocalSpyMessage(spy, color, sender, channel, message);
                            }
                        }
                    }
                }

                // Log message to console if enabled on the channel
                if (channel.logMessages) {
                    String logMessage = Settings.channelLogFormat;
                    logMessage = logMessage.replaceAll("%channel%", channel.id.toUpperCase());
                    logMessage = logMessage.replaceAll("%sender%", sender.getName());
                    logMessage = logMessage.replaceAll("%message%", message);

                    implementor.getLoggingAdapter().log(Level.INFO, logMessage);
                }
                return;
            }
        }
        implementor.getMessageManager().sendMessage(sender, "error_no_channel");
    }

}