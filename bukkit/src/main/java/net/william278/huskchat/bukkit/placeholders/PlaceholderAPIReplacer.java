package net.william278.huskchat.bukkit.placeholders;

import me.clip.placeholderapi.PlaceholderAPI;
import net.william278.huskchat.bukkit.player.BukkitPlayer;
import net.william278.huskchat.placeholders.PlaceholderReplacer;
import net.william278.huskchat.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class PlaceholderAPIReplacer implements PlaceholderReplacer {

    @Override
    public CompletableFuture<String> formatPlaceholders(@NotNull String message, @NotNull Player player) {
        return CompletableFuture.completedFuture(PlaceholderAPI.setPlaceholders(
                ((BukkitPlayer) player).getBukkitPlayer(),
                message
        ));
    }

}
