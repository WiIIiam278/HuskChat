package net.william278.huskchat;

import net.william278.huskchat.event.EventDispatcher;
import net.william278.huskchat.getter.DataGetter;
import net.william278.huskchat.message.MessageManager;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.util.Logger;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface HuskChat {

    MessageManager getMessageManager();

    EventDispatcher getEventDispatcher();

    void reloadSettings();

    void reloadMessages();

    String getMetaVersion();

    String getMetaDescription();

    String getMetaPlatform();

    DataGetter getDataGetter();

    Optional<Player> getPlayer(UUID uuid);

    Collection<Player> getOnlinePlayers();

    Collection<Player> getOnlinePlayersOnServer(Player player);

    Logger getLoggingAdapter();

    Optional<Player> matchPlayer(String username);

}
