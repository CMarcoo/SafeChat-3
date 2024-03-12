/*
 * {{ SafeChat }}
 * Copyright (C) 2024 CMarco
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package top.cmarco.safechat.api.events;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import top.cmarco.safechat.api.checks.ChatCheck;
import top.cmarco.safechat.api.checks.Check;

import java.util.Objects;

/**
 * An abstract event for chat checks.
 * This event is called on the minecraft server main thread.
 */
public abstract class ChatCheckEvent extends Event {

    private final Check check;

    /**
     * This constructor is used to explicitly declare an event as synchronous
     * or asynchronous.
     */
    public ChatCheckEvent(@NotNull Check check, boolean async) {
        super(async);
        this.check = Objects.requireNonNull(check);
    }

    /**
     * Return the {@link ChatCheck} used in the event.
     *
     * @return The chat check.
     */
    @NotNull
    public final Check getCheck() {
        return check;
    }
}
