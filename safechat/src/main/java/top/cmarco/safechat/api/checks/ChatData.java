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

package top.cmarco.safechat.api.checks;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChatData {

    private final Player player;
    private final String message;
    private final long sentAt;

    public ChatData(@NotNull Player player, @NotNull String message, long sentAt) {
        this.player = player;
        this.message = message;
        this.sentAt = sentAt;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public String getMessage() {
        return message;
    }

    public long getSentAt() {
        return sentAt;
    }
}
