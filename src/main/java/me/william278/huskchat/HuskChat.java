package me.william278.huskchat;

import me.william278.huskchat.channels.Channel;
import me.william278.huskchat.commands.*;
import me.william278.huskchat.config.Config;
import me.william278.huskchat.config.ConfigManager;
import me.william278.huskchat.listeners.PlayerListener;
import me.william278.huskchat.messagedata.getter.Getter;
import me.william278.huskchat.messagedata.getter.LuckPermsDataGetter;
import me.william278.huskchat.messagedata.getter.RegularDataGetter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import org.bstats.bungeecord.Metrics;

import java.util.HashMap;
import java.util.UUID;

public final class HuskChat extends Plugin {

    // Instance getting
    private static HuskChat instance;
    public static HuskChat getInstance() {
        return instance;
    }

    private static Getter playerDataGetter;
    public static Getter getPlayerDataGetter() {
        return playerDataGetter;
    }

    private static final HashMap<UUID, String> playerChannels = new HashMap<>();
    public static String getPlayerChannel(UUID uuid) {
        if (!playerChannels.containsKey(uuid)) {
            return getConfig().getDefaultChannel();
        }
        return playerChannels.get(uuid);
    }
    public static void setPlayerChannel(UUID uuid, String playerChannel) { playerChannels.put(uuid, playerChannel); }

    private static Config config;
    public static Config getConfig() {
        return config;
    }
    public static void reloadConfig() {
        ConfigManager.loadConfig();
        Configuration configuration = ConfigManager.getConfig();
        if (configuration != null) {
            config = new Config(configuration);
        }
    }

    private static final HashMap<UUID,UUID> lastMessagePlayers = new HashMap<>();
    public static UUID getLastMessenger(UUID uuid) {
        if (lastMessagePlayers.containsKey(uuid)) {
            return lastMessagePlayers.get(uuid);
        }
        return null;
    }
    public static void setLastMessenger(UUID playerToSet, UUID lastMessenger) {
        lastMessagePlayers.put(playerToSet, lastMessenger);
    }

    @Override
    public void onLoad() {
        // Set instance for easy cross-class referencing
        instance = this;
    }

    @Override
    public void onEnable() {
        // Load config
        reloadConfig();

        // Load messages
        MessageManager.reloadMessages();

        // Setup player data getter
        Plugin luckPerms = ProxyServer.getInstance().getPluginManager().getPlugin("LuckPerms");
        if (luckPerms != null) {
            playerDataGetter = new LuckPermsDataGetter();
        } else {
            playerDataGetter = new RegularDataGetter();
        }

        // Register events
        getProxy().getPluginManager().registerListener(this, new PlayerListener());

        // Register commands
        getProxy().getPluginManager().registerCommand(this, new HuskChatCommand());
        getProxy().getPluginManager().registerCommand(this, new ChannelCommand());
        if (config.doMessageCommand()) {
            getProxy().getPluginManager().registerCommand(this, new MessageCommand());
            getProxy().getPluginManager().registerCommand(this, new ReplyCommand());
        }

        // Register shortcut commands
        for (Channel channel : config.getChannels()) {
            for (String command : channel.getShortcutCommands()) {
                getProxy().getPluginManager().registerCommand(this,
                        new ChannelShortcutCommand(command.substring(1), channel.getChannelId()));
            }
        }

        // Initialise metrics
        new Metrics(this, 11882);

        // Plugin startup logic
        getLogger().info("Enabled HuskChat version " + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Disabled HuskChat version " + getDescription().getVersion());
    }
}
