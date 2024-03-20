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

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.william278.huskchat.VelocityHuskChat;
import net.william278.huskchat.user.ConsoleUser;
import net.william278.huskchat.user.VelocityUser;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class VelocityCommand implements SimpleCommand {

    private final VelocityHuskChat plugin;
    private final CommandBase command;

    public VelocityCommand(@NotNull CommandBase command, @NotNull VelocityHuskChat plugin) {
        this.command = command;
        this.plugin = plugin;

        // Register command
        plugin.getProxyServer().getCommandManager().register(
                command.getName(),
                this,
                command.getAliases().toArray(new String[0])
        );
    }

    @Override
    public void execute(@NotNull Invocation invocation) {
        if (invocation.source() instanceof Player player) {
            command.onExecute(VelocityUser.adapt(player, plugin), invocation.arguments());
        } else {
            command.onExecute(ConsoleUser.wrap(plugin), invocation.arguments());
        }
    }

    @Override
    public List<String> suggest(@NotNull Invocation invocation) {
        if (!(invocation.source() instanceof Player player)) {
            return command.onTabComplete(plugin.getConsoleUser(), invocation.arguments());
        }
        final VelocityUser user = VelocityUser.adapt(player, plugin);
        if (!user.hasPermission(command.getPermission(), !command.isOperatorOnly())) {
            return List.of();
        }
        return command.onTabComplete(user, invocation.arguments());
    }

    @Override
    public boolean hasPermission(@NotNull Invocation invocation) {
        if (invocation.source() instanceof Player player) {
            return VelocityUser.adapt(player, plugin).hasPermission(command.getPermission(), !command.isOperatorOnly());
        }
        return true;
    }

    public enum Type {
        HUSKCHAT((plugin) -> Optional.of(new VelocityCommand(new HuskChatCommand(plugin), plugin))),
        CHANNEL((plugin) -> Optional.of(new VelocityCommand(new ChannelCommand(plugin), plugin))),
        MESSAGE((plugin) -> plugin.getSettings().getMessageCommand().isEnabled()
                ? Optional.of(new VelocityCommand(new MessageCommand(plugin), plugin)) : Optional.empty()),
        REPLY((plugin) -> plugin.getSettings().getMessageCommand().isEnabled()
                ? Optional.of(new VelocityCommand(new ReplyCommand(plugin), plugin)) : Optional.empty()),
        OPT_OUT_MESSAGE((plugin) -> plugin.getSettings().getMessageCommand().isEnabled()
                ? Optional.of(new VelocityCommand(new OptOutMessageCommand(plugin), plugin)) : Optional.empty()),
        BROADCAST((plugin) -> plugin.getSettings().getBroadcastCommand().isEnabled()
                ? Optional.of(new VelocityCommand(new BroadcastCommand(plugin), plugin)) : Optional.empty()),
        SOCIAL_SPY((plugin) -> plugin.getSettings().getSocialSpy().isEnabled()
                ? Optional.of(new VelocityCommand(new SocialSpyCommand(plugin), plugin)) : Optional.empty()),
        LOCAL_SPY((plugin) -> plugin.getSettings().getLocalSpy().isEnabled()
                ? Optional.of(new VelocityCommand(new LocalSpyCommand(plugin), plugin)) : Optional.empty());

        private final Function<VelocityHuskChat, Optional<VelocityCommand>> commandSupplier;

        Type(@NotNull Function<VelocityHuskChat, Optional<VelocityCommand>> commandSupplier) {
            this.commandSupplier = commandSupplier;
        }

        private void register(@NotNull VelocityHuskChat plugin) {
            commandSupplier.apply(plugin);
        }

        public static void registerAll(@NotNull VelocityHuskChat plugin) {
            Arrays.stream(values()).forEach(type -> type.register(plugin));
        }

    }

}