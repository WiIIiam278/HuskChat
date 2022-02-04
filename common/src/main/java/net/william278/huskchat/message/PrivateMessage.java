package net.william278.huskchat.message;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;

public class PrivateMessage {

    public final String targetUsername;
    public final Player sender;
    public final String message;
    private final HuskChat implementor;

    public PrivateMessage(Player sender, String recipient, String message, HuskChat implementor) {
        this.targetUsername = recipient;
        this.sender = sender;
        this.message = message;
        this.implementor = implementor;
    }

    public final void dispatch() {
        if (targetUsername.equalsIgnoreCase(sender.getName())) {
            implementor.getMessageManager().sendMessage(sender, "error_cannot_message_self");
            return;
        }

        for (Player target : implementor.getOnlinePlayers()) {
            if (target.getName().equalsIgnoreCase(targetUsername)) {
                // Show that the message has been sent
                PlayerCache.setLastMessenger(sender.getUuid(), target.getUuid());
                implementor.getMessageManager().sendFormattedOutboundPrivateMessage(target, sender, message);


                // Show the received message
                PlayerCache.setLastMessenger(target.getUuid(), sender.getUuid());
                implementor.getMessageManager().sendFormattedInboundPrivateMessage(target, sender, message);

                // Log to console if enabled
                if (Settings.logPrivateMessages) {
                    implementor.getLoggingAdapter().info("[MSG] " + sender.getName() + " → " + target.getName() + ": " + message);
                }
                return;
            }
        }
        implementor.getMessageManager().sendMessage(sender, "error_player_not_found");
    }

}
