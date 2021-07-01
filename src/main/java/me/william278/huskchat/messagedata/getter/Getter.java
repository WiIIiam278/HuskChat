package me.william278.huskchat.messagedata.getter;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public abstract class Getter {

    public Getter() {
    }

    public abstract String getPlayerFullName(ProxiedPlayer player);

    public abstract String getPlayerName(ProxiedPlayer player);

    public abstract String getPlayerPrefix(ProxiedPlayer player);

    public abstract String getPlayerSuffix(ProxiedPlayer player);

}
