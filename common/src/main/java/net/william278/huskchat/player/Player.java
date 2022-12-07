package net.william278.huskchat.player;

import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Abstract cross-platform Player object
 */
public interface Player {

    /**
     * Return the player's name
     *
     * @return the player's name
     */
    String getName();

    /**
     * Return the player's {@link UUID}
     *
     * @return the player {@link UUID}
     */
    UUID getUuid();

    /**
     * Return the player's ping
     *
     * @return the player's ping
     */
    int getPing();

    /**
     * Return the name of the server the player is connected to
     *
     * @return player's server name
     */
    String getServerName();

    /**
     * Return the number of people on that player's server
     *
     * @return player count on the player's server
     */
    int getPlayersOnServer();

    /**
     * Returns if the player has the permission node
     *
     * @param node The permission node string
     * @return {@code true} if the player has the node; {@code false} otherwise
     */
    boolean hasPermission(String node);

    /**
     * Get the audience for this player
     *
     * @return the audience for this player
     */
    @NotNull
    Audience getAudience();

    /**
     * Send a message to the player
     *
     * @param mineDown the message to send
     */
    default void sendMessage(@NotNull MineDown mineDown) {
        getAudience().sendMessage(mineDown.toComponent());
    }

    /**
     * Send a message to the player
     *
     * @param component the message to send
     */
    default void sendMessage(@NotNull Component component) {
        getAudience().sendMessage(component);
    }
}
