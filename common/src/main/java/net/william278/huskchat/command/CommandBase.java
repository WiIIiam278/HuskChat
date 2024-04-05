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

import lombok.Getter;
import net.william278.huskchat.HuskChat;
import net.william278.huskchat.user.OnlineUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

/**
 * Abstract, cross-platform representation of a plugin command
 */
public abstract class CommandBase {

    protected final HuskChat plugin;
    protected final List<String> aliases;
    protected final String usage;
    @Getter
    protected boolean operatorOnly = false;

    public CommandBase(@NotNull List<String> aliases, @NotNull String usage, @NotNull HuskChat plugin) {
        if (aliases.isEmpty()) {
            throw new IllegalArgumentException("Command must have at least one alias");
        }
        this.aliases = aliases.stream().map(s -> s.toLowerCase(Locale.ENGLISH)).toList();
        this.usage = usage;
        this.plugin = plugin;
    }

    /**
     * Fires when the command is executed
     *
     * @param player {@link OnlineUser} executing the command
     * @param args   Command arguments
     */
    public abstract void onExecute(@NotNull OnlineUser player, @NotNull String[] args);

    /**
     * What should be returned when the player attempts to TAB complete the command
     *
     * @param player {@link OnlineUser} doing the TAB completion
     * @param args   Current command arguments
     * @return List of String arguments to offer TAB suggestions
     */
    @NotNull
    public List<String> onTabComplete(@NotNull OnlineUser player, @NotNull String[] args) {
        return List.of();
    }

    /**
     * Get the primary command alias
     */
    @NotNull
    public String getName() {
        return aliases.get(0);
    }

    /**
     * Command aliases
     */
    @NotNull
    public List<String> getAliases() {
        return aliases.subList(1, aliases.size());
    }

    @NotNull
    public String getUsage() {
        return "/" + getName() + " " + usage;
    }

    /**
     * Command permission node
     */
    @Nullable
    public String getPermission(@NotNull String... children) {
        return String.join(".",
                "huskchat", "command",
                children.length == 0 ? getName() : getName() +
                        "." + String.join(".", children)
        );
    }

}
