package net.william278.huskchat.getter;

import net.william278.huskchat.player.Player;

import java.util.Optional;

/**
 * The default Data Getter if LuckPerms is not installed
 */
public class DefaultDataGetter extends DataGetter {

    public DefaultDataGetter() {
        super();
    }

    @Override
    public String getPlayerFullName(Player player) {
        return player.getName().replaceAll("__", "\\__");
    }

    @Override
    public String getPlayerName(Player player) {
        return player.getName().replaceAll("__", "\\__");
    }

    @Override
    public Optional<String> getPlayerPrefix(Player player) {
        return Optional.empty();
    }

    @Override
    public Optional<String> getPlayerSuffix(Player player) {
        return Optional.empty();
    }

}
