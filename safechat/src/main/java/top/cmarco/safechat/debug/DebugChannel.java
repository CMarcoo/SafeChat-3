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

package top.cmarco.safechat.debug;

import org.jetbrains.annotations.NotNull;

public enum DebugChannel {
    HIBERNATE("safechat:hibernate"),
    COMMAND("safechat:command"),
    CHAT("safechat:chat"),
    CONFIG("safechat:config"),
    DEBUGGER("safechat:debugger");

    private final String channelName;

    DebugChannel(String channelName) {
        this.channelName = channelName;
    }

    @NotNull
    public final String getChannelName() {
        return channelName;
    }
}
