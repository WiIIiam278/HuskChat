package net.william278.huskchat.player;

import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TestPlayer implements Player {

    private final static int TEST_PLAYER_PING = 5;
    private final static String TEST_PLAYER_SERVER = "test";
    private final static int TEST_PLAYER_SERVER_PLAYER_COUNT = 1;

    private final UUID uuid;
    private final String name;

    /**
     * Implementation of a {@link Player} for unit testing
     */
    public TestPlayer() {
        this.uuid = UUID.randomUUID();
        this.name = UUID.randomUUID().toString().split("-")[0];
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public int getPing() {
        return TEST_PLAYER_PING;
    }

    @Override
    public String getServerName() {
        return TEST_PLAYER_SERVER;
    }

    @Override
    public int getPlayersOnServer() {
        return TEST_PLAYER_SERVER_PLAYER_COUNT;
    }

    @Override
    public boolean hasPermission(String node) {
        return true;
    }

    @NotNull
    @Override
    public Audience getAudience() {
        return Audience.empty();
    }
}
