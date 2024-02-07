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

import net.william278.huskchat.user.OnlineUser;
import org.jetbrains.annotations.NotNull;

/**
 * An abstract representation of a chat filterer
 */
public abstract class ChatFilter {

    /**
     * Takes a user's message and returns true if the message passes the filter
     *
     * @param message The user's message
     * @return {@code true} if the filter allows the message to pass; {@code false} otherwise
     */
    public abstract boolean isAllowed(@NotNull OnlineUser sender, @NotNull String message);

    /**
     * The ID of the locale to send the player if their message fails the filter
     *
     * @return the failure message ID
     */
    @NotNull
    public abstract String getFailureErrorMessageId();

    /**
     * The permission node users can have to bypass this filter
     *
     * @return filter bypass permission node
     */
    @NotNull
    public abstract String getFilterIgnorePermission();

}
