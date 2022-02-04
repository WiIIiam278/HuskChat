package net.william278.huskchat;

import net.william278.huskchat.getter.DataGetter;
import net.william278.huskchat.util.Logger;
import net.william278.huskchat.message.MessageManager;
import net.william278.huskchat.player.Player;

import java.util.Collection;
import java.util.UUID;

public interface HuskChat {

    MessageManager getMessageManager();

    void reloadSettings();

    void reloadMessages();

    String getMetaVersion();

    String getMetaDescription();

    String getMetaPlatform();

    DataGetter getDataGetter();

    Player getPlayer(UUID uuid);

    Collection<Player> getOnlinePlayers();

    Collection<Player> getOnlinePlayersOnServer(Player player);

    Logger getLoggingAdapter();

}
