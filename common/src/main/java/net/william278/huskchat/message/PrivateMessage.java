package net.william278.huskchat.message;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;

import java.util.HashMap;
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

        implementor.matchPlayer(targetUsername).ifPresentOrElse(target -> {
            // Prevent sending messages to yourself
            if (target.getUuid().equals(sender.getUuid())) {
                implementor.getMessageManager().sendMessage(sender, "error_cannot_message_self");
                return;
            }

            // Show that the message has been sent
            PlayerCache.setLastMessenger(sender.getUuid(), target.getUuid());
            implementor.getMessageManager().sendFormattedOutboundPrivateMessage(sender, target, message);

            // Show the received message
            PlayerCache.setLastMessenger(target.getUuid(), sender.getUuid());
            implementor.getMessageManager().sendFormattedInboundPrivateMessage(target, sender, message);

            // Show message to social spies
            if (Settings.doSocialSpyCommand) {
                final HashMap<Player, PlayerCache.SpyColor> spies = PlayerCache.getSocialSpyMessageReceivers(target.getUuid(), implementor);
                for (Player spy : spies.keySet()) {
                    if (spy.getUuid().equals(sender.getUuid()) || spy.getUuid().equals(target.getUuid())) {
                        continue;
                    }
                    final PlayerCache.SpyColor color = spies.get(spy);
                    implementor.getMessageManager().sendFormattedSocialSpyMessage(spy, color, sender, target, message);
                }
            }

            // Log to console if enabled
            if (Settings.logPrivateMessages) {
                String logFormat = Settings.messageLogFormat;
                logFormat = logFormat.replaceAll("%sender%", sender.getName());
                logFormat = logFormat.replaceAll("%receiver%", target.getName());

                implementor.getLoggingAdapter().log(Level.INFO, logFormat + message);
            }
        }, () -> implementor.getMessageManager().sendMessage(sender, "error_player_not_found"));
    }

}