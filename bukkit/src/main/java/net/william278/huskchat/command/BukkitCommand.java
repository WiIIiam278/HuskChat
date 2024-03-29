/*
 * This file is part of HuskChat, licensed under the Apache License 2.0.
 *
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.william278.huskchat.command;

import net.william278.huskchat.BukkitHuskChat;
import net.william278.huskchat.user.BukkitUser;
import net.william278.huskchat.user.ConsoleUser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class BukkitCommand extends Command {

    private final BukkitHuskChat plugin;
    private final CommandBase command;

    public BukkitCommand(@NotNull CommandBase command, @NotNull BukkitHuskChat plugin) {
        super(command.getName(), command.getUsage(), command.getUsage(), command.getAliases());
        this.setPermission(command.getPermission());
        this.command = command;
        this.plugin = plugin;

        // Register command & permission with Bukkit
        plugin.getCommandMap().register("huskchat", this);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (sender instanceof Player player) {
            command.onExecute(BukkitUser.adapt(player, plugin), args);
        } else {
            command.onExecute(ConsoleUser.wrap(plugin), args);
        }
        return true;
    }

    @NotNull
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias,
                                    @NotNull String[] args) throws IllegalArgumentException {
        if (!(sender instanceof Player player)) {
            return command.onTabComplete(plugin.getConsoleUser(), args);
        }
        final BukkitUser user = BukkitUser.adapt(player, plugin);
        if (!user.hasPermission(command.getPermission(), !command.isOperatorOnly())) {
            return List.of();
        }
        return command.onTabComplete(user, args);
    }

    @Override
    public boolean testPermissionSilent(@NotNull CommandSender target) {
        if (target instanceof Player player) {
            return BukkitUser.adapt(player, plugin).hasPermission(command.getPermission(), !command.isOperatorOnly());
        }
        return true;
    }

    public enum Type {
        HUSKCHAT((plugin) -> Optional.of(new BukkitCommand(new HuskChatCommand(plugin), plugin))),
        CHANNEL((plugin) -> Optional.of(new BukkitCommand(new ChannelCommand(plugin), plugin))),
        MESSAGE((plugin) -> plugin.getSettings().getMessageCommand().isEnabled()
                ? Optional.of(new BukkitCommand(new MessageCommand(plugin), plugin)) : Optional.empty()),
        REPLY((plugin) -> plugin.getSettings().getMessageCommand().isEnabled()
                ? Optional.of(new BukkitCommand(new ReplyCommand(plugin), plugin)) : Optional.empty()),
        OPT_OUT_MESSAGE((plugin) -> plugin.getSettings().getMessageCommand().isEnabled()
                ? Optional.of(new BukkitCommand(new OptOutMessageCommand(plugin), plugin)) : Optional.empty()),
        BROADCAST((plugin) -> plugin.getSettings().getBroadcastCommand().isEnabled()
                ? Optional.of(new BukkitCommand(new BroadcastCommand(plugin), plugin)) : Optional.empty()),
        SOCIAL_SPY((plugin) -> plugin.getSettings().getSocialSpy().isEnabled()
                ? Optional.of(new BukkitCommand(new SocialSpyCommand(plugin), plugin)) : Optional.empty());

        private final Function<BukkitHuskChat, Optional<BukkitCommand>> commandSupplier;

        Type(@NotNull Function<BukkitHuskChat, Optional<BukkitCommand>> commandSupplier) {
            this.commandSupplier = commandSupplier;
        }

        private void register(@NotNull BukkitHuskChat plugin) {
            commandSupplier.apply(plugin);
        }

        public static void registerAll(@NotNull BukkitHuskChat plugin) {
            Arrays.stream(values()).forEach(type -> type.register(plugin));
        }

    }

}
