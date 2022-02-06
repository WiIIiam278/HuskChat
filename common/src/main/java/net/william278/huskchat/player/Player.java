package net.william278.huskchat.player;

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
     * Have the player passthrough a chat message to the backend server
     *
     * @param message The message to pass to the backend server
     */
    void passthroughChat(String message);
}
