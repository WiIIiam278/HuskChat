package net.william278.huskchat.message;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.filter.ChatFilter;
import net.william278.huskchat.filter.replacer.ReplacerFilter;
import net.william278.huskchat.player.ConsolePlayer;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

/**
 * Represents a private message to be sent to a target user
 */
public class PrivateMessage {
    private Player sender;
    private List<String> targetUsernames;
    private String message;
    private HuskChat implementor;

    public PrivateMessage(Player sender, List<String> targetUsernames,
                          String message, HuskChat implementor) {
        this.sender = sender;
        this.targetUsernames = targetUsernames;
        this.message = message;
        this.implementor = implementor;
    }

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

        // Verify that the player is not sending a group message when they are turned off
        if (targetUsernames.size() > 1 && !Settings.doGroupMessages) {
            implementor.getMessageManager().sendMessage(sender, "error_group_messages_disabled");
            return;
        }

        // Validate message targets
        final ArrayList<Player> targetPlayers = new ArrayList<>();
        final HashSet<UUID> targetUUIDs = new HashSet<>();
        for (String targetUsername : targetUsernames) {
            Optional<Player> targetPlayer;
            if (ConsolePlayer.isConsolePlayer(targetUsername)) {
                targetPlayer = Optional.of(ConsolePlayer.adaptConsolePlayer(implementor));
            } else {
                targetPlayer = implementor.matchPlayer(targetUsername);
            }

            if (targetPlayer.isPresent()) {
                // Prevent sending messages to yourself
                if (targetPlayer.get().getUuid().equals(sender.getUuid())) {
                    implementor.getMessageManager().sendMessage(sender, "error_cannot_message_self");
                    return;
                }

                // Remove duplicate users from array
                if (targetUUIDs.contains(targetPlayer.get().getUuid())) {
                    continue;
                }

                targetPlayers.add(targetPlayer.get());
                targetUUIDs.add(targetPlayer.get().getUuid());
            }
        }

        // Validate that there aren't too many users
        if (targetPlayers.size() > Settings.maxGroupMessageSize) {
            implementor.getMessageManager().sendMessage(sender, "error_group_messages_max", Integer.toString(Settings.maxGroupMessageSize));
            return;
        }

        // Validate that the message has recipients
        if (targetPlayers.isEmpty()) {
            if (targetUsernames.size() > 1) {
                implementor.getMessageManager().sendMessage(sender, "error_players_not_found");
            } else {
                implementor.getMessageManager().sendMessage(sender, "error_player_not_found");
            }
            return;
        }

        AtomicReference<String> finalMessage = new AtomicReference<>(message);

        // If the message is to be filtered, then perform filter checks (unless they have the bypass permission)
        if (!sender.hasPermission("huskchat.bypass_filters")) {
            for (ChatFilter filter : Settings.chatFilters.get("private_messages")) {
                if (sender.hasPermission(filter.getFilterIgnorePermission())) {
                    continue;
                }
                if (!filter.isAllowed(sender, finalMessage.get())) {
                    implementor.getMessageManager().sendMessage(sender, filter.getFailureErrorMessageId());
                    return;
                }
                if (filter instanceof ReplacerFilter replacer) {
                    finalMessage.set(replacer.replace(finalMessage.get()));
                }
            }
        }

        implementor.getEventDispatcher().dispatchPrivateMessageEvent(sender, targetPlayers, finalMessage.get()).thenAccept(event -> {
            if (event.isCancelled()) return;

            sender = event.getSender();
            ArrayList<Player> receivers = event.getRecipients();
            finalMessage.set(event.getMessage());

            // Show that the message has been sent
            PlayerCache.setLastMessenger(sender.getUuid(), receivers);
            implementor.getMessageManager().sendFormattedOutboundPrivateMessage(sender, receivers, finalMessage.get());

            // Show the received message
            for (Player target : receivers) {
                final ArrayList<Player> receivedMessageFrom = new ArrayList<>(receivers);
                receivedMessageFrom.removeIf(player -> player.getUuid().equals(target.getUuid()));
                receivedMessageFrom.add(0, sender);

                PlayerCache.setLastMessenger(target.getUuid(), receivedMessageFrom);
            }
            implementor.getMessageManager().sendFormattedInboundPrivateMessage(receivers, sender, finalMessage.get());

            // Show message to social spies
            if (Settings.doSocialSpyCommand) {
                if (!(sender.hasPermission("huskchat.command.socialspy.bypass") || receivers.stream().findFirst().get().hasPermission("huskchat.command.socialspy.bypass"))) {
                    final HashMap<Player, PlayerCache.SpyColor> spies = PlayerCache.getSocialSpyMessageReceivers(receivers, implementor);
                    for (Player spy : spies.keySet()) {
                        if (spy.getUuid().equals(sender.getUuid())) {
                            continue;
                        }
                        if (!spy.hasPermission("huskchat.command.socialspy")) {
                            try {
                                PlayerCache.removeSocialSpy(spy);
                            } catch (IOException e) {
                                implementor.getLoggingAdapter().log(Level.SEVERE, "Failed to remove social spy after failed permission check", e);
                            }
                            continue;
                        }
                        final PlayerCache.SpyColor color = spies.get(spy);
                        implementor.getMessageManager().sendFormattedSocialSpyMessage(spy, color, sender, receivers, finalMessage.get());
                    }
                }

            }

            // Log the private message to console, if that is enabled
            if (Settings.logPrivateMessages) {
                // Log all recipients of the message
                final StringJoiner formattedPlayers = new StringJoiner(", ");
                for (Player player : receivers) {
                    formattedPlayers.add(player.getName());
                }

                String logFormat = Settings.messageLogFormat;
                logFormat = logFormat.replaceAll("%sender%", sender.getName());
                logFormat = logFormat.replaceAll("%receiver%", formattedPlayers.toString());

                implementor.getLoggingAdapter().log(Level.INFO, logFormat + finalMessage);
            }
        });
    }

}