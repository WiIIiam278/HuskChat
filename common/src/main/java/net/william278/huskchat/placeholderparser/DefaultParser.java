package net.william278.huskchat.placeholderparser;

import net.william278.huskchat.player.Player;

import java.util.concurrent.CompletableFuture;

public class DefaultParser implements Placeholders {
    @Override
    public CompletableFuture<String> parsePlaceholders(String stringToParse, Player player) {
        return CompletableFuture.completedFuture(stringToParse);
    }
}
