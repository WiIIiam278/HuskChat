package me.william278.huskchat.messagedata.getter;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class RegularDataGetter extends Getter {

    public RegularDataGetter() {
        super();
    }

    @Override
    public String getPlayerFullName(ProxiedPlayer player) {
        return player.getDisplayName();
    }

    @Override
    public String getPlayerName(ProxiedPlayer player) {
        return player.getName();
    }

    @Override
    public String getPlayerPrefix(ProxiedPlayer player) {
        return "";
    }

    @Override
    public String getPlayerSuffix(ProxiedPlayer player) {
        return "";
    }
}
