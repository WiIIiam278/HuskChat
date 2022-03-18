package net.william278.huskchat.getter;

import net.alpenblock.bungeeperms.BungeePerms;
import net.alpenblock.bungeeperms.PermissionsManager;
import net.alpenblock.bungeeperms.User;
import net.william278.huskchat.player.Player;

import java.util.Optional;

/**
 * A Data Getter that hooks with the BungeePerms API to fetch user prefixes / suffixes
 */
public class BungeePermsDataGetter extends DataGetter {

    private final PermissionsManager permissionsManager;

    public BungeePermsDataGetter() {
        super();
        permissionsManager = BungeePerms.getInstance().getPermissionsManager();
    }

    @Override
    public String getPlayerFullName(Player player) {
        final Optional<String> prefix = getPlayerPrefix(player);
        final Optional<String> suffix = getPlayerSuffix(player);
        return (prefix.isPresent() ? prefix : "") + player.getName().replaceAll("__", "\\__")
                + (suffix.isPresent() ? suffix : "");
    }

    @Override
    public String getPlayerName(Player player) {
        return player.getName().replaceAll("__", "\\__");
    }

    @Override
    public Optional<String> getPlayerPrefix(Player player) {
        try {
            return Optional.of(permissionsManager.getMainGroup(getUser(player)).getPrefix());
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getPlayerSuffix(Player player) {
        try {
            return Optional.of(permissionsManager.getMainGroup(getUser(player)).getSuffix());
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

    private User getUser(Player player) {
        return permissionsManager.getUser(player.getUuid());
    }
}
