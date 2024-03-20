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

package net.william278.huskchat.filter;

import net.william278.huskchat.HuskChat;
import net.william278.huskchat.channel.Channel;
import net.william278.huskchat.config.Filters;
import net.william278.huskchat.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public interface FilterProvider {

    List<ChatFilter> getFiltersAndReplacers();

    default void loadFilters() {
        final Filters settings = getPlugin().getFilterSettings();
        settings.getFilters().entrySet().stream()
                .filter(entry -> entry.getValue().isEnabled())
                .forEach(entry -> {
                    final ChatFilter.Type type = entry.getKey();
                    final ChatFilter.FilterSettings filterSettings = entry.getValue();
                    getFiltersAndReplacers().add(type.getCreator().apply(filterSettings));
                    getPlugin().log(Level.INFO, "Loaded %s filter".formatted(type.name()));
                });
    }

    default Optional<String> filter(@NotNull OnlineUser sender, @NotNull String message,
                                    @NotNull List<ChatFilter> filters) {
        boolean bypass = sender.hasPermission("huskchat.bypass_filters", false);
        final StringBuilder filtered = new StringBuilder(message);
        for (ChatFilter filter : filters) {
            if (sender.hasPermission(filter.getIgnorePermission(), false)) {
                continue;
            }
            if (filter instanceof ChatFilter.ReplacerFilter replacer) {
                filtered.replace(0, filtered.length(), replacer.replace(filtered.toString()));
            }
            if (!bypass && !filter.isAllowed(sender, message)) {
                getPlugin().getLocales().sendMessage(sender, filter.getDisallowedLocale());
                return Optional.empty();
            }
        }
        return Optional.of(filtered.toString());
    }

    default List<ChatFilter> getChannelFilters(@NotNull Channel channel) {
        return getFiltersAndReplacers().stream()
                .filter(filter -> filter.getSettings().getChannels().contains(channel.getId()))
                .toList();
    }

    default List<ChatFilter> getMessageFilters() {
        return getFiltersAndReplacers().stream()
                .filter(filter -> filter.getSettings().isPrivateMessages())
                .toList();
    }

    default List<ChatFilter> getBroadcastFilters() {
        return getFiltersAndReplacers().stream()
                .filter(filter -> filter.getSettings().isBroadcastMessages())
                .toList();
    }


    @NotNull
    HuskChat getPlugin();


}
