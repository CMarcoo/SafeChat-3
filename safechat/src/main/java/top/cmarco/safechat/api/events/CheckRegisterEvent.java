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

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import top.cmarco.safechat.api.checks.Check;

/**
 * This event is called when a check has been registered.
 */
public class CheckRegisterEvent extends ChatCheckEvent {

    public static final HandlerList handlerList = new HandlerList();

    /**
     * This constructor is used to explicitly declare an event as synchronous
     * or asynchronous.
     *
     * @param check the check.
     */
    public CheckRegisterEvent(@NotNull Check check) {
        super(check, false);
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public final @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
