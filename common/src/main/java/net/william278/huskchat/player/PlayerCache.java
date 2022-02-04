package net.william278.huskchat.player;

import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.message.MessageManager;

import java.util.HashMap;
import java.util.UUID;

/**
 * Static class storing and managing player data
 */
public class PlayerCache {

    // Player channels map
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
}
