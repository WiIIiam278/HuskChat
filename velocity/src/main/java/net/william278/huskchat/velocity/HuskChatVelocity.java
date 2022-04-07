package net.william278.huskchat.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import net.william278.huskchat.HuskChat;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.command.*;
import net.william278.huskchat.config.Settings;
import net.william278.huskchat.getter.DataGetter;
import net.william278.huskchat.getter.DefaultDataGetter;
import net.william278.huskchat.getter.LuckPermsDataGetter;
import net.william278.huskchat.message.MessageManager;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.player.PlayerCache;
import net.william278.huskchat.util.Logger;
import net.william278.huskchat.velocity.command.VelocityCommand;
import net.william278.huskchat.velocity.config.VelocityMessageManager;
import net.william278.huskchat.velocity.listener.VelocityListener;
import net.william278.huskchat.velocity.player.VelocityPlayer;
import net.william278.huskchat.velocity.util.VelocityLogger;
import org.bstats.velocity.Metrics;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;

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

        // Load saved social spy state
        PlayerCache.setDataFolder(getDataFolder());

        try {
            PlayerCache.loadSpy();
        } catch (IOException e) {
            getLoggingAdapter().log(Level.SEVERE, "Failed to load spies file");
        }

        // Setup player data getter
        Optional<PluginContainer> luckPerms = getProxyServer().getPluginManager().getPlugin("luckperms");
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

        if (Settings.doBroadcastCommand) {
            new VelocityCommand(new BroadcastCommand(this));
        }

        if (Settings.doSocialSpyCommand) {
            new VelocityCommand(new SocialSpyCommand(this));
        }

        if (Settings.doLocalSpyCommand) {
            new VelocityCommand(new LocalSpyCommand(this));
        }

        // Register shortcut commands
        for (Channel channel : Settings.channels) {
            for (String command : channel.shortcutCommands) {
                new VelocityCommand(new ShortcutCommand(command, channel.id, this));
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
        try {
            Settings.load(YamlDocument.create(new File(getDataFolder(), "config.yml"),
                    Objects.requireNonNull(HuskChat.class.getClassLoader().getResourceAsStream("config.yml")),
                    GeneralSettings.builder().setUseDefaults(false).build(),
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build()));
        } catch (IOException e) {
            getLoggingAdapter().log(Level.SEVERE, "Failed to load config file");
        }
    }

    @Override
    public void reloadMessages() {
        try {
            messageManager = new VelocityMessageManager();
        } catch (IOException e) {
            getLoggingAdapter().log(Level.SEVERE, "Failed to load messages file");
        }
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
    public Optional<Player> getPlayer(UUID uuid) {
        final Optional<com.velocitypowered.api.proxy.Player> player = getProxyServer().getPlayer(uuid);
        return player.map(VelocityPlayer::adaptCrossPlatform);
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
        VelocityPlayer.adaptVelocity(serverPlayer).flatMap(com.velocitypowered.api.proxy.Player::getCurrentServer).ifPresent(serverConnection -> {
            for (com.velocitypowered.api.proxy.Player connectedPlayer : serverConnection.getServer().getPlayersConnected()) {
                velocityPlayers.add(VelocityPlayer.adaptCrossPlatform(connectedPlayer));
            }
        });
        return velocityPlayers;
    }

    @Override
    public Logger getLoggingAdapter() {
        return VelocityLogger.get(logger);
    }

    @Override
    public Optional<Player> matchPlayer(String username) {
        final Optional<Player> optionalPlayer;
        if (getProxyServer().getPlayer(username).isPresent()) {
            final com.velocitypowered.api.proxy.Player player = getProxyServer().getPlayer(username).get();
            optionalPlayer = Optional.of(VelocityPlayer.adaptCrossPlatform(player));
        } else {
            final List<com.velocitypowered.api.proxy.Player> matchedPlayers = getProxyServer().matchPlayer(username)
                    .stream().filter(val -> val.getUsername().startsWith(username)).sorted().toList();
            if (matchedPlayers.size() > 0) {
                optionalPlayer = Optional.of(VelocityPlayer.adaptCrossPlatform(matchedPlayers.get(0)));
            } else {
                optionalPlayer = Optional.empty();
            }
        }
        return optionalPlayer;
    }
}
