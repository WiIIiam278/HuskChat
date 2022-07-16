package net.william278.huskchat;

import net.william278.huskchat.discord.WebhookDispatcher;
import net.william278.huskchat.event.EventDispatcher;
import net.william278.huskchat.getter.DataGetter;
import net.william278.huskchat.message.MessageManager;
import net.william278.huskchat.player.Player;
import net.william278.huskchat.util.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface HuskChat {

    @NotNull
    MessageManager getMessageManager();

    @NotNull
    EventDispatcher getEventDispatcher();

    Optional<WebhookDispatcher> getWebhookDispatcher();

    void reloadSettings();

    void reloadMessages();

    @NotNull
    String getMetaVersion();

    @NotNull
    String getMetaDescription();

    @NotNull
    String getMetaPlatform();

    DataGetter getDataGetter();

    Optional<Player> getPlayer(UUID uuid);

    Collection<Player> getOnlinePlayers();

    Collection<Player> getOnlinePlayersOnServer(Player player);

    @NotNull
    Logger getLoggingAdapter();

    Optional<Player> matchPlayer(String username);

}
