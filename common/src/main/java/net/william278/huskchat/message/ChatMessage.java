package net.william278.huskchat.message;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.filter.ChatFilter;
import net.william278.huskchat.filter.replacer.ReplacerFilter;
import net.william278.huskchat.player.ConsolePlayer;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

/**
 * Represents a message to be sent in a chat channel
 */
public class ChatMessage {

    public final String targetChannelId;
    public Player sender;
    public final HuskChat implementor;

    public String message;

    public ChatMessage(String targetChannelId, Player sender, String message, HuskChat implementor) {
        this.targetChannelId = targetChannelId;
        this.sender = sender;
        this.message = message;
        this.implementor = implementor;
    }

    /**
     * Dispatch the message to be sent
     */
    public void dispatch() {
        AtomicReference<Channel> channel = new AtomicReference<>(Settings.channels.get(targetChannelId));

        if (channel.get() == null) {
            implementor.getMessageManager().sendMessage(sender, "error_no_channel");
            return;
        }

        // Verify that the player has permission to send in the channel
        if (channel.get().sendPermission != null) {
            if (!sender.hasPermission(channel.get().sendPermission)) {
                implementor.getMessageManager().sendMessage(sender, "error_no_permission_send", channel.get().id);
                return;
            }
        }

        // Verify that the player is not sending a message from a server where channel access is restricted
        for (String restrictedServer : channel.get().restrictedServers) {
            if (restrictedServer.equalsIgnoreCase(sender.getServerName())) {
                implementor.getMessageManager().sendMessage(sender, "error_channel_restricted_server", channel.get().id);
                return;
            }
        }

        // Determine the players who will receive the message;
        Channel.BroadcastScope broadcastScope = channel.get().broadcastScope;

        // There's no point in allowing console to send to local chat as it's not actually in any servers and
        // therefore the message won't get sent to anyone
        if (sender instanceof ConsolePlayer && (broadcastScope == Channel.BroadcastScope.LOCAL ||
                broadcastScope == Channel.BroadcastScope.LOCAL_PASSTHROUGH)) {
            implementor.getLoggingAdapter().log(Level.INFO, implementor.getMessageManager().getRawMessage("error_console_local_scope"));
            return;
        }

        StringBuilder msg = new StringBuilder(message);
        if (!ChatMessage.passesFilters(implementor, sender, msg, channel.get())) {
            return;
        }
        message = msg.toString();

        HashSet<Player> messageRecipients = new HashSet<>();
        switch (broadcastScope) {
            case GLOBAL, GLOBAL_PASSTHROUGH -> messageRecipients.addAll(implementor.getOnlinePlayers());
            case LOCAL, LOCAL_PASSTHROUGH -> messageRecipients.addAll(implementor.getOnlinePlayersOnServer(sender));
            default -> {
            } // No message recipients if the channel is exclusively passed through; let the backend handle it
        }

        implementor.getEventDispatcher().dispatchChatMessageEvent(sender, message, targetChannelId).thenAccept(event -> {
            if (event.isCancelled()) return;

            sender = event.getSender();

            if (!event.getChannelId().equals(channel.get().id)) {
                if (Settings.channels.containsKey(event.getChannelId())) {
                    channel.set(Settings.channels.get(event.getChannelId()));
                }
            }

            message = event.getMessage();

            // Dispatch message to all applicable users in the scope with permission who are not on a restricted server
            MESSAGE_DISPATCH:
            for (Player recipient : messageRecipients) {
                if (channel.get().receivePermission != null) {
                    if (!recipient.hasPermission(channel.get().receivePermission) && !(recipient.getUuid().equals(sender.getUuid()))) {
                        continue;
                    }
                }

                for (String restrictedServer : channel.get().restrictedServers) {
                    if (restrictedServer.equalsIgnoreCase(recipient.getServerName())) {
                        continue MESSAGE_DISPATCH;
                    }
                }

                implementor.getMessageManager().sendFormattedChannelMessage(recipient, sender, channel.get(), message);
            }

            // If the message is on a local channel, dispatch local spy messages to appropriate spies.
            if (broadcastScope == Channel.BroadcastScope.LOCAL || broadcastScope == Channel.BroadcastScope.LOCAL_PASSTHROUGH) {
                if (Settings.doLocalSpyCommand) {
                    if (!Settings.isLocalSpyChannelExcluded(channel.get())) {
                        final HashMap<Player, PlayerCache.SpyColor> spies = PlayerCache.getLocalSpyMessageReceivers(sender.getServerName(), implementor);
                        for (Player spy : spies.keySet()) {
                            if (spy.getUuid().equals(sender.getUuid())) {
                                continue;
                            }
                            if (!spy.hasPermission("huskchat.command.localspy")) {
                                try {
                                    PlayerCache.removeLocalSpy(spy);
                                } catch (IOException e) {
                                    implementor.getLoggingAdapter().log(Level.SEVERE, "Failed to remove local spy after failed permission check", e);
                                }
                                continue;
                            }
                            final PlayerCache.SpyColor color = spies.get(spy);
                            implementor.getMessageManager().sendFormattedLocalSpyMessage(spy, color, sender, channel.get(), message);
                        }
                    }
                }
            }

            // Log message to console if enabled on the channel
            if (channel.get().logMessages) {
                String logFormat = Settings.channelLogFormat;
                logFormat = logFormat.replaceAll("%channel%", channel.get().id.toUpperCase());
                logFormat = logFormat.replaceAll("%sender%", sender.getName());
                implementor.getLoggingAdapter().log(Level.INFO, logFormat + message);
            }

        });
    }

    // This is a static method to allow for filters to be applied to passthrough channels due to those not going through this class
    // The StringBuilder allows us to modify the message if a replacer requires it.
    // Returns true if it passes all chat filters.
    public static boolean passesFilters(HuskChat implementor, Player sender, StringBuilder message, Channel channel) {
        // If the message is to be filtered, then perform filter checks (unless they have the bypass permission)
        if (channel.filter && !sender.hasPermission("huskchat.bypass_filters")) {
            for (ChatFilter filter : Settings.chatFilters.get(channel.id)) {
                if (sender.hasPermission(filter.getFilterIgnorePermission())) {
                    continue;
                }
                if (!filter.isAllowed(sender, message.toString())) {
                    implementor.getMessageManager().sendMessage(sender, filter.getFailureErrorMessageId());
                    return false;
                }

                if (filter instanceof ReplacerFilter replacer && !channel.broadcastScope.isPassThrough) {
                    String msg = message.toString();
                    message.delete(0, message.length());
                    message.append(replacer.replace(msg));
                }
            }
        }

        return true;
    }
}