package net.william278.huskchat.getter;

import net.william278.huskchat.player.Player;

/**
 * The default Data Getter if LuckPerms is not installed
 */
public class DefaultDataGetter extends DataGetter {

    public DefaultDataGetter() {
        super();
    }

    @Override
    public String getPlayerFullName(Player player) {
        return player.getName();
    }

    @Override
    public String getPlayerName(Player player) {
        return player.getName();
    }

    @Override
    public String getPlayerPrefix(Player player) {
        return "";
    }

    @Override
    public String getPlayerSuffix(Player player) {
        return "";
    }

}
