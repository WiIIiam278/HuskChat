package net.william278.huskchat.bungeecord.player;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.william278.huskchat.player.Player;

import java.util.Optional;
import java.util.UUID;

/**
 * Bungee implementation of a cross-platform {@link Player}
 */
public class BungeePlayer implements Player {

    private BungeePlayer() {
    }

    private ProxiedPlayer player;

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public UUID getUuid() {
        return player.getUniqueId();
    }

    @Override
    public int getPing() {
        return player.getPing();
    }

    @Override
    public String getServerName() {
        return player.getServer().getInfo().getName();
    }

    @Override
    public int getPlayersOnServer() {
        return player.getServer().getInfo().getPlayers().size();
    }

    @Override
    public boolean hasPermission(String s) {
        return player.hasPermission(s);
    }

    @Override
    public void passthroughChat(String s) {
        player.chat(s);
    }

    /**
     * Adapts a cross-platform {@link Player} to a bungee {@link CommandSender} object
     *
     * @param player {@link Player} to adapt
     * @return The {@link ProxiedPlayer} object, {@code null} if they are offline
     */
    public static Optional<ProxiedPlayer> adaptBungee(Player player) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player.getUuid());
        if (proxiedPlayer != null) {
            return Optional.of(proxiedPlayer);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Adapts a bungee {@link ProxiedPlayer} to a cross-platform {@link Player} object
     *
     * @param player {@link ProxiedPlayer} to adapt
     * @return The {@link Player} object
     */
    public static BungeePlayer adaptCrossPlatform(ProxiedPlayer player) {
        BungeePlayer bungeePlayer = new BungeePlayer();
        bungeePlayer.player = player;
        return bungeePlayer;
    }
}
