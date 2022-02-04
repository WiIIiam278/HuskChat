package net.william278.huskchat.getter;

import net.william278.huskchat.player.Player;

/**
 * DataGetter, implemented by LuckPerms
 */
public abstract class DataGetter {

    public DataGetter() {
    }

    public abstract String getPlayerFullName(Player player);

    public abstract String getPlayerName(Player player);

    public abstract String getPlayerPrefix(Player player);

    public abstract String getPlayerSuffix(Player player);

}
