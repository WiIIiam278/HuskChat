package net.william278.huskchat.getter;

import net.william278.huskchat.player.Player;

import java.util.Optional;

/**
 * Used for fetching data about users from third party plugins
 */
public abstract class DataGetter {

    public abstract String getPlayerFullName(Player player);

    public abstract String getPlayerName(Player player);

    public abstract Optional<String> getPlayerPrefix(Player player);

    public abstract Optional<String> getPlayerSuffix(Player player);

}
