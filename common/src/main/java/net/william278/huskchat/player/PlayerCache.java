package net.william278.huskchat.player;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.message.MessageManager;

import java.util.*;

/**
 * Static class storing and managing player data
 */
public class PlayerCache {

    // Map of users to their current channel
    private static final HashMap<UUID, String> playerChannels = new HashMap<>();

    public static String getPlayerChannel(UUID uuid) {
        if (!playerChannels.containsKey(uuid)) {
            return Settings.defaultChannel;
        }
        return playerChannels.get(uuid);
    }

    public static void setPlayerChannel(UUID uuid, String playerChannel) {
        playerChannels.put(uuid, playerChannel);
    }


    /**
     * Switch the {@link Player}'s channel
     *
     * @param player         {@link Player} to switch the channel of
     * @param channelID      ID of the channel to switch to
     * @param messageManager Instance of the {@link MessageManager} to display switch information via
     */
    public static void switchPlayerChannel(Player player, String channelID, MessageManager messageManager) {
        for (Channel channel : Settings.channels) {
            if (channel.id.equalsIgnoreCase(channelID)) {
                if (channel.sendPermission != null) {
                    if (!player.hasPermission(channel.sendPermission)) {
                        messageManager.sendMessage(player, "error_no_permission_send", channel.id);
                        return;
                    }
                }
                setPlayerChannel(player.getUuid(), channel.id);
                messageManager.sendMessage(player, "channel_switched", channel.id);
                return;
            }
        }
        messageManager.sendMessage(player, "error_invalid_channel");
    }


    // Map of users last private message target for /reply command
    private static final HashMap<UUID, UUID> lastMessagePlayers = new HashMap<>();

    public static UUID getLastMessenger(UUID uuid) {
        if (lastMessagePlayers.containsKey(uuid)) {
            return lastMessagePlayers.get(uuid);
        }
        return null;
    }

    public static void setLastMessenger(UUID playerToSet, UUID lastMessenger) {
        lastMessagePlayers.put(playerToSet, lastMessenger);
    }


    // Set of players social spying
    private static final HashMap<UUID, SpyColor> socialSpies = new HashMap<>();

    public static boolean isSocialSpying(Player player) {
        return socialSpies.containsKey(player.getUuid());
    }

    public static void setSocialSpy(Player player) {
        socialSpies.put(player.getUuid(), SpyColor.DEFAULT_SPY_COLOR);
    }

    public static void setSocialSpy(Player player, SpyColor spyColor) {
        socialSpies.put(player.getUuid(), spyColor);
    }

    public static void removeSocialSpy(Player player) {
        socialSpies.remove(player.getUuid());
    }

    public static HashMap<Player, SpyColor> getSocialSpyMessageReceivers(UUID messageRecipient, HuskChat implementor) {
        HashMap<Player, SpyColor> receivers = new HashMap<>();
        for (UUID player : socialSpies.keySet()) {
            final SpyColor color = socialSpies.get(player);
            final Player spy = implementor.getPlayer(player);
            if (player.equals(messageRecipient)) {
                continue;
            }
            receivers.put(spy, color);
        }
        return receivers;
    }


    // Set of players local spying
    private static final HashMap<UUID, SpyColor> localSpies = new HashMap<>();

    public static boolean isLocalSpying(Player player) {
        return localSpies.containsKey(player.getUuid());
    }

    public static void setLocalSpy(Player player) {
        localSpies.put(player.getUuid(), SpyColor.DEFAULT_SPY_COLOR);
    }

    public static void setLocalSpy(Player player, SpyColor spyColor) {
        localSpies.put(player.getUuid(), spyColor);
    }

    public static void removeLocalSpy(Player player) {
        localSpies.remove(player.getUuid());
    }

    public static HashMap<Player, SpyColor> getLocalSpyMessageReceivers(HashSet<Player> existingRecipients, String localMessageServer, HuskChat implementor) {
        HashMap<Player, SpyColor> receivers = new HashMap<>();
        for (UUID player : localSpies.keySet()) {
            final SpyColor color = localSpies.get(player);
            final Player spy = implementor.getPlayer(player);
            if (spy.getServerName().equals(localMessageServer)) {
                continue;
            }
            receivers.put(spy, color);
        }
        return receivers;
    }


    /**
     * Color used for displaying chat
     */
    public enum SpyColor {
        DARK_RED("&4"),
        RED("&c"),
        GOLD("&6"),
        YELLOW("&e"),
        DARK_GREEN("&2"),
        GREEN("&a"),
        AQUA("&b"),
        DARK_AQUA("&3"),
        DARK_BLUE("&1"),
        BLUE("&9"),
        LIGHT_PURPLE("&d"),
        DARK_PURPLE("&5"),
        WHITE("&f"),
        GRAY("&7"),
        DARK_GRAY("&8"),
        BLACK("&9");

        public static final SpyColor DEFAULT_SPY_COLOR = DARK_GRAY;

        public final String colorCode;

        SpyColor(String colorCode) {
            this.colorCode = colorCode;
        }

        public static List<String> getColorStrings() {
            List<String> colors = new ArrayList<>();
            for (SpyColor color : SpyColor.values()) {
                colors.add(color.name().toLowerCase());
            }
            return colors;
        }

        public static Optional<SpyColor> getColor(String colorCode) {
            for (SpyColor color : SpyColor.values()) {
                if (color.colorCode.equals(colorCode) || color.colorCode.equalsIgnoreCase(color.name())) {
                    return Optional.of(color);
                }
            }
            return Optional.empty();
        }
    }
}
