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

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import top.cmarco.safechat.api.checks.ChatData;
import top.cmarco.safechat.api.checks.Check;

/**
 * This check is called when a player has failed one of the loaded chat checks.
 * This check is cancellable: doing so will make the player pass correctly the check.
 * This event is not synchronized with the minecraft server's main thread.
 */
public class PlayerFailCheckEvent extends ChatCheckEvent implements Cancellable {

    public final static HandlerList handlerList = new HandlerList();

    private final ChatData chatData;

    private boolean cancelled = false;

    public PlayerFailCheckEvent(@NotNull Check check, @NotNull ChatData chatData) {
        super(check, true);
        this.chatData = chatData;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     *
     * @return true if this event is cancelled
     */
    @Override
    public final boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins.
     *
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public final void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    /**
     * Get the data of this event.
     *
     * @return The data of the event.
     */
    @NotNull
    public ChatData getChatData() {
        return chatData;
    }

    @Override
    public final @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
