package net.william278.huskchat.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.william278.huskchat.HuskChat;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.command.*;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.getter.DataGetter;
import net.william278.huskchat.getter.DefaultDataGetter;
import net.william278.huskchat.getter.LuckPermsDataGetter;
import net.william278.huskchat.message.MessageManager;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.util.Logger;
import net.william278.huskchat.velocity.command.VelocityCommand;
import net.william278.huskchat.velocity.config.VelocityConfigFile;
import net.william278.huskchat.velocity.config.VelocityMessageManager;
import net.william278.huskchat.velocity.listener.VelocityListener;
import net.william278.huskchat.velocity.player.VelocityPlayer;
import net.william278.huskchat.velocity.util.VelocityLogger;
import org.bstats.velocity.Metrics;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Plugin(id = "huskchat")
public class HuskChatVelocity implements HuskChat {

    // Plugin version
    public static String VERSION = null;
    public static String DESCRIPTION = null;

    // Velocity bStats ID
    private static final int METRICS_ID = 14187;
    private final Metrics.Factory metricsFactory;

    private static HuskChatVelocity instance;

    public static HuskChatVelocity getInstance() {
        return instance;
    }

    // Message manager
    public static VelocityMessageManager messageManager;

    // Player data fetcher
    public static DataGetter playerDataGetter;

    private final org.slf4j.Logger logger;
    private final ProxyServer server;
    private final Path dataDirectory;

    // Get the data folder
    public File getDataFolder() {
        return dataDirectory.toFile();
    }

    // Get the proxy server
    public ProxyServer getProxyServer() {
        return server;
    }

    @Inject
    public HuskChatVelocity(ProxyServer server, org.slf4j.Logger logger, @DataDirectory Path dataDirectory,
                            Metrics.Factory metricsFactory, PluginContainer pluginContainer) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.metricsFactory = metricsFactory;
        pluginContainer.getDescription().getVersion().ifPresent(versionString -> VERSION = versionString);
        pluginContainer.getDescription().getDescription().ifPresent(descriptionString -> DESCRIPTION = descriptionString);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        instance = this;

        // Load config
        reloadSettings();

        // Load messages
        reloadMessages();

        // Setup player data getter
        Optional<PluginContainer> luckPerms = getProxyServer().getPluginManager().getPlugin("LuckPerms");
        if (luckPerms.isPresent()) {
            playerDataGetter = new LuckPermsDataGetter();
        } else {
            playerDataGetter = new DefaultDataGetter();
        }

        // Register events
        getProxyServer().getEventManager().register(this, new VelocityListener());

        // Register commands
        new VelocityCommand(new HuskChatCommand(this));
        new VelocityCommand(new ChannelCommand(this));

        if (Settings.doMessageCommand) {
            new VelocityCommand(new MsgCommand(this));
            new VelocityCommand(new ReplyCommand(this));
        }

        // Register shortcut commands
        for (Channel channel : Settings.channels) {
            for (String command : channel.shortcutCommands) {
                new VelocityCommand(new ShortcutCommand(command.substring(1), channel.id, this));
            }
        }

        // Initialise metrics
        metricsFactory.make(this, METRICS_ID);

        // Plugin startup logic
        getLoggingAdapter().info("Enabled HuskChat version " + getMetaVersion());
    }

    @Override
    public MessageManager getMessageManager() {
        return messageManager;
    }

    @Override
    public void reloadSettings() {
        Settings.load(new VelocityConfigFile("config.yml", "config.yml"));
    }

    @Override
    public void reloadMessages() {
        messageManager = new VelocityMessageManager();
    }

    @Override
    public String getMetaVersion() {
        return VERSION;
    }

    @Override
    public String getMetaDescription() {
        return DESCRIPTION;
    }

    @Override
    public String getMetaPlatform() {
        return "Velocity";
    }

    @Override
    public DataGetter getDataGetter() {
        return playerDataGetter;
    }

    @Override
    public Player getPlayer(UUID uuid) {
        AtomicReference<com.velocitypowered.api.proxy.Player> player = new AtomicReference<>();
        getProxyServer().getPlayer(uuid).ifPresent(player::set);
        return VelocityPlayer.adaptCrossPlatform(player.get());
    }

    @Override
    public Collection<Player> getOnlinePlayers() {
        final ArrayList<Player> velocityPlayers = new ArrayList<>();
        for (com.velocitypowered.api.proxy.Player player : getProxyServer().getAllPlayers()) {
            velocityPlayers.add(VelocityPlayer.adaptCrossPlatform(player));
        }
        return velocityPlayers;
    }

    @Override
    public Collection<Player> getOnlinePlayersOnServer(Player serverPlayer) {
        final ArrayList<Player> velocityPlayers = new ArrayList<>();
        VelocityPlayer.adaptVelocity(serverPlayer).getCurrentServer().ifPresent(serverConnection -> {
            for (com.velocitypowered.api.proxy.Player player : serverConnection.getServer().getPlayersConnected()) {
                velocityPlayers.add(VelocityPlayer.adaptCrossPlatform(player));
            }
        });
        return velocityPlayers;
    }

    @Override
    public Logger getLoggingAdapter() {
        return VelocityLogger.get(logger);
    }
}
