package net.william278.huskchat.message;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.player.Player;

import java.util.HashSet;
import java.util.logging.Level;

public class ChatMessage {

    public final String targetChannelId;
    public final Player sender;
    public final String message;
    private final HuskChat implementor;

    public ChatMessage(String targetChannelId, Player sender, String message, HuskChat implementor) {
        this.targetChannelId = targetChannelId;
        this.sender = sender;
        this.message = message;
        this.implementor = implementor;
    }

    public final void dispatch() {
        for (Channel channel : Settings.channels) {
            if (channel.id.equalsIgnoreCase(targetChannelId)) {
                if (channel.sendPermission != null) {
                    if (!sender.hasPermission(channel.sendPermission)) {
                        implementor.getMessageManager().sendMessage(sender, "error_no_permission_send", channel.id);
                        return;
                    }
                }

                // Determine the players who will receive the message;
                Channel.BroadcastScope broadcastScope = channel.broadcastScope;

                // If the message is to be passed through, run that
                if (broadcastScope.isPassThrough) {
                    sender.passthroughChat(message);
                }

                HashSet<Player> messageRecipients = new HashSet<>();
                switch (broadcastScope) {
                    case GLOBAL, GLOBAL_PASSTHROUGH -> messageRecipients.addAll(implementor.getOnlinePlayers());
                    case LOCAL, LOCAL_PASSTHROUGH -> messageRecipients.addAll(implementor.getOnlinePlayersOnServer(sender));
                    default -> {
                        return;
                    }
                }

                // Dispatch message to all applicable users in the scope with permission
                for (Player recipient : messageRecipients) {
                    if (channel.receivePermission != null) {
                        if (!recipient.hasPermission(channel.receivePermission) && !(recipient.getUuid().equals(sender.getUuid()))) {
                            continue;
                        }
                    }
                    implementor.getMessageManager().sendFormattedChannelMessage(recipient, sender, channel, message);
                }

                // Log message to console if enabled on the channel
                if (channel.logMessages) {
                    String logMessage = Settings.messageLogFormat;
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
