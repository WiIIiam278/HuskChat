package net.william278.huskchat.placeholderparser;

import net.william278.huskchat.player.Player;
import net.william278.papiproxybridge.api.PlaceholderAPI;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PAPIProxyBridgeParser implements Placeholders {
    @Override
    public CompletableFuture<String> parsePlaceholders(String stringToParse, Player player) {
        final PlaceholderAPI api = PlaceholderAPI.getInstance();
        final UUID uuid = player.getUuid();
        return api.formatPlaceholders(stringToParse, uuid).toCompletableFuture();
    }
}
