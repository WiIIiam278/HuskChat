package net.william278.huskchat.player;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.william278.huskchat.HuskChat;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.message.MessageManager;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Static class storing and managing player data
 */
public class PlayerCache {
    private static File dataFolder;

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
        addSpy("social", player.getUuid(), SpyColor.DEFAULT_SPY_COLOR);
    }

    public static void setSocialSpy(Player player, SpyColor spyColor) {
        socialSpies.put(player.getUuid(), spyColor);
        addSpy("social", player.getUuid(), spyColor);
    }

    public static void removeSocialSpy(Player player) {
        socialSpies.remove(player.getUuid());
        removeSpy("social", player.getUuid());
    }

    public static HashMap<Player, SpyColor> getSocialSpyMessageReceivers(UUID messageRecipient, HuskChat implementor) {
        HashMap<Player, SpyColor> receivers = new HashMap<>();
        for (UUID player : socialSpies.keySet()) {
            final SpyColor color = socialSpies.get(player);
            final Optional<Player> spy = implementor.getPlayer(player);
            if (spy.isEmpty()) {
                continue;
            }
            if (player.equals(messageRecipient)) {
                continue;
            }
            receivers.put(spy.get(), color);
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
        addSpy("local", player.getUuid(), SpyColor.DEFAULT_SPY_COLOR);
    }

    public static void setLocalSpy(Player player, SpyColor spyColor) {
        localSpies.put(player.getUuid(), spyColor);
        addSpy("local", player.getUuid(), spyColor);
    }

    public static void removeLocalSpy(Player player) {
        localSpies.remove(player.getUuid());
        removeSpy("local", player.getUuid());
    }

    public static HashMap<Player, SpyColor> getLocalSpyMessageReceivers(String localMessageServer, HuskChat implementor) {
        HashMap<Player, SpyColor> receivers = new HashMap<>();
        for (UUID player : localSpies.keySet()) {
            final SpyColor color = localSpies.get(player);
            final Optional<Player> spy = implementor.getPlayer(player);
            if (spy.isEmpty()) {
                continue;
            }
            if (spy.get().getServerName().equals(localMessageServer)) {
                continue;
            }
            receivers.put(spy.get(), color);
        }
        return receivers;
    }

    // Load local and social spy data into maps
    public static void loadSpy() {
        try {
            YamlDocument spies = YamlDocument.create(new File(dataFolder, "spies.yml"));

            if (spies.contains("local")) {
                Section local = spies.getSection("local");

                for (Object name : local.getKeys()) {
                    localSpies.put(UUID.fromString(name.toString()),
                            SpyColor.valueOf(local.getSection(name.toString()).getString("color")));
                }
            }

            if (spies.contains("social")) {
                Section social = spies.getSection("social");

                for (Object name : social.getKeys()) {
                    socialSpies.put(UUID.fromString(name.toString()),
                            SpyColor.valueOf(social.getSection(name.toString()).getString("color")));
                }
            }
        } catch(IOException e) {
            // TODO: Use logger
            e.printStackTrace();
        }
    }

    // Adds spy state to data file
    public static void addSpy(String type, UUID uuid, SpyColor spyColor) {
        try {
            if (!type.equals("local") && !type.equals("social")) return;

            YamlDocument spies = YamlDocument.create(new File(dataFolder, "spies.yml"));

            if (!spies.contains(type)) {
                spies.createSection(type);
            }

            if (!spies.getSection(type).contains(uuid.toString())) {
                spies.getSection(type).createSection(uuid.toString());
            }

            spies.getSection(type)
                    .getSection(uuid.toString())
                    .set("color", spyColor.toString());
            spies.save();
        } catch (IOException e) {
            // TODO: Use logger
            e.printStackTrace();
        }
    }

    // Removes spy state from data file
    public static void removeSpy(String type, UUID uuid) {
        try {
            if (!type.equals("local") && !type.equals("social")) return;

            YamlDocument spies = YamlDocument.create(new File(dataFolder, "spies.yml"));

            if (!spies.contains(type)) return;
            if (!spies.getSection(type).contains(uuid.toString())) return;

            spies.getSection(type)
                    .remove(uuid.toString());

            if (spies.getSection(type).getKeys().size() == 0) {
                spies.remove(type);
            }

            spies.save();
        } catch (IOException e) {
            // TODO: Use logger
            e.printStackTrace();
        }
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

        public static Optional<SpyColor> getColor(String colorInput) {
            for (SpyColor color : SpyColor.values()) {
                if (color.colorCode.replace("&", "").equals(colorInput.replace("&", ""))
                        || color.name().equalsIgnoreCase(colorInput.toUpperCase())) {
                    return Optional.of(color);
                }
            }
            return Optional.empty();
        }
    }

    // Sets the data directory so that social spy state can be persisted
    public static void setDataFolder(File folder) {
        dataFolder = folder;
    }
}
