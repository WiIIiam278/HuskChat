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

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.william278.huskchat.BungeeHuskChat;
import net.william278.huskchat.user.BungeeUser;
import net.william278.huskchat.user.ConsoleUser;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class BungeeCommand extends Command implements TabExecutor {

    private final BungeeHuskChat plugin;
    private final CommandBase command;

    public BungeeCommand(@NotNull CommandBase command, @NotNull BungeeHuskChat plugin) {
        super(command.getName(), command.getPermission(), command.getAliases().toArray(new String[0]));
        this.command = command;
        this.plugin = plugin;

        // Register command
        plugin.getProxy().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer player) {
            final BungeeUser user = BungeeUser.adapt(player, plugin);
            if (!user.hasPermission(command.getPermission(), !command.isOperatorOnly())) {
                plugin.getLocales().sendMessage(user, "error_no_permission");
                return;
            }
            command.onExecute(user, args);
        } else {
            command.onExecute(ConsoleUser.wrap(plugin), args);
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer player)) {
            return command.onTabComplete(plugin.getConsoleUser(), args);
        }
        final BungeeUser user = BungeeUser.adapt(player, plugin);
        if (!user.hasPermission(command.getPermission(), !command.isOperatorOnly())) {
            return List.of();
        }
        return command.onTabComplete(user, args);
    }

    public enum Type {
        HUSKCHAT((plugin) -> Optional.of(new BungeeCommand(new HuskChatCommand(plugin), plugin))),
        CHANNEL((plugin) -> Optional.of(new BungeeCommand(new ChannelCommand(plugin), plugin))),
        MESSAGE((plugin) -> plugin.getSettings().getMessageCommand().isEnabled()
                ? Optional.of(new BungeeCommand(new MessageCommand(plugin), plugin)) : Optional.empty()),
        REPLY((plugin) -> plugin.getSettings().getMessageCommand().isEnabled()
                ? Optional.of(new BungeeCommand(new ReplyCommand(plugin), plugin)) : Optional.empty()),
        OPT_OUT_MESSAGE((plugin) -> plugin.getSettings().getMessageCommand().isEnabled()
                ? Optional.of(new BungeeCommand(new OptOutMessageCommand(plugin), plugin)) : Optional.empty()),
        BROADCAST((plugin) -> plugin.getSettings().getBroadcastCommand().isEnabled()
                ? Optional.of(new BungeeCommand(new BroadcastCommand(plugin), plugin)) : Optional.empty()),
        SOCIAL_SPY((plugin) -> plugin.getSettings().getSocialSpy().isEnabled()
                ? Optional.of(new BungeeCommand(new SocialSpyCommand(plugin), plugin)) : Optional.empty()),
        LOCAL_SPY((plugin) -> plugin.getSettings().getLocalSpy().isEnabled()
                ? Optional.of(new BungeeCommand(new LocalSpyCommand(plugin), plugin)) : Optional.empty());

        private final Function<BungeeHuskChat, Optional<BungeeCommand>> commandSupplier;

        Type(@NotNull Function<BungeeHuskChat, Optional<BungeeCommand>> commandSupplier) {
            this.commandSupplier = commandSupplier;
        }

        private void register(@NotNull BungeeHuskChat plugin) {
            commandSupplier.apply(plugin);
        }

        public static void registerAll(@NotNull BungeeHuskChat plugin) {
            Arrays.stream(values()).forEach(type -> type.register(plugin));
        }

    }

}
