package net.william278.huskchat.message;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;

import java.util.logging.Level;

/**
 * Represents a private message to be sent to a target user
 */
public record PrivateMessage(Player sender, String targetUsername,
                             String message, HuskChat implementor) {

    /**
     * Dispatch the private message to be sent
     */
    public void dispatch() {
        // Verify that the player is not sending a message from a server where channel access is restricted
        for (String restrictedServer : Settings.messageCommandRestrictedServers) {
            if (restrictedServer.equalsIgnoreCase(sender.getServerName())) {
                implementor.getMessageManager().sendMessage(sender, "error_message_restricted_server");
                return;
            }
        }

        if (targetUsername.equalsIgnoreCase(sender.getName())) {
            implementor.getMessageManager().sendMessage(sender, "error_cannot_message_self");
            return;
        }

        implementor.matchPlayer(targetUsername).ifPresentOrElse(target -> {
            // Show that the message has been sent
            PlayerCache.setLastMessenger(sender.getUuid(), target.getUuid());
            implementor.getMessageManager().sendFormattedOutboundPrivateMessage(target, sender, message);

            // Show the received message
            PlayerCache.setLastMessenger(target.getUuid(), sender.getUuid());
            implementor.getMessageManager().sendFormattedInboundPrivateMessage(target, sender, message);

            // Log to console if enabled
            if (Settings.logPrivateMessages) {
                String logMessage = Settings.channelLogFormat;
                logMessage = logMessage.replaceAll("%sender%", sender.getName());
                logMessage = logMessage.replaceAll("%receiver%", target.getName());
                logMessage = logMessage.replaceAll("%message%", message);

                implementor.getLoggingAdapter().log(Level.INFO, logMessage);
            }
        }, () -> implementor.getMessageManager().sendMessage(sender, "error_player_not_found"));
    }

}