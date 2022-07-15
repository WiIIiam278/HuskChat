package net.william278.huskchat.player;

import net.william278.huskchat.HuskChat;

import java.util.UUID;
import java.util.logging.Level;

public class ConsolePlayer implements Player {

    private static final UUID consoleUUID = new UUID(0, 0);
    private static final String consoleUsername = "[CONSOLE]";
    private HuskChat implementor;

    private ConsolePlayer() {
    }

    @Override
    public String getName() {
        return consoleUsername;
    }

    @Override
    public UUID getUuid() {
        return consoleUUID;
    }

    @Override
    public int getPing() {
        return 0;
    }

    @Override
    public String getServerName() {
        return implementor.getMetaPlatform();
    }

    @Override
    public int getPlayersOnServer() {
        return implementor.getOnlinePlayers().size();
    }

    @Override
    public boolean hasPermission(String node) {
        return true;
    }

    /**
     * Adapt the proxy console player into a cross-platform one
     *
     * @param plugin The implementing HuskChat plugin
     * @return The ConsolePlayer
     */
    public static ConsolePlayer adaptConsolePlayer(HuskChat plugin) {
        ConsolePlayer consolePlayer = new ConsolePlayer();
        consolePlayer.implementor = plugin;
        return consolePlayer;
    }

    /**
     * Returns true if the UUID is that of the console player
     *
     * @param uuid UUID to check
     * @return {@code true} if the UUID is the console
     */
    public static boolean isConsolePlayer(UUID uuid) {
        return uuid.equals(consoleUUID);
    }

    /**
     * Returns true if the username is that of the console player
     *
     * @param username username to check
     * @return {@code true} if the username is the console
     */
    public static boolean isConsolePlayer(String username) {
        return username.equals(consoleUsername);
    }
}
