package net.william278.huskchat.message;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.player.Player;

import java.util.logging.Level;

/**
 * Represents a broadcast message to be sent to everyone
 */
public record BroadcastMessage(String message, HuskChat implementor) {

    /**
     * Dispatch the broadcast message to be sent
     */
    public void dispatch() {
        // Dispatch message to all players
        for (Player player : implementor.getOnlinePlayers()) {
            implementor.getMessageManager().sendFormattedBroadcastMessage(player, message);
        }

        // Log to console if enabled
        if (Settings.logBroadcasts) {
            implementor.getLoggingAdapter().log(Level.INFO, Settings.broadcastLogFormat + message);
        }
    }

}