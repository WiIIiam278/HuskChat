package me.william278.huskchat.messagedata.getter;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedDataManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class LuckPermsDataGetter extends Getter {

    private final LuckPerms api;

    public LuckPermsDataGetter() {
        super();
        api = LuckPermsProvider.get();
    }

    @Override
    public String getPlayerFullName(ProxiedPlayer player) {
        CachedDataManager cachedData = api.getUserManager().getUser(player.getUniqueId()).getCachedData();
        StringBuilder fullName = new StringBuilder();

        final String prefix = cachedData.getMetaData().getPrefix();
        if (prefix != null) {
            fullName.append(prefix);
        }
        fullName.append(player.getName());
        final String suffix = cachedData.getMetaData().getSuffix();
        if (suffix != null) {
            fullName.append(suffix);
        }
        return fullName.toString();
    }

    @Override
    public String getPlayerName(ProxiedPlayer player) {
        return player.getName();
    }

    @Override
    public String getPlayerPrefix(ProxiedPlayer player) {
        CachedDataManager cachedData = api.getUserManager().getUser(player.getUniqueId()).getCachedData();

        final String prefix = cachedData.getMetaData().getPrefix();
        if (prefix != null) {
            return prefix;
        }
        return "";
    }

    @Override
    public String getPlayerSuffix(ProxiedPlayer player) {
        CachedDataManager cachedData = api.getUserManager().getUser(player.getUniqueId()).getCachedData();

        final String suffix = cachedData.getMetaData().getSuffix();
        if (suffix != null) {
            return suffix;
        }
        return "";
    }
}
