package net.william278.huskchat.bungeecord;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.william278.huskchat.HuskChat;
import net.william278.huskchat.bungeecord.command.*;
import net.william278.huskchat.bungeecord.config.BungeeConfigFile;
import net.william278.huskchat.bungeecord.config.BungeeMessageManager;
import net.william278.huskchat.bungeecord.listener.BungeeListener;
import net.william278.huskchat.bungeecord.player.BungeePlayer;
import net.william278.huskchat.bungeecord.util.BungeeLogger;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.command.*;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.getter.BungeePermsDataGetter;
import net.william278.huskchat.getter.DataGetter;
import net.william278.huskchat.getter.DefaultDataGetter;
import net.william278.huskchat.getter.LuckPermsDataGetter;
import net.william278.huskchat.message.MessageManager;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.util.Logger;
import org.bstats.bungeecord.Metrics;

import java.util.*;

public final class HuskChatBungee extends Plugin implements HuskChat {

    // Metrics ID
    private static final int METRICS_ID = 11882;

    // Instance provider
    private static HuskChatBungee instance;

    public static HuskChatBungee getInstance() {
        return instance;
    }

    // Message manager
    public static BungeeMessageManager messageManager;

    // Player data fetcher
    public static DataGetter playerDataGetter;

    @Override
    public void onLoad() {
        // Set instance for easy cross-class referencing
        instance = this;
    }

    @Override
    public void onEnable() {
        // Load config
        reloadSettings();

        // Load messages
        reloadMessages();

        // Setup player data getter
        Plugin luckPerms = ProxyServer.getInstance().getPluginManager().getPlugin("LuckPerms");
        if (luckPerms != null) {
            playerDataGetter = new LuckPermsDataGetter();
        } else {
            Plugin bungeePerms = ProxyServer.getInstance().getPluginManager().getPlugin("BungeePerms");
            if (bungeePerms != null) {
                playerDataGetter = new BungeePermsDataGetter();
            } else {
                playerDataGetter = new DefaultDataGetter();
            }
        }

        // Register events
        getProxy().getPluginManager().registerListener(this, new BungeeListener());

        // Register commands
        new BungeeCommand(new HuskChatCommand(this));
        new BungeeCommand(new ChannelCommand(this));

        if (Settings.doMessageCommand) {
            new BungeeCommand(new MsgCommand(this));
            new BungeeCommand(new ReplyCommand(this));
        }

        // Register shortcut commands
        for (Channel channel : Settings.channels) {
            for (String command : channel.shortcutCommands) {
                new BungeeCommand(new ShortcutCommand(command, channel.id, this));
            }
        }

        // Initialise metrics
        new Metrics(this, METRICS_ID);

        // Plugin startup logic
        getLogger().info("Enabled HuskChat version " + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Disabled HuskChat version " + getDescription().getVersion());
    }

    @Override
    public void reloadMessages() {
        messageManager = new BungeeMessageManager();
    }

    @Override
    public MessageManager getMessageManager() {
        return messageManager;
    }

    @Override
    public void reloadSettings() {
        Settings.load(new BungeeConfigFile("config.yml", "config.yml"));
    }

    @Override
    public String getMetaVersion() {
        return getDescription().getVersion();
    }

    @Override
    public String getMetaDescription() {
        return getDescription().getDescription();
    }

    @Override
    public String getMetaPlatform() {
        return ProxyServer.getInstance().getName();
    }

    @Override
    public DataGetter getDataGetter() {
        return playerDataGetter;
    }

    @Override
    public Player getPlayer(UUID uuid) {
        return BungeePlayer.adaptCrossPlatform(ProxyServer.getInstance().getPlayer(uuid));
    }

    @Override
    public Collection<Player> getOnlinePlayers() {
        ArrayList<Player> crossPlatform = new ArrayList<>();
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            crossPlatform.add(BungeePlayer.adaptCrossPlatform(player));
        }
        return crossPlatform;
    }

    @Override
    public Collection<Player> getOnlinePlayersOnServer(Player player) {
        ArrayList<Player> crossPlatform = new ArrayList<>();
        for (ProxiedPlayer playerOnServer : ProxyServer.getInstance().getPlayer(player.getUuid()).getServer().getInfo().getPlayers()) {
            crossPlatform.add(BungeePlayer.adaptCrossPlatform(playerOnServer));
        }
        return crossPlatform;
    }

    @Override
    public Logger getLoggingAdapter() {
        return BungeeLogger.get();
    }

    @Override
    public Optional<Player> matchPlayer(String username) {
        final Optional<Player> optionalPlayer;
        if (ProxyServer.getInstance().getPlayer(username) != null) {
            final ProxiedPlayer player = ProxyServer.getInstance().getPlayer(username);
            optionalPlayer = Optional.of(BungeePlayer.adaptCrossPlatform(player));
        } else {
            final List<ProxiedPlayer> matchedPlayers = ProxyServer.getInstance().matchPlayer(username)
                    .stream().filter(val -> val.getName().startsWith(username)).sorted().toList();
            if (matchedPlayers.size() > 0) {
                optionalPlayer = Optional.of(BungeePlayer.adaptCrossPlatform(matchedPlayers.get(0)));
            } else {
                optionalPlayer = Optional.empty();
            }
        }
        return optionalPlayer;
    }

}
