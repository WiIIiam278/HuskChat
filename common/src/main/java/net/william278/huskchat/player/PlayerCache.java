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
        Channel channel = Settings.channels.get(channelID);
        if (channel == null) {
            messageManager.sendMessage(player, "error_invalid_channel");
            return;
        }

        if (channel.sendPermission != null) {
            if (!player.hasPermission(channel.sendPermission)) {
                messageManager.sendMessage(player, "error_no_permission_send", channel.id);
                return;
            }
        }
        setPlayerChannel(player.getUuid(), channel.id);
        messageManager.sendMessage(player, "channel_switched", channel.id);
    }


    // Map of users last private message target for /reply command
    private static final HashMap<UUID, HashSet<UUID>> lastMessagePlayers = new HashMap<>();

    public static Optional<HashSet<UUID>> getLastMessengers(UUID uuid) {
        if (lastMessagePlayers.containsKey(uuid)) {
            return Optional.of(lastMessagePlayers.get(uuid));
        }
        return Optional.empty();
    }

    public static void setLastMessenger(UUID playerToSet, ArrayList<Player> lastMessengers) {
        final HashSet<UUID> uuidPlayers = new HashSet<>();
        for (Player player : lastMessengers) {
            uuidPlayers.add(player.getUuid());
        }
        lastMessagePlayers.put(playerToSet, uuidPlayers);
    }


    // Set of players social spying
    private static final HashMap<UUID, SpyColor> socialSpies = new HashMap<>();

    public static boolean isSocialSpying(Player player) {
        return socialSpies.containsKey(player.getUuid());
    }

    public static void setSocialSpy(Player player) throws IOException {
        socialSpies.put(player.getUuid(), SpyColor.DEFAULT_SPY_COLOR);
        addSpy("social", player.getUuid(), SpyColor.DEFAULT_SPY_COLOR);
    }

    public static void setSocialSpy(Player player, SpyColor spyColor) throws IOException {
        socialSpies.put(player.getUuid(), spyColor);
        addSpy("social", player.getUuid(), spyColor);
    }

    public static void removeSocialSpy(Player player) throws IOException {
        socialSpies.remove(player.getUuid());
        removeSpy("social", player.getUuid());
    }

    // Determines who is going to receive a spy message
    public static HashMap<Player, SpyColor> getSocialSpyMessageReceivers(ArrayList<Player> messageRecipients, HuskChat implementor) {
        final HashMap<Player, SpyColor> receivers = new HashMap<>();

        calculateSpies:
        for (UUID player : socialSpies.keySet()) {
            final SpyColor color = socialSpies.get(player);
            final Optional<Player> spy = implementor.getPlayer(player);
            if (spy.isEmpty()) {
                continue;
            }
            for (Player messageRecipient : messageRecipients) {
                if (player.equals(messageRecipient.getUuid())) {
                    continue calculateSpies;
                }
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

    public static void setLocalSpy(Player player) throws IOException {
        localSpies.put(player.getUuid(), SpyColor.DEFAULT_SPY_COLOR);
        addSpy("local", player.getUuid(), SpyColor.DEFAULT_SPY_COLOR);
    }

    public static void setLocalSpy(Player player, SpyColor spyColor) throws IOException {
        localSpies.put(player.getUuid(), spyColor);
        addSpy("local", player.getUuid(), spyColor);
    }

    public static void removeLocalSpy(Player player) throws IOException {
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
    public static void loadSpy() throws IOException {
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
    }

    // Adds spy state to data file
    public static void addSpy(String type, UUID uuid, SpyColor spyColor) throws IOException {
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
    }

    // Removes spy state from data file
    public static void removeSpy(String type, UUID uuid) throws IOException {
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
